package org.saudigitus.climasaude.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.saudigitus.climasaude.domain.ai.LocalModel
import org.saudigitus.climasaude.domain.ai.UnavailableLocalModel
import org.saudigitus.climasaude.domain.guidance.GuidanceEngine
import org.saudigitus.climasaude.domain.guidance.TriageRecommendationEngine

internal val domainModule: Module = module {
    single<LocalModel> { UnavailableLocalModel() }
    single { GuidanceEngine(get()) }
    single { TriageRecommendationEngine() }
}
