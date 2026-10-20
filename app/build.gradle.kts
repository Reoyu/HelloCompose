import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")   // @Serializable (DTO)
}

// API key dibaca dari local.properties (TIDAK di-commit), bukan ditulis di kode.
// Catatan: project.findProperty() hanya membaca gradle.properties / -P,
// BUKAN local.properties -- karena itu file-nya dibaca manual di sini.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
val newsApiKey: String = localProperties.getProperty("NEWS_API_KEY")
    ?: (project.findProperty("NEWS_API_KEY") as String?)   // alternatif: CI / -P
    ?: ""

android {
    namespace  = "com.example.ch09"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ch09"
        minSdk        = 24
        targetSdk     = 34
        versionCode   = 1
        versionName   = "1.0"

        buildConfigField("String", "NEWS_API_KEY", "\"$newsApiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true          // agar BuildConfig.NEWS_API_KEY tersedia
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Room menulis file JSON skema database ke app/schemas/ (Tugas 4)
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    // 2024.09.00 = Material 3 1.3, yang menyediakan PullToRefreshBox (Tugas 3)
    val composeBom = platform("androidx.compose:compose-bom:2024.09.00")
    implementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // ViewModel + StateFlow di Compose (Chapter 6)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Retrofit + OkHttp (Chapter 9)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Kotlinx Serialization + converter untuk Retrofit.
    // 1.6.3 = versi terakhir yang cocok dengan Kotlin 1.9 (1.7.x butuh Kotlin 2.0)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // Coil: memuat gambar dari URL
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")

    // Room: cache offline (Tugas 4)
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Unit test JVM (ViewModel, repository, Retrofit + server palsu)
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}
