package com.example.onuldo_fe.model.term

import com.example.onuldo_fe.data.auth.dto.TermType

/** 약관 본문 한 블록. */
data class TermContentBlock(
    val type: String,
    val content: String,
)

/** 약관 문서. 시행일과 본문 블록 목록을 가진다. */
data class Term(
    val termType: TermType?,
    val title: String,
    val effectiveDate: String?,
    val content: List<TermContentBlock>,
)
