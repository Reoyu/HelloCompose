plugins {
    id("com.android.application") version "8.3.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.devtools.ksp") version "1.9.23-1.0.20" apply false
    // Versi plugin serialization WAJIB sama dengan versi Kotlin
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23" apply false
    // Hilt (Chapter 10): plugin Gradle-nya membantu men-generate kode DI
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}
