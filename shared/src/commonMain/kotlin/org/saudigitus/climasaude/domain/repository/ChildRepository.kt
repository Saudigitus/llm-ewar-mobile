package org.saudigitus.climasaude.domain.repository

import org.saudigitus.climasaude.domain.model.GeoPoint

interface ChildRepository {
    /** Saves a child for the active profile and returns the new child ID. */
    suspend fun addChild(
        name: String,
        ageYears: Int?,
        sex: String?,
        caregiver: String?,
        location: GeoPoint?,
        areaId: String?
    ): String
}
