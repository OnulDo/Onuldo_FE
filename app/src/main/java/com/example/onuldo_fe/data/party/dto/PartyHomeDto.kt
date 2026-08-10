package com.example.onuldo_fe.data.party.dto

/** GET /api/parties/home 응답의 result. */
data class PartyHomeResultDto(
    val settlementBanners: List<PartySettlementBannerDto> = emptyList(), // 아직 확인하지 않은 정산 완료 파티 목록
    val parties: List<PartyHomeItemDto> = emptyList()                    // 홈에 노출할 진행 중 파티 목록
)

/** 홈의 "함께하는 파티" 카드 한 건. */
data class PartyHomeItemDto(
    val partyId: Long = 0,                                  // 파티 ID
    val name: String = "",                                  // 파티 이름
    val challengeTitle: String = "",                        // 파티가 수행 중인 챌린지 이름
    val challengeId: Long = 0,                              // 인증 API에 전달할 챌린지 ID
    val startDate: String = "",                            // 파티 시작일(yyyy-MM-dd)
    val endDate: String = "",                               // 파티 종료일(yyyy-MM-dd)
    val verificationDeadline: String? = null,                // 오늘 인증 마감 시각(HH:mm 또는 HH:mm:ss)
    val showRemainingTime: Boolean = false,                  // 마감까지 남은 시간 표시 여부
    val status: String = "NOT_VERIFIED",                    // 나의 오늘 인증 상태
    val dailyStatus: String = "WAITING",                    // 챌린지 데일리 상태
    val verifiedAt: String? = null,                          // 오늘 인증 제출 또는 완료 시각
    val streakDays: Int = 0,                                // 연속 인증 성공 일수
    val members: List<PartyHomeMemberDto> = emptyList(),     // 파티원별 오늘 인증 현황
    // HOME-09 응답에는 category가 없으므로 카메라 상단 표시는 빈 값으로 유지한다.
    val category: String? = null
)

/** 홈 파티 카드에 표시할 파티원별 오늘 인증 상태. */
data class PartyHomeMemberDto(
    val userId: Long = 0,                        // 파티원 사용자 ID
    val profileImageUrl: String? = null,         // 파티원 프로필 이미지 URL
    val isVerifiedToday: Boolean = false         // 오늘 인증 완료 여부
)

/** 아직 확인하지 않은 파티 정산 결과 배너. */
data class PartySettlementBannerDto(
    val partyId: Long = 0,                       // 정산 결과를 조회할 파티 ID
    val partyName: String = ""                   // 정산이 완료된 파티 이름
)
