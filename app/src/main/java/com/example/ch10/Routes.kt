package com.example.ch10

import android.net.Uri

const val ARG_ARTICLE_URL = "articleUrl"

sealed class Routes(val route: String) {
    data object News : Routes("news")

    // URL artikel berisi "/", "?", "&": WAJIB di-encode agar tidak
    // merusak pola route. Navigation men-decode-nya kembali secara otomatis.
    data object Detail : Routes("detail/{$ARG_ARTICLE_URL}") {
        fun createRoute(articleUrl: String) = "detail/${Uri.encode(articleUrl)}"
    }
}
