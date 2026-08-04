package com.example.onuldo_fe.data.common

/** Swagger에 정의된 공통 API 응답 형식. */
data class ApiResponse<T>(
    val timestamp: String,
    val code: String,
    val message: String,
    val result: T
)
