package com.example.feature_camerax.sample

import android.app.Application
import android.content.res.Configuration
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class MainApplication : Application() {

    override fun onCreate() {
        startKoin {
            modules(listOf())
        }
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}