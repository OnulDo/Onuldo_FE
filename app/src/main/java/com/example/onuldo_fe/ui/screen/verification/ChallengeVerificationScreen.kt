package com.example.onuldo_fe.ui.screen.verification

import androidx.compose.runtime.Composable

@Composable
fun ChallengeVerificationScreen(
    status: VerificationStatus,
    failureReason: String = "",
    onConfirmClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onManualReviewClick: () -> Unit = {},
    isReviewResultReady: Boolean = false,
    submittedAt: String? = null
) {
    when (status) {
        VerificationStatus.REVIEWING -> VerificationReviewingScreen(
            isResultReady = isReviewResultReady
        )
        VerificationStatus.SUCCESS -> VerificationSuccessScreen(onConfirmClick = onConfirmClick)
        VerificationStatus.FAILURE -> VerificationFailureScreen(
            failureReason = failureReason,
            onRetryClick = onRetryClick,
            onManualReviewClick = onManualReviewClick
        )
        VerificationStatus.WAITING -> VerificationWaitingScreen(
            submittedAt = submittedAt,
            onConfirmClick = onConfirmClick
        )
    }
}
