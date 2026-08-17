package org.saudigitus.climasaude.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Dhis2User(
    val id: String,
    val username: String = "",
    val displayName: String = "",
    val organisationUnits: List<Dhis2Area> = emptyList()
)
