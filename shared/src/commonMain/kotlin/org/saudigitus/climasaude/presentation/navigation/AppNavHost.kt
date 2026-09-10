package org.saudigitus.climasaude.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.saudigitus.climasaude.domain.model.AppLanguage
import org.saudigitus.climasaude.presentation.alert.AlertDetailScreen
import org.saudigitus.climasaude.presentation.app.AppViewModel
import org.saudigitus.climasaude.presentation.dashboard.DashboardScreen
import org.saudigitus.climasaude.presentation.profile.ProfileScreen
import org.saudigitus.climasaude.utils.text.UiText
import org.saudigitus.climasaude.presentation.triage.child.ChildListScreen
import org.saudigitus.climasaude.presentation.triage.child.ChildProfileScreen
import org.saudigitus.climasaude.presentation.triage.TriageDetailScreen
import org.saudigitus.climasaude.presentation.triage.TriageEvent
import org.saudigitus.climasaude.presentation.triage.TriageViewModel
import org.saudigitus.climasaude.presentation.triage.form.NewChildForm
import org.saudigitus.climasaude.presentation.triage.form.NewTriageForm

@Composable
fun AppNavHost(
    navController: NavHostController,
    appViewModel: AppViewModel,
    triageViewModel: TriageViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appState by appViewModel.state.collectAsState()
    val triageState by triageViewModel.state.collectAsState()

    LaunchedEffect(triageViewModel, navController) {
        triageViewModel.navigation.collect { event ->
            when (event) {
                is TriageEvent.ChildSaved -> {
                    navController.navigate(ChildRoute(event.childId)) {
                        popUpTo<NewChildRoute> { inclusive = true }
                    }
                    navController.navigate(NewTriageRoute(event.childId))
                }

                is TriageEvent.TriageSaved -> navController.navigate(
                    TriageDetailRoute(event.childId, event.triageId, hasSaved = true)
                ) { popUpTo<NewTriageRoute> { inclusive = true } }
            }
        }
    }

    NavHost(navController, startDestination = AlertsRoute, modifier = modifier) {
        composable<AlertsRoute> {
            DashboardScreen(
                appState,
                triageState.triageCount,
                UiText,
                onSelect = { navController.navigate(AlertDetailRoute(it.id)) },
                onDismiss = appViewModel::dismissMessage,
                onTriage = { navController.navigateToTopLevel(TopLevelDestination.TRIAGE) }
            )
        }

        composable<TriageRoute> {
            ChildListScreen(
                triageState,
                onOpenChild = { navController.navigate(ChildRoute(it)) },
                onNewChild = { navController.navigate(NewChildRoute) },
                onSearch = triageViewModel::updateSearch
            )
        }

        composable<ProfileRoute> {
            appState.profile?.let {
                ProfileScreen(it, onLanguageChange = appViewModel::setLanguage, onLogout = onLogout)
            }
        }

        composable<AlertDetailRoute> { entry ->
            val alertId = entry.toRoute<AlertDetailRoute>().alertId
            val alert = appState.alerts.firstOrNull { it.id == alertId } ?: return@composable
            LaunchedEffect(alert.id) { appViewModel.select(alert) }
            val selected = appState.selected?.id == alert.id
            AlertDetailScreen(
                alert,
                appState.guidance.takeIf { selected },
                if (selected) appState.translationLanguage else AppLanguage.PORTUGUESE,
                UiText,
                appViewModel::translate
            )
        }

        composable<ChildRoute> { entry ->
            val childId = entry.toRoute<ChildRoute>().childId
            val item = triageState.child(childId) ?: return@composable
            ChildProfileScreen(
                item,
                triageState.areas.firstOrNull { it.id == item.child.areaId }?.name,
                onOpenTriage = { navController.navigate(TriageDetailRoute(childId, it)) }
            )
        }

        composable<NewChildRoute> {
            LaunchedEffect(Unit) { triageViewModel.clearError() }
            NewChildForm(triageState, triageViewModel::addChild)
        }

        composable<NewTriageRoute> { entry ->
            val childId = entry.toRoute<NewTriageRoute>().childId
            LaunchedEffect(Unit) { triageViewModel.clearError() }
            val child = triageState.child(childId)?.child
            NewTriageForm(
                child,
                triageState.areas.firstOrNull { it.id == child?.areaId }?.name,
                triageState,
                onSave = { fever, dangerSigns, malariaTest, referred, notes ->
                    triageViewModel.addTriage(childId, fever, dangerSigns, malariaTest, referred, notes)
                }
            )
        }

        composable<TriageDetailRoute> { entry ->
            val route = entry.toRoute<TriageDetailRoute>()
            val triage = triageState.triage(route.triageId) ?: return@composable
            LaunchedEffect(route.triageId) { triageViewModel.showTriage(route.triageId) }
            val selected = triageState.selectedTriageId == route.triageId
            TriageDetailScreen(
                triage,
                triageState.child(route.childId)?.child,
                route.hasSaved,
                onViewHistory = { navController.popBackStack() },
                language = if (selected) triageState.recommendationLanguage else AppLanguage.PORTUGUESE,
                translating = selected && triageState.translating,
                translationError = triageState.translationError.takeIf { selected },
                onTranslate = triageViewModel::translateRecommendation
            )
        }
    }
}


fun NavHostController.navigateToTopLevel(destination: TopLevelDestination) {
    navigate(destination.route) {
        popUpTo<AlertsRoute> { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
