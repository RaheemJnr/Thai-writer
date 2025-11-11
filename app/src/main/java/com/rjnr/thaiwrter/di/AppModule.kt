package com.rjnr.thaiwrter.di

import com.rjnr.thaiwrter.data.local.AppDatabase
import com.rjnr.thaiwrter.data.preferences.OnboardingPreferences
import com.rjnr.thaiwrter.data.repository.ThaiLanguageRepository
import com.rjnr.thaiwrter.ui.screens.character_practice.CharacterPracticeViewModel
import com.rjnr.thaiwrter.ui.screens.free_drawing.FreewritingViewModel
import com.rjnr.thaiwrter.ui.screens.main.MainViewModel
import com.rjnr.thaiwrter.ui.screens.onboarding.OnboardingStatusViewModel
import com.rjnr.thaiwrter.ui.screens.onboarding.OnboardingViewModel
import com.rjnr.thaiwrter.ui.screens.progress.ProgressViewModel
import com.rjnr.thaiwrter.utils.ConnectivityMonitor
import com.rjnr.thaiwrter.utils.DatabaseInitializer
import com.rjnr.thaiwrter.utils.MLStrokeValidator
import com.rjnr.thaiwrter.utils.SoundManager
import com.rjnr.thaiwrter.utils.TelemetryLogger
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().thaiCharacterDao() }
    single { get<AppDatabase>().userProgressDao() }

    // Repository
    single { ThaiLanguageRepository(get(), get()) }
    single { DatabaseInitializer(androidContext(), get()) }
    single { ConnectivityMonitor(androidContext()) }
    single { OnboardingPreferences(androidContext()) }

    single { MLStrokeValidator(androidContext()) }
    single { TelemetryLogger() }
    single { SoundManager(androidContext()) }

    // ViewModels
    viewModel { OnboardingViewModel(get()) }
    viewModel { OnboardingStatusViewModel(get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { ProgressViewModel(get()) }
    viewModel { CharacterPracticeViewModel(get(), get(), get()) }
    viewModel { FreewritingViewModel(get(), get()) }
}
