package org.saudigitus.climasaude.presentation.triage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.GeoPoint
import org.saudigitus.climasaude.domain.repository.ChildRepository
import org.saudigitus.climasaude.domain.repository.SessionRepository
import org.saudigitus.climasaude.domain.repository.TriageRepository
import org.saudigitus.climasaude.platform.LocationProvider
import kotlin.time.Duration.Companion.seconds

class TriageViewModel(
    private val sessionRepository: SessionRepository,
    private val childRepository: ChildRepository,
    private val triageRepository: TriageRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {
    private val mutableState = MutableStateFlow(TriageUiState())
    val state: StateFlow<TriageUiState> = mutableState
    private val events = Channel<TriageEvent>(Channel.BUFFERED)
    val navigation: Flow<TriageEvent> = events.receiveAsFlow()
    private var locationJob: Job? = null

    init {
        viewModelScope.launch {
            sessionRepository.areas.collect { areas ->
                mutableState.value = mutableState.value.copy(areas = areas)
            }
        }
        viewModelScope.launch {
            triageRepository.followUps.collect { children ->
                mutableState.value = mutableState.value.copy(children = children)
            }
        }
        viewModelScope.launch {
            triageRepository.pendingCount.collect { count ->
                mutableState.value = mutableState.value.copy(pendingCount = count)
            }
        }
    }

    fun updateSearch(value: String) {
        mutableState.value = mutableState.value.copy(search = value)
    }

    fun clearError() {
        mutableState.value = mutableState.value.copy(error = null)
    }

    /** Selects the triage whose recommendation is shown; resets the language when it changes. */
    fun showTriage(id: String) {
        if (mutableState.value.selectedTriageId == id) return
        mutableState.value = mutableState.value.copy(
            selectedTriageId = id, recommendationLanguage = AppLanguage.PORTUGUESE,
            translating = false, translationError = null
        )
    }

    fun addChild(
        name: String,
        ageText: String,
        sex: String?,
        caregiver: String,
        areaId: String?
    ) {
        val age = ageText.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
        if (name.trim()
                .isEmpty() || name.length > 80 || caregiver.length > 80 ||
            (ageText.isNotBlank() && (age == null || age !in 0..17))
        ) {
            mutableState.value =
                mutableState.value.copy(error = "Confirme o nome e a idade (até 17 anos).")
            return
        }
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(busy = true, error = null)
            runCatching {
                childRepository.addChild(
                    name,
                    age,
                    sex,
                    caregiver.takeIf { it.isNotBlank() },
                    mutableState.value.childLocation,
                    areaId
                )
            }
                .onSuccess {
                    clearLocation()
                    events.send(TriageEvent.ChildSaved(it))
                }
                .onFailure {
                    mutableState.value =
                        mutableState.value.copy(error = "Não foi possível guardar a criança.")
                }
            mutableState.value = mutableState.value.copy(busy = false)
        }
    }

    fun startLocation(askPermission: Boolean) {
        if (locationJob?.isActive == true) return
        locationJob = viewModelScope.launch {
            if (!locationProvider.hasPermission() &&
                !(askPermission && locationProvider.requestPermission())
            ) {
                mutableState.update {
                    it.copy(
                        locationPermissionNeeded = true,
                        locationError = if (askPermission) "Permita o acesso à localização para recolher as coordenadas." else null
                    )
                }
                return@launch
            }
            if (!locationProvider.isEnabled()) {
                mutableState.update {
                    it.copy(
                        locationPermissionNeeded = false,
                        locationError = "Ligue a localização (GPS) do telemóvel e tente de novo."
                    )
                }
                return@launch
            }
            mutableState.update {
                it.copy(
                    locating = true,
                    locationPermissionNeeded = false,
                    locationError = null,
                    childLocation = null
                )
            }
            try {
                withTimeoutOrNull(LOCATION_TIMEOUT) {
                    locationProvider.updates()
                        .transformWhile { point ->
                            emit(point)
                            !point.isPrecise()
                        }
                        .collect { point ->
                            mutableState.update {
                                if (point.isBetterThan(it.childLocation)) it.copy(childLocation = point) else it
                            }
                        }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Throwable) {
            } finally {
                if (isActive) mutableState.update {
                    it.copy(
                        locating = false,
                        locationError = if (it.childLocation == null) "Não foi possível obter as coordenadas. Tente ao ar livre." else null
                    )
                }
            }
        }
    }

    fun stopLocation() {
        locationJob?.cancel()
        locationJob = null
        mutableState.update { it.copy(locating = false) }
    }

    fun clearLocation() {
        stopLocation()
        mutableState.update {
            it.copy(childLocation = null, locationError = null, locationPermissionNeeded = false)
        }
    }

    private fun GeoPoint.isPrecise() = (accuracyMeters ?: Double.MAX_VALUE) <= TARGET_ACCURACY_M

    private fun GeoPoint.isBetterThan(other: GeoPoint?) = other == null ||
        (accuracyMeters ?: Double.MAX_VALUE) <= (other.accuracyMeters ?: Double.MAX_VALUE)

    fun addTriage(
        childId: String,
        fever: Boolean,
        dangerSigns: Boolean,
        malariaTest: Boolean,
        referred: Boolean,
        notes: String
    ) {
        if (notes.length > 500) {
            mutableState.value =
                mutableState.value.copy(error = "As notas devem ter até 500 caracteres.")
            return
        }
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(busy = true, error = null)
            runCatching {
                triageRepository.addTriage(
                    childId,
                    fever,
                    dangerSigns,
                    malariaTest,
                    referred,
                    notes
                )
            }
                .onSuccess { triage ->
                    mutableState.value = mutableState.value.copy(
                        selectedTriageId = triage.id, justSavedTriage = triage,
                        recommendationLanguage = AppLanguage.PORTUGUESE, translationError = null
                    )
                    events.send(TriageEvent.TriageSaved(childId, triage.id))
                    refineRecommendation(triage.id)
                }
                .onFailure {
                    mutableState.value =
                        mutableState.value.copy(error = "Não foi possível guardar a triagem.")
                }
            mutableState.value = mutableState.value.copy(busy = false)
        }
    }

    private fun refineRecommendation(triageId: String) {
        mutableState.value = mutableState.value.copy(
            generatingIds = mutableState.value.generatingIds + triageId
        )
        viewModelScope.launch {
            try {
                runCatching {
                    triageRepository.refineRecommendation(triageId) { draft ->
                        mutableState.update { it.copy(drafts = it.drafts + (triageId to draft)) }
                    }
                }
            } finally {
                mutableState.update {
                    it.copy(generatingIds = it.generatingIds - triageId, drafts = it.drafts - triageId)
                }
            }
        }
    }

    fun translateRecommendation(language: AppLanguage) {
        val triage = mutableState.value.selectedTriage ?: return
        if (triage.id in mutableState.value.generatingIds) return
        mutableState.value = mutableState.value.copy(
            recommendationLanguage = language, translationError = null
        )
        if (language == AppLanguage.PORTUGUESE || triage.translations.containsKey(language)) return
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(translating = true)
            val success =
                runCatching { triageRepository.translate(triage.id, language) }.getOrDefault(false)
            mutableState.value = mutableState.value.copy(
                translating = false,
                translationError = if (success) null else "A tradução ainda não está disponível neste telemóvel."
            )
        }
    }

    fun reset() {
        stopLocation()
        mutableState.value = TriageUiState()
    }

    private companion object {
        val LOCATION_TIMEOUT = 90.seconds
        const val TARGET_ACCURACY_M = 7.0
    }
}
