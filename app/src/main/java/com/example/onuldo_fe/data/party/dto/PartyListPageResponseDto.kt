package com.example.onuldo_fe.data.party.dto

/** GET /api/parties의 커서 기반 목록 응답. */
data class PartyListPageResponseDto(
    val timestamp: String,                         // 서버 응답 시각
    val code: String,                              // 응답 결과 코드
    val message: String,                           // 응답 메시지
    val content: List<RealPartySummaryDto>,        // 현재 페이지의 파티 목록
    val nextCursor: String?,                       // 다음 페이지 요청에 전달할 커서
    val hasNext: Boolean                           // 다음 페이지 존재 여부
)
