package com.example.ch06

import android.app.Application
import com.example.ch06.di.AppContainer
import com.example.ch06.di.DefaultAppContainer

class MainApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}
