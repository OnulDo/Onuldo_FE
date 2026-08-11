package com.example.onuldo_fe.data.party.dto

import com.google.gson.annotations.SerializedName

/** GET /api/parties의 커서 기반 목록 응답. */
data class PartyListPageResponseDto(
    val timestamp: String = "",                    // 서버 응답 시각
    val code: String = "",                         // 응답 결과 코드
    val message: String = "",                      // 응답 메시지
    @SerializedName(value = "result", alternate = ["content"])
    val result: List<RealPartySummaryDto> = emptyList(), // 현재 페이지의 파티 목록
    val nextCursor: String? = null,                 // 다음 페이지 조회용 커서
    val hasNext: Boolean = false                    // 다음 페이지 존재 여부
)
