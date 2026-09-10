package org.saudigitus.climasaude.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TriagesResponse(val triages: List<TriagePayload> = emptyList())
