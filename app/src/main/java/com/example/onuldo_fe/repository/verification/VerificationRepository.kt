package com.example.onuldo_fe.repository.verification

import android.net.Uri
import com.example.onuldo_fe.model.verification.ImageUploadResult

interface VerificationRepository {
    suspend fun uploadImage(imageUri: Uri): ImageUploadResult
}