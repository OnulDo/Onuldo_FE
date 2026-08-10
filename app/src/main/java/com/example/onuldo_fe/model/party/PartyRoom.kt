package com.example.onuldo_fe.model.party

// 파티 대기방에서 사용하는 참여자 권한
enum class PartyRole {
    Leader,                                    // 파티 생성자 또는 권한을 승계받은 파티장
    Member                                     // 초대코드로 참여한 일반 파티원
}

// 파티 대기방에서 사용하는 파티원 준비 상태
enum class PartyMemberReadyStatus {
    NotApplicable,                             // 준비완료 대상이 아닌 파티장
    Waiting,                                   // 아직 준비완료하지 않은 파티원
    Ready                                      // 포인트 검증 후 준비를 완료한 파티원
}

// 파티 생성부터 시작·해체까지의 진행 상태
enum class PartyLifecycleStatus {
    Recruiting,                                // 파티원을 모집하는 대기 상태
    InProgress,                                // 챌린지를 시작한 진행 상태
    Disbanded                                  // 모든 파티원이 이탈해 해체된 상태
}

// 파티 목록 카드의 로그인 사용자 오늘 인증 상태
enum class PartyVerificationStatus {
    NotVerified,
    Pending,
    Success,
    Fail
}

// 파티 목록 카드에 노출할 파티원별 인증 정보
data class PartySummaryMember(
    val userId: Long,                           // 파티원 회원 ID
    val nickname: String,                       // 파티원 닉네임
    val profileImageUrl: String?,               // 파티원 프로필 이미지 URL
    val isVerifiedToday: Boolean                // 오늘 인증 완료 여부
)

// 파티 대기방에 참여 중인 파티원 정보
data class PartyMember(
    val id: String,                            // 파티원 고유 ID
    val nickname: String,                      // 파티원 닉네임
    val profileImageUrl: String?,              // 파티원 프로필 이미지 URL
    val role: PartyRole,                       // 파티장 또는 일반 파티원 권한
    val readyStatus: PartyMemberReadyStatus,   // 준비완료 대상 여부와 현재 준비 상태
    val joinedOrder: Int,                      // 방장 승계를 결정하는 파티 입장 순서
    val defaultCharacterId: Int?               // 프로필 이미지가 없을 때 표시할 기본 캐릭터 ID
)

// 파티원 모집과 준비 상태를 표시할 대기방 정보
data class PartyWaitingRoom(
    val partyId: String,                       // 대기 중인 파티 고유 ID
    val partyName: String,                     // 파티 이름
    val inviteCode: String,                    // 파티 참여용 6자리 초대코드
    val period: String,                        // 파티원 전체에 적용할 진행 기간
    val deposit: Int,                          // 파티원 1인당 예치할 도전금
    val capacity: Int,                         // 파티장을 포함한 모집 최대 인원
    val members: List<PartyMember>,            // 현재 대기방에 참여 중인 파티원 목록
    val isHost: Boolean,                       // 로그인 사용자가 파티장인지 여부
    val canStart: Boolean,                     // 서버가 판정한 파티 시작 가능 여부
    val status: PartyLifecycleStatus = PartyLifecycleStatus.Recruiting // 현재 파티 진행 상태
)

// 파티 홈의 진행 중인 파티 카드에 표시할 요약 정보
data class PartySummary(
    val partyId: String,                       // 파티 고유 ID
    val partyName: String,                     // 파티 이름
    val challengeName: String,                 // 연계된 챌린지 이름
    val dDay: String,                          // 챌린지 종료일까지 남은 기간 문구
    val deadline: String,                      // 오늘 인증 마감 시간 문구
    val remainingText: String?,                // 오늘 인증 마감까지 남은 시간 문구
    val completedMemberCount: Int,             // 오늘 인증을 완료한 파티원 수
    val totalMemberCount: Int,                 // 현재 참여 중인 전체 파티원 수
    val status: PartyLifecycleStatus,          // 파티 모집·진행·해체 상태
    val goal: String = challengeName,          // 카드에 표시할 파티 목표 문구
    val verificationStatus: PartyVerificationStatus = PartyVerificationStatus.NotVerified, // 내 오늘 인증 상태
    val myDailyStatus: String = "WAITING",     // 내 오늘 데일리 인증 가능 상태
    val members: List<PartySummaryMember> = emptyList(), // 카드에 노출할 파티원별 인증 현황
    val challengeId: Long = 0                  // 인증 화면에 전달할 챌린지 ID
)

// 파티 생성 화면의 입력값을 Repository에 전달하는 명령
data class CreatePartyCommand(
    val name: String,                          // 생성할 파티 이름
    val challengeId: String,                   // 연계할 챌린지 고유 ID
    val challengeName: String,                 // 연계할 챌린지 이름
    val period: String,                        // 파티원 전체에 적용할 진행 기간
    val deposit: Int,                          // 파티원 1인당 예치할 도전금
    val capacity: Int                          // 파티장을 포함한 모집 최대 인원
)

// 파티 생성 성공 후 화면 이동에 사용할 발급 정보
data class CreatedParty(
    val partyId: String,                       // 생성된 파티 고유 ID
    val inviteCode: String                     // 서버에서 발급한 6자리 초대코드
)
