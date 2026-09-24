package org.saudigitus.climasaude

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import org.saudigitus.climasaude.ai.AndroidTriageModel
import org.saudigitus.climasaude.data.local.androidDatabaseBuilder
import org.saudigitus.climasaude.di.appModule
import org.saudigitus.climasaude.di.startAppKoin
import org.saudigitus.climasaude.platform.AndroidAutoSyncScheduler
import org.saudigitus.climasaude.platform.AndroidLocationProvider
import org.saudigitus.climasaude.platform.AndroidNudgeScheduler
import org.saudigitus.climasaude.platform.AndroidSecureCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ClimaSaudeApplication : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val locationProvider by lazy { AndroidLocationProvider(this) }

    override fun onCreate() {
        super.onCreate()
        val triageModel = AndroidTriageModel(this)
        startAppKoin(
            appModule(
                androidDatabaseBuilder(this),
                AndroidSecureCredentials(this),
                AndroidNudgeScheduler(this),
                AndroidAutoSyncScheduler(this),
                triageModel,
                locationProvider,
                BuildConfig.API_BASE_URL,
                BuildConfig.ALERTS_BASE_URL.takeIf { it.isNotBlank() })
        )
        val manager = WorkManager.getInstance(this)
        manager.cancelUniqueWork("alert-sync")
        val request = PeriodicWorkRequestBuilder<AppSyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()
        manager.enqueueUniquePeriodicWork("app-sync", ExistingPeriodicWorkPolicy.KEEP, request)
        AndroidAutoSyncScheduler(this).requestSync()

        appScope.launch { triageModel.init() }
    }
}
