package com.example.ch10.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// "2025-05-01T08:00:00Z" atau "2025-05-01T08:00:00.123Z" -> epoch millis.
// SimpleDateFormat dipakai (bukan java.time) karena minSdk 24.
// Mengembalikan null jika format tidak dikenali.
fun parseIsoInstant(text: String): Long? {
    if (text.isBlank()) return null
    // Buang pecahan detik: ".123Z" -> "Z"
    val normalized = text.trim().replace(Regex("""\.\d+(?=Z$)"""), "")
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
        isLenient = false
    }
    return try {
        format.parse(normalized)?.time
    } catch (e: ParseException) {
        null
    }
}
