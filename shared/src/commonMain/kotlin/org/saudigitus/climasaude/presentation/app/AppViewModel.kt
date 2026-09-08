package org.saudigitus.climasaude.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.saudigitus.climasaude.domain.error.AppError
import org.saudigitus.climasaude.domain.error.AppFailure
import org.saudigitus.climasaude.domain.guidance.GuidanceEngine
import org.saudigitus.climasaude.domain.model.Alert
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.SyncKind
import org.saudigitus.climasaude.domain.repository.AlertRepository
import org.saudigitus.climasaude.domain.repository.SessionRepository
import org.saudigitus.climasaude.domain.sync.SyncCoordinator

class AppViewModel(
    private val sessionRepository: SessionRepository,
    private val alertRepository: AlertRepository,
    private val guidanceEngine: GuidanceEngine,
    private val syncCoordinator: SyncCoordinator
) : ViewModel() {
    private val mutableState = MutableStateFlow(AppUiState())
    val state: StateFlow<AppUiState> = mutableState

    init {
        viewModelScope.launch {
            sessionRepository.profile.collectLatest { profile ->
                mutableState.value = mutableState.value.copy(profile = profile, loading = false)
                if (profile?.demo == true) alertRepository.refresh()
            }
        }
        viewModelScope.launch {
            alertRepository.alerts.collect { alerts ->
                mutableState.value = mutableState.value.copy(alerts = alerts)
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(busy = true, message = null)
            runCatching { sessionRepository.login(username, password) }
                .onSuccess { synced ->
                    if (!synced) mutableState.value =
                        mutableState.value.copy(message = AppError.CONNECTION)
                }
                .onFailure {
                    mutableState.value = mutableState.value.copy(
                        message = (it as? AppFailure)?.code ?: AppError.CONNECTION
                    )
                }
            mutableState.value = mutableState.value.copy(busy = false)
        }
    }

    fun enterDemo() {
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(busy = true, message = null)
            runCatching { sessionRepository.enterDemo() }
                .onFailure {
                    mutableState.value = mutableState.value.copy(message = AppError.CONNECTION)
                }
            mutableState.value = mutableState.value.copy(busy = false)
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(syncing = true, syncStatus = null)
            runCatching { syncCoordinator.sync() }
                .onSuccess { outcome ->
                    val status = when (outcome.kind) {
                        SyncKind.NO_SESSION -> "Entre na sua conta para enviar os dados."
                        SyncKind.DEMO -> "Modo de demonstração: os dados ficam neste telemóvel."
                        SyncKind.NOT_CONFIGURED -> "Envio ainda não disponível. Os dados ficam guardados aqui."
                        SyncKind.COMPLETE -> "Tudo enviado."
                    }
                    mutableState.value = mutableState.value.copy(syncStatus = status)
                }
                .onFailure {
                    mutableState.value = mutableState.value.copy(
                        syncStatus =
                            if ((it as? AppFailure)?.code == AppError.SIGN_IN_AGAIN) "A sessão expirou. Entre outra vez para enviar."
                            else "Sem ligação. Os dados ficam guardados e são enviados depois."
                    )
                }
            mutableState.value = mutableState.value.copy(syncing = false)
            val shownStatus = mutableState.value.syncStatus
            delay(8_000)
            if (mutableState.value.syncStatus == shownStatus) {
                mutableState.value = mutableState.value.copy(syncStatus = null)
            }
        }
    }

    fun select(alert: Alert) {
        if (mutableState.value.selected?.id == alert.id) return
        mutableState.value = mutableState.value.copy(
            selected = alert,
            translationLanguage = AppLanguage.PORTUGUESE,
            guidance = null
        )
        viewModelScope.launch {
            val guidance = guidanceEngine.forAlert(alert, AppLanguage.PORTUGUESE)
            if (mutableState.value.selected?.id == alert.id) mutableState.value =
                mutableState.value.copy(guidance = guidance)
        }
    }

    fun translate(language: AppLanguage) {
        val alert = mutableState.value.selected ?: return
        mutableState.value =
            mutableState.value.copy(translationLanguage = language, guidance = null)
        viewModelScope.launch {
            val guidance = guidanceEngine.forAlert(alert, language)
            if (mutableState.value.selected?.id == alert.id && mutableState.value.translationLanguage == language) {
                mutableState.value = mutableState.value.copy(guidance = guidance)
            }
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { sessionRepository.setLanguage(language) }
    }

    fun dismissMessage() {
        mutableState.value = mutableState.value.copy(message = null)
    }

    fun logout() {
        viewModelScope.launch {
            sessionRepository.logout()
            mutableState.value = AppUiState(loading = false)
        }
    }
}
