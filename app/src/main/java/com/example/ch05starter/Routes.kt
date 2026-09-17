package com.example.ch05starter

// Semua route didefinisikan sebagai konstanta agar terhindar dari typo
sealed class Routes(val route: String) {
    data object Home    : Routes("home")
    data object Explore : Routes("explore")
    data object Profile : Routes("profile")
    data object Detail  : Routes("detail/{itemId}") {
        fun createRoute(itemId: Int) = "detail/$itemId"
    }
}
