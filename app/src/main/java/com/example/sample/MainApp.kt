package com.example.sample

import android.app.Application
import com.example.sample.di.koinModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // dependency initialization
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(applicationContext)
            modules(koinModule)
        }

    }

}