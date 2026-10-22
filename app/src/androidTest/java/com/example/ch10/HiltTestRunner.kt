package com.example.ch10

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

// Saat uji instrumented, MainApplication diganti HiltTestApplication agar
// Hilt bisa membangun komponen khusus uji (dengan FakeRepositoryModule).
// Didaftarkan di app/build.gradle.kts: testInstrumentationRunner.
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application =
        super.newApplication(cl, HiltTestApplication::class.java.name, context)
}
