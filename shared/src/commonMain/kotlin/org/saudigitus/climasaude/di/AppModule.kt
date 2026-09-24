package org.saudigitus.climasaude.di

import androidx.room.RoomDatabase
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.saudigitus.climasaude.data.local.AppDatabase
import org.saudigitus.climasaude.domain.ai.TriageModel
import org.saudigitus.climasaude.platform.AutoSyncScheduler
import org.saudigitus.climasaude.platform.LocationProvider
import org.saudigitus.climasaude.platform.NudgeScheduler
import org.saudigitus.climasaude.platform.SecureCredentials

fun appModule(
    databaseBuilder: RoomDatabase.Builder<AppDatabase>,
    credentials: SecureCredentials,
    nudgeScheduler: NudgeScheduler,
    autoSyncScheduler: AutoSyncScheduler,
    triageModel: TriageModel,
    locationProvider: LocationProvider,
    baseUrl: String,
    alertsBaseUrl: String?
): Module = module {
    includes(
        platformModule(credentials, nudgeScheduler, autoSyncScheduler, triageModel, locationProvider),
        databaseModule(databaseBuilder),
        networkModule(baseUrl, alertsBaseUrl),
        repositoryModule,
        domainModule,
        presentationModule
    )
}

fun startAppKoin(module: Module) {
    startKoin { modules(module) }
}
