package com.example.ch09

import android.app.Application
import com.example.ch09.di.AppContainer
import com.example.ch09.di.DefaultAppContainer

class MainApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
