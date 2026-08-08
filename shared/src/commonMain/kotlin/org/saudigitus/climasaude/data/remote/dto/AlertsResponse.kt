package org.saudigitus.climasaude.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AlertsResponse(val alerts: List<AlertPayload> = emptyList())
