package com.example.onuldo_fe.data.verification.dto

data class ImageUploadResultDto(
    val bucket: String,
    val fileId: String,
    val url: String,
    val contentType: String,
    val size: Long
)