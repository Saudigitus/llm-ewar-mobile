package org.saudigitus.climasaude.domain.model

data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Double?
)
