package com.example.onuldo_fe.viewmodel.party


data class PartyChallengeSelectUiState(
    val challenges: List<PartyChallengeCardUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
