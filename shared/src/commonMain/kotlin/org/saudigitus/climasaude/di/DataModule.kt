package org.saudigitus.climasaude.di

import androidx.room.RoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import org.saudigitus.climasaude.data.local.AppDatabase
import org.saudigitus.climasaude.data.local.buildDatabase
import org.saudigitus.climasaude.data.remote.api.ClimaSaudeApi
import org.saudigitus.climasaude.data.remote.api.Dhis2Api
import org.saudigitus.climasaude.data.remote.createHttpClient
import org.saudigitus.climasaude.data.repository.AlertRepositoryImpl
import org.saudigitus.climasaude.data.repository.ChildRepositoryImpl
import org.saudigitus.climasaude.data.repository.SessionRepositoryImpl
import org.saudigitus.climasaude.data.repository.TriageRepositoryImpl
import org.saudigitus.climasaude.data.sync.SyncCoordinatorImpl
import org.saudigitus.climasaude.data.sync.TriageSyncRepository
import org.saudigitus.climasaude.domain.repository.AlertRepository
import org.saudigitus.climasaude.domain.repository.ChildRepository
import org.saudigitus.climasaude.domain.repository.SessionRepository
import org.saudigitus.climasaude.domain.repository.TriageRepository
import org.saudigitus.climasaude.domain.sync.SyncCoordinator


internal fun databaseModule(builder: RoomDatabase.Builder<AppDatabase>): Module = module {
    single { buildDatabase(builder) }
    single { get<AppDatabase>().profileDao() }
    single { get<AppDatabase>().areaDao() }
    single { get<AppDatabase>().alertDao() }
    single { get<AppDatabase>().childDao() }
    single { get<AppDatabase>().triageDao() }
    single { get<AppDatabase>().triageTranslationDao() }
}


internal fun networkModule(baseUrl: String, alertsBaseUrl: String?): Module = module {
    single { createHttpClient() }
    single { Dhis2Api(get(), baseUrl) }
    single { ClimaSaudeApi(get(), alertsBaseUrl) }
}


internal val repositoryModule: Module = module {
    single<AlertRepository> { AlertRepositoryImpl(get(), get(), get(), get(), get(), get()) }
    single<SessionRepository> {
        SessionRepositoryImpl(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    single<ChildRepository> {
        ChildRepositoryImpl(
            get(), get(), get(), get()) }
    single<TriageRepository> {
        TriageRepositoryImpl(get(), get(), get(), get(), get(), get(), get(), get(), get())
    }
    single {
        TriageSyncRepository(
            get(),
            get(),
            get(),
            get(),
            get())
    }
    single<SyncCoordinator> {
        SyncCoordinatorImpl(
            get(),
            get(),
            get(), get())
    }
}
