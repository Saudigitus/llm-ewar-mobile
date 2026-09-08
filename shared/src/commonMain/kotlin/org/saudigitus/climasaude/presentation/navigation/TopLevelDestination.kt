package org.saudigitus.climasaude.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import org.saudigitus.climasaude.utils.text.UiText
import kotlin.reflect.KClass


enum class TopLevelDestination(
    val route: Any,
    val routeClass: KClass<*>,
    val label: String,
    val icon: ImageVector
) {
    ALERTS(AlertsRoute, AlertsRoute::class, UiText.alerts, Icons.Filled.Notifications),
    TRIAGE(TriageRoute, TriageRoute::class, UiText.triageTab, Icons.Filled.Face),
    PROFILE(ProfileRoute, ProfileRoute::class, UiText.settings, Icons.Filled.Person)
}
