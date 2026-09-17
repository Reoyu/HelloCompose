package com.example.ch05starter

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

// ============================================================================
// TODO Pertemuan 5 — MainScreen (soal #2 & #3 di Tugas Pertemuan 5)
//
// [ ] 2. Tambahkan `enterTransition` dan `exitTransition` ke SETIAP
//        `composable(...)` di bawah (Home, Explore, Profile, Detail) agar
//        perpindahan antar layar tidak lagi instan. Contoh:
//
//        composable(
//            route           = Routes.Explore.route,
//            enterTransition = { slideInHorizontally { it } + fadeIn() },
//            exitTransition  = { slideOutHorizontally { -it } + fadeOut() }
//        ) { ExploreScreen() }
//
//        (import androidx.compose.animation.slideInHorizontally, fadeIn,
//        slideOutHorizontally, fadeOut — semuanya dari androidx.compose.animation)
//
// [ ] 3. (Tantangan) Setelah mendaftarkan intent-filter di AndroidManifest.xml
//        (cari TODO 3 di sana), tambahkan `deepLinks` ke composable Detail:
//
//        composable(
//            route     = Routes.Detail.route,
//            arguments = listOf(navArgument("itemId") { type = NavType.IntType }),
//            deepLinks = listOf(
//                navDeepLink { uriPattern = "myapp://article/{itemId}" }
//            )
//        ) { ... }
//
//        Test dengan:
//        adb shell am start -W -a android.intent.action.VIEW \
//            -d "myapp://article/3" com.example.ch05starter
// ============================================================================

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Routes.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector =
                                    if (currentRoute == item.route)
                                        item.iconSelected
                                    else
                                        item.iconUnselected,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // TODO 2: tambahkan enterTransition/exitTransition di keempat
            // composable() di bawah ini (lihat contoh di komentar atas file).

            composable(
                route = Routes.Home.route,
                enterTransition = {
                    slideInHorizontally { it } + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally { -it } + fadeOut()
                }
            ) {
                HomeScreen(navController)
            }

            composable(
                route = Routes.Explore.route,
                enterTransition = {
                    slideInHorizontally { it } + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally { -it } + fadeOut()
                }
            ) {
                ExploreScreen()
            }

            composable(
                route = Routes.Profile.route,
                enterTransition = {
                    slideInHorizontally { it } + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally { -it } + fadeOut()
                }
            ) {
                ProfileScreen()
            }

            composable(
                route = Routes.Detail.route,
                arguments = listOf(
                    navArgument("itemId") {
                        type = NavType.IntType
                    }
                ),
                enterTransition = {
                    slideInHorizontally { it } + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally { -it } + fadeOut()
                },
                // TODO 3 (Tantangan): tambahkan parameter `deepLinks = listOf(...)`
                // di sini — lihat contoh di komentar atas file.
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "myapp://article/{itemId}"
                    }
                )
            ) { backStackEntry ->
                val itemId =
                    backStackEntry.arguments?.getInt("itemId") ?: 0

                DetailScreen(
                    itemId = itemId,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}