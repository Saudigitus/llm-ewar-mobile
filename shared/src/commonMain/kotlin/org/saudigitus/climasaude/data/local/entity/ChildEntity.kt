package org.saudigitus.climasaude.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class ChildEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val ageYears: Int?,
    val createdAt: String,
    val demo: Boolean,
    val sex: String? = null,
    val caregiver: String? = null,
    val community: String? = null,
    val syncedAt: String? = null,
    val areaId: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationAccuracy: Double? = null
)
