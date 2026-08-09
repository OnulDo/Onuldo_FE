package com.example.onuldo_fe.ui.screen.verification

import androidx.compose.runtime.Composable

@Composable
fun ChallengeVerificationScreen(
    status: VerificationStatus,
    failureReason: String = "",
    verificationDeadline: String = "",
    isManualReviewLoading: Boolean = false,
    manualReviewErrorMessage: String? = null,
    onConfirmClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onManualReviewClick: () -> Unit = {},
    onManualReviewErrorDismiss: () -> Unit = {},
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
            verificationDeadline = verificationDeadline,
            isManualReviewLoading = isManualReviewLoading,
            manualReviewErrorMessage = manualReviewErrorMessage,
            onRetryClick = onRetryClick,
            onManualReviewClick = onManualReviewClick,
            onManualReviewErrorDismiss = onManualReviewErrorDismiss
        )
        VerificationStatus.WAITING -> VerificationWaitingScreen(
            submittedAt = submittedAt,
            onConfirmClick = onConfirmClick
        )
    }
}
