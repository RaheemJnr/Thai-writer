package com.rjnr.thaiwrter.di

import com.rjnr.thaiwrter.data.local.AppDatabase
import com.rjnr.thaiwrter.data.repository.ThaiLanguageRepository
import com.rjnr.thaiwrter.ui.viewmodel.CharacterPracticeViewModel
import com.rjnr.thaiwrter.ui.viewmodel.MainViewModel
import com.rjnr.thaiwrter.utils.DatabaseInitializer
import com.rjnr.thaiwrter.utils.MLStrokeValidator
import com.rjnr.thaiwrter.utils.SoundManager
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

    single { MLStrokeValidator(androidContext()) }
    single { SoundManager(androidContext()) }

    // ViewModels
    viewModel { MainViewModel(get()) }
    viewModel { CharacterPracticeViewModel(get(), get(), get()) }
}