package com.example.onuldo_fe.ui.screen.verification

import androidx.compose.runtime.Composable

@Composable
fun ChallengeVerificationScreen(
    status: VerificationStatus
) {
    when (status) {
        VerificationStatus.REVIEWING ->
            VerificationReviewingScreen()

        VerificationStatus.SUCCESS ->
            VerificationSuccessScreen()

        VerificationStatus.FAILURE ->
            VerificationFailureScreen()

        VerificationStatus.WAITING ->
            VerificationWaitingScreen()
    }
}