package com.example.ch05starter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val label:          String,
    val route:          String,
    val iconSelected:   ImageVector,
    val iconUnselected: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        label          = "Beranda",
        route          = Routes.Home.route,
        iconSelected   = Icons.Filled.Home,
        iconUnselected = Icons.Outlined.Home
    ),
    BottomNavItem(
        label          = "Eksplorasi",
        route          = Routes.Explore.route,
        iconSelected   = Icons.Filled.Search,
        iconUnselected = Icons.Outlined.Search
    ),
    BottomNavItem(
        label          = "Profil",
        route          = Routes.Profile.route,
        iconSelected   = Icons.Filled.Person,
        iconUnselected = Icons.Outlined.Person
    ),
)
