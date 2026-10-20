package com.example.ch09

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ch09.ui.detail.ArticleDetailRoute
import com.example.ch09.ui.news.NewsRoute

@Composable
fun KabarKiniApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.News.route) {
        composable(Routes.News.route) {
            NewsRoute(onArticleClick = { navController.navigate(Routes.Detail.createRoute(it.url)) })
        }
        composable(
            Routes.Detail.route,
            arguments = listOf(navArgument("articleUrl") { type = NavType.StringType })
        ) { entry ->
            ArticleDetailRoute(
                articleUrl = entry.arguments?.getString("articleUrl").orEmpty(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
