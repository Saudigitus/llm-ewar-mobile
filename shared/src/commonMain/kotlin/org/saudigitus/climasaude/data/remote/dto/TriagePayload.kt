package org.saudigitus.climasaude.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TriagePayload(
    val id: String,
    val userId: String,
    val childId: String,
    val recordedAt: String,
    val fever: Boolean,
    val dangerSigns: Boolean,
    val malariaTest: Boolean,
    val referred: Boolean,
    val notes: String,
    val recommendationTitle: String? = null,
    val recommendationBody: String? = null,
    val recommendationSource: String? = null,
    val recommendationSteps: List<String>? = null,
    val recommendationAlertIds: List<String>? = null
)
