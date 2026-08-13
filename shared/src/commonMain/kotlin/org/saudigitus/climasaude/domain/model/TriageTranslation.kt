package org.saudigitus.climasaude.domain.model

data class TriageTranslation(
    val language: AppLanguage,
    val title: String,
    val steps: List<String>
)
