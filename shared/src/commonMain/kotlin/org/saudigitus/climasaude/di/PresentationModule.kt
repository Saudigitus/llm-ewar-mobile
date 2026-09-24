package org.saudigitus.climasaude.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.saudigitus.climasaude.presentation.app.AppViewModel
import org.saudigitus.climasaude.presentation.triage.TriageViewModel

internal val presentationModule: Module = module {
    viewModel { AppViewModel(get(), get(), get(), get()) }
    viewModel { TriageViewModel(get(), get(), get(), get()) }
}
