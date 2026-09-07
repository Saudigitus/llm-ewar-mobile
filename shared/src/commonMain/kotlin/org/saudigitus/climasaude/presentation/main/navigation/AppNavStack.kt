package org.saudigitus.climasaude.presentation.main.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.toRoute
import org.saudigitus.climasaude.presentation.navigation.AlertDetailRoute
import org.saudigitus.climasaude.presentation.navigation.ChildRoute
import org.saudigitus.climasaude.presentation.navigation.NewChildRoute
import org.saudigitus.climasaude.presentation.navigation.NewTriageRoute
import org.saudigitus.climasaude.presentation.navigation.TopLevelDestination
import org.saudigitus.climasaude.presentation.navigation.TriageDetailRoute
import org.saudigitus.climasaude.presentation.triage.TriageUiState
import org.saudigitus.climasaude.utils.text.UiText

internal fun NavBackStackEntry.topLevelDestination(): TopLevelDestination? =
    TopLevelDestination.entries.firstOrNull { top ->
        destination.hierarchy.any { it.hasRoute(top.routeClass) }
    }

internal fun NavBackStackEntry.title(triageState: TriageUiState): String =
    when {
        destination.hasRoute<AlertDetailRoute>() -> UiText.alertDetail
        destination.hasRoute<ChildRoute>() ->
            triageState.child(toRoute<ChildRoute>().childId)?.child?.name ?: UiText.child
        destination.hasRoute<NewChildRoute>() -> UiText.addChild
        destination.hasRoute<NewTriageRoute>() -> UiText.newTriage
        destination.hasRoute<TriageDetailRoute>() ->
            if (toRoute<TriageDetailRoute>().hasSaved) UiText.triageSaved else UiText.triageDetail
        else -> when (topLevelDestination()) {
            TopLevelDestination.ALERTS, null -> UiText.appName
            TopLevelDestination.TRIAGE -> UiText.triageTab
            TopLevelDestination.PROFILE -> UiText.settings
        }
    }