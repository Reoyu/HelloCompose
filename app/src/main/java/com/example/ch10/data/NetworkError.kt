package com.example.ch10.data

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

// Menerjemahkan exception menjadi pesan yang dimengerti pengguna.
// Urutan penting: SocketTimeoutException adalah turunan IOException.
fun Throwable.toUserMessage(): String = when (this) {
    is HttpException -> when (code()) {
        401 -> "API key tidak valid atau belum diisi. Isi NEWS_API_KEY di local.properties."
        429 -> "Batas permintaan NewsAPI tercapai. Coba lagi nanti."
        in 500..599 -> "Server sedang bermasalah (kode ${code()}). Coba lagi nanti."
        else -> "Permintaan gagal (kode ${code()})."
    }
    is SocketTimeoutException -> "Koneksi terlalu lambat. Coba lagi."
    is IOException -> "Tidak ada koneksi internet."
    is SerializationException -> "Format data dari server tidak dikenali."
    else -> message ?: "Terjadi kesalahan."
}
