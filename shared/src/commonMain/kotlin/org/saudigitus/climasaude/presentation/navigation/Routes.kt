package org.saudigitus.climasaude.presentation.navigation

import kotlinx.serialization.Serializable


@Serializable
data object AlertsRoute

@Serializable
data object TriageRoute

@Serializable
data object ProfileRoute

@Serializable
data class AlertDetailRoute(val alertId: String)

@Serializable
data class ChildRoute(val childId: String)

@Serializable
data object NewChildRoute

@Serializable
data class NewTriageRoute(val childId: String)


@Serializable
data class TriageDetailRoute(
    val childId: String,
    val triageId: String,
    val hasSaved: Boolean = false
)
