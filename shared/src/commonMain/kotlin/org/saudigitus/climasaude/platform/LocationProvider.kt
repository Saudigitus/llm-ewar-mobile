package org.saudigitus.climasaude.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.saudigitus.climasaude.domain.model.GeoPoint

interface LocationProvider {
    fun hasPermission(): Boolean
    suspend fun requestPermission(): Boolean
    fun isEnabled(): Boolean
    fun updates(): Flow<GeoPoint>
}

class UnavailableLocationProvider : LocationProvider {
    override fun hasPermission(): Boolean = false
    override suspend fun requestPermission(): Boolean = false
    override fun isEnabled(): Boolean = false
    override fun updates(): Flow<GeoPoint> = emptyFlow()
}
