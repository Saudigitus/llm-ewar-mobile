package org.saudigitus.climasaude.platform

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import org.saudigitus.climasaude.domain.model.GeoPoint

class AndroidLocationProvider(private val context: Context) : LocationProvider {
    @Volatile
    var permissionRequester: (suspend () -> Boolean)? = null

    private val manager: LocationManager?
        get() = context.getSystemService(LocationManager::class.java)

    override fun hasPermission(): Boolean = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }

    override suspend fun requestPermission(): Boolean =
        hasPermission() || permissionRequester?.invoke() == true || hasPermission()

    override fun isEnabled(): Boolean = enabledProviders().isNotEmpty()

    @SuppressLint("MissingPermission")
    override fun updates(): Flow<GeoPoint> = callbackFlow {
        val manager = manager
        val providers = enabledProviders()
        if (manager == null || providers.isEmpty()) {
            close()
            return@callbackFlow
        }
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trySend(location.toPoint())
            }

            override fun onProviderEnabled(provider: String) = Unit

            override fun onProviderDisabled(provider: String) = Unit

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
        }
        providers.forEach {
            manager.requestLocationUpdates(it, 0L, 0f, listener, Looper.getMainLooper())
        }
        awaitClose { manager.removeUpdates(listener) }
    }.flowOn(Dispatchers.Main)

    private fun enabledProviders(): List<String> {
        val manager = manager ?: return emptyList()
        return listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            .filter { runCatching { manager.isProviderEnabled(it) }.getOrDefault(false) }
    }

    private fun Location.toPoint() = GeoPoint(
        latitude,
        longitude,
        if (hasAccuracy()) accuracy.toDouble() else null
    )
}
