package com.example.onuldo_fe.data.party.dto

// 새로운 파티를 생성할 때 서버로 전달하는 요청
data class CreatePartyRequestDto(
    val name: String,                          // 생성할 파티 이름
    val challengeId: String,                   // 연계할 챌린지 ID
    val challengeName: String,                 // 연계할 챌린지 이름
    val period: String,                        // 파티 공통 진행 기간
    val deposit: Int,                          // 파티원 1인당 도전금
    val capacity: Int                          // 방장을 포함한 모집 최대 인원
)

// 파티 생성 성공 후 발급된 파티 정보 응답
data class CreatePartyResponseDto(
    val partyId: String,                       // 생성된 파티 ID
    val inviteCode: String                     // 서버에서 발급한 6자리 초대코드
)

// 파티 대기방에 참여 중인 파티원 정보 응답
data class PartyMemberDto(
    val id: String,                            // 파티원 고유 ID
    val nickname: String,                      // 파티원 닉네임
    val profileImageUrl: String?,              // 파티원 프로필 이미지 URL
    val role: String,                          // 파티 역할(LEADER 또는 MEMBER)
    val readyStatus: String,                   // 준비 상태(NOT_APPLICABLE, WAITING, READY)
    val joinedOrder: Int,                      // 방장 승계를 위한 파티 입장 순서
    val defaultCharacterId: Int? = null        // 프로필 이미지가 없을 때 표시할 기본 캐릭터 ID
)

// 파티원 모집과 준비 상태를 표시하기 위한 대기방 정보 응답
data class PartyWaitingRoomDto(
    val partyId: String,                       // 대기 중인 파티 ID
    val partyName: String,                     // 파티 이름
    val challengeName: String,                 // 연계된 챌린지 이름
    val inviteCode: String,                    // 파티 참여용 6자리 초대코드
    val period: String,                        // 파티 공통 진행 기간
    val deposit: Int,                          // 파티원 1인당 도전금
    val capacity: Int,                         // 방장을 포함한 모집 최대 인원
    val members: List<PartyMemberDto>           // 현재 대기방에 참여한 파티원 목록
)

// 파티 홈의 진행 중인 파티 카드에 표시할 요약 정보 응답
data class PartySummaryDto(
    val partyId: String,                       // 파티 고유 ID
    val partyName: String,                     // 파티 이름
    val challengeName: String,                 // 연계된 챌린지 이름
    val dDay: String,                          // 챌린지 종료일까지 남은 기간
    val deadline: String,                      // 오늘 인증 마감 시간
    val remainingText: String?,                // 오늘 인증 마감까지 남은 시간 문구
    val completedMemberCount: Int,             // 오늘 인증을 완료한 파티원 수
    val totalMemberCount: Int,                 // 현재 참여 중인 전체 파티원 수
    val status: String                         // 파티 상태(RECRUITING, IN_PROGRESS, DISBANDED)
)
