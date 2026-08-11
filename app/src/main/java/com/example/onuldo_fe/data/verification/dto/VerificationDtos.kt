package com.example.onuldo_fe.data.verification.dto

data class ImageUploadResultDto(
    val bucket: String,
    val fileId: String,
    val url: String,
    val contentType: String,
    val size: Long
)

data class ChallengeVerificationRequestDto(
    val fileId: String
)

data class ChallengeVerificationResultDto(
    val verificationId: Long,
    val challengeId: Long,
    val participationId: Long,
    val fileId: String,
    val verificationDate: String,
    val verifiedAt: String,
    val review: String
)

data class ManualReviewResultDto(
    val manualReviewRequestedAt: String? = null
)
