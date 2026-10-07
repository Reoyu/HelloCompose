package com.example.ch07.util

// *TODO [T3.3] Implementasikan fungsi murni (tanpa Android) ini. Selisih*
//   *`now - then` (negatif dianggap 0) diubah menjadi teks:*
//     *< 1 menit  -> "baru saja"        < 1 hari   -> "N jam lalu"*
//     *< 1 jam    -> "N menit lalu"     < 30 hari  -> "N hari lalu"*
//     *< 365 hari -> "N bulan lalu" (1 bulan = 30 hari)   selebihnya -> "N tahun lalu"*
//   *Tes yang sudah disediakan: RelativeTimeTest (merah sampai fungsi ini selesai).*
fun formatRelativeTime(now: Long, then: Long): String {
    val diff = (now - then).coerceAtLeast(0L)

    val minute = 60_000L
    val hour = 60 * minute
    val day = 24 * hour
    val month = 30 * day
    val year = 365 * day

    return when {
        diff < minute -> "baru saja"
        diff < hour -> "${diff / minute} menit lalu"
        diff < day -> "${diff / hour} jam lalu"
        diff < month -> "${diff / day} hari lalu"
        diff < year -> "${diff / month} bulan lalu"
        else -> "${diff / year} tahun lalu"
    }
}