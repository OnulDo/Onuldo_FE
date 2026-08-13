package com.example.onuldo_fe.ui.screen.verification

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ChallengeVerificationScreen(
    status: VerificationStatus,
    failureReason: String = "",
    verificationDeadline: String = "",
    isManualReviewLoading: Boolean = false,
    manualReviewErrorMessage: String? = null,
    onConfirmClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onManualReviewClick: () -> Unit = {},
    onManualReviewErrorConfirm: () -> Unit = {},
    isReviewResultReady: Boolean = false,
    submittedAt: String? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        when (status) {
            VerificationStatus.REVIEWING -> VerificationReviewingScreen(
                isResultReady = isReviewResultReady,
            )
            VerificationStatus.SUCCESS -> VerificationSuccessScreen(
                onConfirmClick = onConfirmClick,
            )
            VerificationStatus.FAILURE -> VerificationFailureScreen(
                failureReason = failureReason,
                verificationDeadline = verificationDeadline,
                isManualReviewLoading = isManualReviewLoading,
                manualReviewErrorMessage = manualReviewErrorMessage,
                onRetryClick = onRetryClick,
                onBackClick = onBackClick,
                onManualReviewClick = onManualReviewClick,
                onManualReviewErrorConfirm = onManualReviewErrorConfirm,
            )
            VerificationStatus.WAITING -> VerificationWaitingScreen(
                submittedAt = submittedAt,
                onConfirmClick = onConfirmClick,
            )
        }
    }
}