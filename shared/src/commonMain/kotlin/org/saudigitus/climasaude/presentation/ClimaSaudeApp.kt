package org.saudigitus.climasaude.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import org.saudigitus.climasaude.presentation.app.AppViewModel
import org.saudigitus.climasaude.presentation.login.LoginScreen
import org.saudigitus.climasaude.presentation.main.MainScreen
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
    ClimaSaudeTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Canvas) {
            when {
                state.loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                state.profile == null -> LoginScreen(
                    state.busy,
                    state.message,
                    viewModel::login,
                    viewModel::enterDemo
                )

                else -> MainScreen(
                    viewModel,
                    triageViewModel,
                    onLogout = { triageViewModel.reset(); viewModel.logout() }
                )
            }
        }
    }
}
