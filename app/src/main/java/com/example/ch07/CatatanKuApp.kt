package com.example.ch07

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ch07.ui.edit.NoteEditRoute
import com.example.ch07.ui.list.NoteListRoute

@Composable
fun CatatanKuApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.NoteList.route
    ) {
        composable(Routes.NoteList.route) {
            NoteListRoute(
                onAddNote = { navController.navigate(Routes.NoteEdit.createRoute()) },
                onEditNote = { id -> navController.navigate(Routes.NoteEdit.createRoute(id)) }
            )
        }

        composable(
            route = Routes.NoteEdit.route,
            arguments = listOf(navArgument(ARG_NOTE_ID) { type = NavType.IntType })
        ) { entry ->
            NoteEditRoute(
                noteId = entry.arguments?.getInt(ARG_NOTE_ID) ?: 0,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
