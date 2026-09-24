package org.saudigitus.climasaude.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChildPayload(
    val id: String,
    val userId: String,
    val name: String,
    val ageYears: Int? = null,
    val createdAt: String,
    val sex: String? = null,
    val caregiver: String? = null,
    val community: String? = null,
    val areaId: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationAccuracy: Double? = null
)
