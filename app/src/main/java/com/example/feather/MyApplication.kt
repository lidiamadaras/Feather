package com.example.feather

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp  // This annotation triggers Hilt's code generation.
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any global state here if needed.
    }
}