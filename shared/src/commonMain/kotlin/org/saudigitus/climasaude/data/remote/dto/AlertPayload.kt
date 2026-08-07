package org.saudigitus.climasaude.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AlertPayload(
    val id: String,
    val areaId: String,
    val areaName: String = "",
    val disease: String,
    val level: String,
    val probability: Double? = null,
    val startsAt: String,
    val endsAt: String,
    val rainfallNote: String? = null,
    val updatedAt: String
)
