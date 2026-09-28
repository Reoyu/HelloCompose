package com.example.ch06.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ch06.MainApplication

@Composable
fun ProfileRoute() {
    val app = LocalContext.current.applicationContext as MainApplication

    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(
            app.container.profileRepository
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        uiState = uiState,
        onUsernameChange = viewModel::onUsernameChange,
        onToggleNotification = viewModel::onToggleNotification
    )
}