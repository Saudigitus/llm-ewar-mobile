package org.saudigitus.climasaude.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import org.saudigitus.climasaude.presentation.app.AppViewModel
import org.saudigitus.climasaude.presentation.login.LoginScreen
import org.saudigitus.climasaude.presentation.main.MainScreen
import org.saudigitus.climasaude.presentation.splash.SplashScreen
import org.saudigitus.climasaude.presentation.theme.Canvas
import org.saudigitus.climasaude.presentation.theme.ClimaSaudeTheme
import org.saudigitus.climasaude.presentation.triage.TriageViewModel

@Composable
fun ClimaSaudeApp(
    viewModel: AppViewModel = koinViewModel(), triageViewModel: TriageViewModel = koinViewModel(),
    onReadyForNotifications: (() -> Unit)? = null
) {
    val state by viewModel.state.collectAsState()
    val hasRealAlerts = state.profile?.demo == false && state.alerts.any { !it.demo }
    LaunchedEffect(hasRealAlerts) {
        if (hasRealAlerts) onReadyForNotifications?.invoke()
    }
    var minimumShown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(SplashMinimumMs)
        minimumShown = true
    }
    ClimaSaudeTheme {
        Crossfade(state.loading || !minimumShown) { splash ->
            if (splash) SplashScreen()
            else Surface(modifier = Modifier.fillMaxSize(), color = Canvas) {
                if (state.profile == null) LoginScreen(
                    state.busy,
                    state.message,
                    viewModel::login,
                    viewModel::enterDemo
                )
                else MainScreen(
                    viewModel,
                    triageViewModel,
                    onLogout = { triageViewModel.reset(); viewModel.logout() }
                )
            }
        }
    }
}

private const val SplashMinimumMs = 1_200L
