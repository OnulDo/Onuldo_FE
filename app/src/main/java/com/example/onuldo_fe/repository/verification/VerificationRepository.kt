package com.example.onuldo_fe.repository.verification

import android.net.Uri
import com.example.onuldo_fe.model.verification.ChallengeVerificationResult
import com.example.onuldo_fe.model.verification.ImageUploadResult
import com.example.onuldo_fe.model.verification.ManualReviewResult

interface VerificationRepository {
    suspend fun uploadImage(imageUri: Uri): ImageUploadResult
    suspend fun verifyChallenge(challengeId: Long, fileId: String): ChallengeVerificationResult
    suspend fun requestManualReview(challengeId: Long): ManualReviewResult
}
