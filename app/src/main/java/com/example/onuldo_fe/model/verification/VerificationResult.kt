package com.example.onuldo_fe.model.verification

data class ImageUploadResult(
    val bucket: String,
    val fileId: String,
    val url: String,
    val contentType: String,
    val size: Long
)

enum class VerificationReview {
    PENDING,
    PASS,
    AUTO_FAIL,
    MANUAL_REVIEW
}

data class ChallengeVerificationResult(
    val verificationId: Long,
    val challengeId: Long,
    val participationId: Long,
    val fileId: String,
    val verificationDate: String,
    val verifiedAt: String,
    val review: VerificationReview
)

data class ManualReviewResult(
    val requestedAt: String
)
