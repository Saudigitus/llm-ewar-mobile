package org.saudigitus.climasaude.domain.model

data class Triage(
    val id: String,
    val childId: String,
    val recordedAt: String,
    val fever: Boolean,
    val dangerSigns: Boolean,
    val malariaTest: Boolean,
    val referred: Boolean,
    val notes: String,
    val demo: Boolean,
    val recommendationTitle: String? = null,
    val recommendationBody: String? = null,
    val recommendationSource: String? = null,
    val syncedAt: String? = null,
    val recommendationSteps: List<String> = emptyList(),
    val recommendationAlertIds: List<String> = emptyList(),
    val translations: Map<AppLanguage, TriageTranslation> = emptyMap()
)
