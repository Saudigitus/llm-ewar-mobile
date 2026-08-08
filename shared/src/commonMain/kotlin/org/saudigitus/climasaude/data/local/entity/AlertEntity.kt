package org.saudigitus.climasaude.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey val id: String,
    val areaId: String,
    val areaName: String,
    val disease: String,
    val level: String,
    val probability: Double?,
    val startsAt: String,
    val endsAt: String,
    val rainfallNote: String?,
    val updatedAt: String,
    val demo: Boolean
)
