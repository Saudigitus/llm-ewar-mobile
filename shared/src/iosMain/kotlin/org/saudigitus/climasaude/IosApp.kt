package org.saudigitus.climasaude

import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import org.saudigitus.climasaude.data.local.iosDatabaseBuilder
import org.saudigitus.climasaude.di.appModule
import org.saudigitus.climasaude.di.startAppKoin
import org.saudigitus.climasaude.domain.ai.UnavailableTriageModel
import org.saudigitus.climasaude.domain.sync.SyncCoordinator
import org.saudigitus.climasaude.platform.IosAutoSyncScheduler
import org.saudigitus.climasaude.platform.NudgeScheduler
import org.saudigitus.climasaude.platform.SecureCredentials
import org.saudigitus.climasaude.platform.UnavailableLocationProvider
import org.saudigitus.climasaude.presentation.ClimaSaudeApp

private val connectivityScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

fun startIosApp(
    baseUrl: String,
    alertsBaseUrl: String,
    credentials: SecureCredentials,
    nudgeScheduler: NudgeScheduler
) {
    startAppKoin(
        appModule(
            iosDatabaseBuilder(), credentials, nudgeScheduler, IosAutoSyncScheduler(),
            UnavailableTriageModel(),
            UnavailableLocationProvider(),
            baseUrl, alertsBaseUrl.takeIf { it.isNotBlank() })
    )
}

fun mainViewController() = ComposeUIViewController { ClimaSaudeApp() }

fun syncWhenConnected() {
    connectivityScope.launch { runCatching { GlobalContext.get().get<SyncCoordinator>().sync() } }
}
