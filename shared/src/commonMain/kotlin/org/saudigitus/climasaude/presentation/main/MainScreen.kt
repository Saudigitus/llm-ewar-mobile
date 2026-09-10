package org.saudigitus.climasaude.presentation.main

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.saudigitus.climasaude.presentation.app.AppUiState
import org.saudigitus.climasaude.presentation.app.AppViewModel
import org.saudigitus.climasaude.presentation.components.AddButton
import org.saudigitus.climasaude.presentation.components.ClimaSaudeBottomBar
import org.saudigitus.climasaude.presentation.components.ClimaSaudeTopBar
import org.saudigitus.climasaude.presentation.main.navigation.title
import org.saudigitus.climasaude.presentation.main.navigation.topLevelDestination
import org.saudigitus.climasaude.presentation.navigation.AlertDetailRoute
import org.saudigitus.climasaude.presentation.navigation.AppNavHost
import org.saudigitus.climasaude.presentation.navigation.ChildRoute
import org.saudigitus.climasaude.presentation.navigation.NewChildRoute
import org.saudigitus.climasaude.presentation.navigation.NewTriageRoute
import org.saudigitus.climasaude.presentation.navigation.TopLevelDestination
import org.saudigitus.climasaude.presentation.navigation.TriageDetailRoute
import org.saudigitus.climasaude.presentation.navigation.navigateToTopLevel
import org.saudigitus.climasaude.utils.text.UiText
import org.saudigitus.climasaude.presentation.theme.Canvas
import org.saudigitus.climasaude.presentation.theme.Teal
import org.saudigitus.climasaude.presentation.triage.TriageUiState
import org.saudigitus.climasaude.presentation.triage.TriageViewModel


@Composable
fun MainScreen(
    appViewModel: AppViewModel,
    triageViewModel: TriageViewModel,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val appState by appViewModel.state.collectAsState()
    val triageState by triageViewModel.state.collectAsState()
    val entry by navController.currentBackStackEntryAsState()
    val topLevel = entry?.topLevelDestination()
    val childId = entry?.takeIf { it.destination.hasRoute<ChildRoute>() }
        ?.toRoute<ChildRoute>()?.childId

    Scaffold(
        containerColor = Canvas,
        topBar = {
            ClimaSaudeTopBar(
                title = entry?.title(triageState) ?: UiText.appName,
                subtitle = null,
                onBack = if (topLevel == null) ({ navController.navigateUp() }) else null,
                syncing = appState.syncing,
                onSync = if (topLevel != null) appViewModel::syncNow else null
            )
        },
        bottomBar = {
            if (topLevel != null) ClimaSaudeBottomBar(topLevel, navController::navigateToTopLevel)
        },
        floatingActionButton = {
            when {
                topLevel == TopLevelDestination.TRIAGE ->
                    AddButton(UiText.addChild) { navController.navigate(NewChildRoute) }

                childId != null && triageState.child(childId) != null ->
                    AddButton(UiText.newTriage) { navController.navigate(NewTriageRoute(childId)) }
            }
        }
    ) { padding ->
        AppNavHost(
            navController,
            appViewModel,
            triageViewModel,
            onLogout,
            Modifier.padding(padding).consumeWindowInsets(padding).imePadding()
        )
    }
}
