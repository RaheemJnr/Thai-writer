package com.rjnr.thaiwrter

import android.app.Application
import com.rjnr.thaiwrter.di.appModule
import com.rjnr.thaiwrter.utils.DatabaseInitializer
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class ThaiLanguageApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ThaiLanguageApp)
            modules(appModule)
        }
        // Initialize database
        runBlocking {
            get<DatabaseInitializer>().initializeDatabase()
        }
    }
}