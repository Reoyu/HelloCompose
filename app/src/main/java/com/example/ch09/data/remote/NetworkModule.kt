package com.example.ch09.data.remote

import com.example.ch09.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

const val API_KEY_HEADER = "X-Api-Key"

object NetworkModule {

    private const val BASE_URL = "https://newsapi.org/"

    val json = Json {
        ignoreUnknownKeys = true    // toleran terhadap field baru dari server
        coerceInputValues = true    // null -> nilai default jika properti non-null
    }

    // baseUrl dan apiKey bisa diganti saat pengujian (server palsu MockWebServer)
    fun createNewsApiService(
        baseUrl: String = BASE_URL,
        apiKey: String = BuildConfig.NEWS_API_KEY,
        enableLogging: Boolean = BuildConfig.DEBUG
    ): NewsApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = if (enableLogging) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
            redactHeader(API_KEY_HEADER)      // key tidak ikut tercetak di Logcat
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(apiKey))
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(NewsApiService::class.java)
    }
}

// Menambahkan API key ke SETIAP request sebagai header, sehingga key tidak
// muncul di URL (dan tidak perlu ditulis di tiap fungsi endpoint).
class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(
            chain.request().newBuilder()
                .header(API_KEY_HEADER, apiKey)
                .build()
        )
}
