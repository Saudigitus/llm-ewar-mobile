package org.saudigitus.climasaude.domain.repository

import kotlinx.coroutines.flow.Flow
import org.saudigitus.climasaude.domain.model.Alert

interface AlertRepository {
    /** Cached alerts for the active profile's areas, most severe first. */
    val alerts: Flow<List<Alert>>

    /** Replaces the cached alerts with fresh ones and returns how many were stored. */
    suspend fun refresh(): Int
}
