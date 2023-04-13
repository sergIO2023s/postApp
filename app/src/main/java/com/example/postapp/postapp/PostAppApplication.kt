package com.example.postapp.postapp

import android.app.Application
import com.example.postapp.postapp.di.postAppModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PostAppApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@PostAppApplication)
            // Load modules
            modules(postAppModules)
        }
    }
}