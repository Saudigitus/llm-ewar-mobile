package org.saudigitus.climasaude.domain.model

data class Alert(
    val id: String,
    val areaId: String,
    val areaName: String,
    val disease: Disease,
    val level: RiskLevel,
    val probability: Double?,
    val startsAt: String,
    val endsAt: String,
    val rainfallNote: String?,
    val updatedAt: String,
    val demo: Boolean = false
)
