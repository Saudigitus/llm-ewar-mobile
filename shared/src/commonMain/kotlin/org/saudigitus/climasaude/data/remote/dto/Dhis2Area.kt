package org.saudigitus.climasaude.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Dhis2Area(val id: String, val displayName: String = "")
