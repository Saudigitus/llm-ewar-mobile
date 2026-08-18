package org.saudigitus.climasaude.domain.model

data class UserProfile(
    val id: String,
    val username: String,
    val name: String,
    val areas: List<CatchmentArea>,
    val demo: Boolean,
    /** Local language the APE prefers for guidance. */
    val language: AppLanguage = AppLanguage.PORTUGUESE
)
