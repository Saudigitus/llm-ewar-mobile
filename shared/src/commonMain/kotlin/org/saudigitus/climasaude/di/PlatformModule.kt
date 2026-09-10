package org.saudigitus.climasaude.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.saudigitus.climasaude.domain.ai.TriageModel
import org.saudigitus.climasaude.platform.AutoSyncScheduler
import org.saudigitus.climasaude.platform.NudgeScheduler
import org.saudigitus.climasaude.platform.SecureCredentials

/** Bindings for the contracts implemented natively by each host app. */
internal fun platformModule(
    credentials: SecureCredentials,
    nudgeScheduler: NudgeScheduler,
    autoSyncScheduler: AutoSyncScheduler,
    triageModel: TriageModel
): Module = module {
    single<SecureCredentials> { credentials }
    single<NudgeScheduler> { nudgeScheduler }
    single<AutoSyncScheduler> { autoSyncScheduler }
    single<TriageModel> { triageModel }
}
