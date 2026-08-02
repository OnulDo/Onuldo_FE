package com.example.onuldo_fe.model.verification

data class ImageUploadResult(
    val bucket: String,
    val fileId: String,
    val url: String,
    val contentType: String,
    val size: Long
)