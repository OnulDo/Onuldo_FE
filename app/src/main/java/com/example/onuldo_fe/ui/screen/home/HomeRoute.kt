package com.example.onuldo_fe.ui.screen.home

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = viewModel()
) {
    HomeScreen(uiState = viewModel.uiState)
}
