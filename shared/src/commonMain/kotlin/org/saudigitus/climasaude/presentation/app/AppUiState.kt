package org.saudigitus.climasaude.presentation.app

import org.saudigitus.climasaude.domain.error.AppError
import org.saudigitus.climasaude.domain.model.Alert
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.domain.model.Guidance
import org.saudigitus.climasaude.domain.model.UserProfile

data class AppUiState(
    val loading: Boolean = true,
    val profile: UserProfile? = null,
    val alerts: List<Alert> = emptyList(),
    val selected: Alert? = null,
    val translationLanguage: AppLanguage = AppLanguage.PORTUGUESE,
    val guidance: Guidance? = null,
    val busy: Boolean = false,
    val message: AppError? = null,
    val syncing: Boolean = false,
    val syncStatus: String? = null
)
