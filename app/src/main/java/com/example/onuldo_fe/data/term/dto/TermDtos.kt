package com.example.onuldo_fe.data.term.dto

/** 약관 본문 블록. [type]은 서버가 문단 종류를 나타내는 문자열로 준다(예: 제목/문단). */
data class ContentBlockResponseDto(
    val type: String? = null,
    val content: String? = null,
)

/** `GET /api/terms/{termType}` */
data class TermResponseDto(
    val termType: String? = null,
    val title: String? = null,
    val effectiveDate: String? = null,
    val content: List<ContentBlockResponseDto>? = null,
)
