package org.saudigitus.climasaude.domain.repository

interface ChildRepository {
    /** Saves a child for the active profile and returns the new child ID. */
    suspend fun addChild(
        name: String,
        ageYears: Int?,
        sex: String?,
        caregiver: String?,
        community: String?,
        areaId: String?
    ): String
}
