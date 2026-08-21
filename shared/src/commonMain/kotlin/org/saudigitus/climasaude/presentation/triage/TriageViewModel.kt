package org.saudigitus.climasaude.presentation.triage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.repository.ChildRepository
import org.saudigitus.climasaude.domain.repository.SessionRepository
import org.saudigitus.climasaude.domain.repository.TriageRepository

class TriageViewModel(
    private val sessionRepository: SessionRepository,
    private val childRepository: ChildRepository,
    private val triageRepository: TriageRepository
) : ViewModel() {
    private val mutableState = MutableStateFlow(TriageUiState())
    val state: StateFlow<TriageUiState> = mutableState
    private val events = Channel<TriageEvent>(Channel.BUFFERED)
    val navigation: Flow<TriageEvent> = events.receiveAsFlow()

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
        community: String,
        areaId: String?
    ) {
        val age = ageText.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
        if (name.trim()
                .isEmpty() || name.length > 80 || caregiver.length > 80 || community.length > 80 ||
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
                    community.takeIf { it.isNotBlank() },
                    areaId
                )
            }
                .onSuccess { events.send(TriageEvent.ChildSaved(it)) }
                .onFailure {
                    mutableState.value =
                        mutableState.value.copy(error = "Não foi possível guardar a criança.")
                }
            mutableState.value = mutableState.value.copy(busy = false)
        }
    }

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
                }
                .onFailure {
                    mutableState.value =
                        mutableState.value.copy(error = "Não foi possível guardar a triagem.")
                }
            mutableState.value = mutableState.value.copy(busy = false)
        }
    }

    fun translateRecommendation(language: AppLanguage) {
        val triage = mutableState.value.selectedTriage ?: return
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
        mutableState.value = TriageUiState()
    }
}
