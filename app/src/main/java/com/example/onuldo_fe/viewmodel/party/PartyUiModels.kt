package com.example.onuldo_fe.viewmodel.party

import androidx.annotation.DrawableRes

// 파티 대기방에서 사용하는 파티원 권한
enum class PartyMemberRole {
    Leader,                                    // 파티 생성자 또는 권한을 승계받은 파티장
    Member                                     // 초대코드로 참여한 일반 파티원
}

// 파티 대기방에서 표시하는 파티원의 준비 상태
enum class PartyReadyStatus {
    NotApplicable,                             // 준비완료 대상이 아닌 파티장
    Waiting,                                   // 아직 준비완료하지 않은 파티원
    Ready                                      // 포인트 검증 후 준비를 완료한 파티원
}

// 파티의 현재 진행 상태
enum class PartyStatus {
    Recruiting,                                // 파티원을 모집하고 있는 대기 상태
    InProgress,                                // 모집을 마치고 챌린지를 진행 중인 상태
    Disbanded                                  // 모든 파티원이 이탈하여 해체된 상태
}

// 초대코드 검증 실패 시 다이얼로그에 표시할 오류 상태와 문구
enum class InviteCodeError(val message: String) {
    Invalid("잘못된 초대코드예요. 코드를 다시 확인해주세요."), // 존재하지 않거나 형식이 잘못된 코드
    AlreadyStarted("이미 시작된 파티예요."),                    // 이미 챌린지를 시작하여 사용할 수 없는 코드
    Full("파티 인원이 가득 찼어요."),                          // 설정된 모집 최대 인원에 도달한 파티
    Expired("만료된 초대코드예요.")                            // 파티 해체 등으로 만료된 코드
}

// 파티 대기방의 파티원 카드에 표시할 정보
data class PartyMemberUi(
    val name: String,                          // 파티원 닉네임
    val role: PartyMemberRole,                 // 파티장 또는 일반 파티원 권한
    val readyStatus: PartyReadyStatus,         // 준비완료 대상 여부와 현재 준비 상태
    val id: String = name,                     // 파티원 고유 ID
    val joinedOrder: Int = 0,                  // 방장 이탈 시 권한 승계를 결정하는 입장 순서
    val profileImageUrl: String? = null        // 파티원 프로필 이미지 URL
)

// 파티 홈의 진행 중인 파티 카드에 표시할 정보
data class PartyCardUi(
    val id: String,                            // 파티 고유 ID
    val partyName: String,                     // 파티 이름
    val challengeName: String,                 // 파티와 연계된 챌린지 이름
    val dDay: String,                          // 챌린지 종료일까지 남은 기간 문구
    val deadline: String,                      // 오늘 인증 마감 시간 문구
    val remainingText: String? = null,         // 오늘 인증 마감까지 남은 시간 문구
    val completedMemberCount: Int,             // 오늘 인증을 완료한 파티원 수
    val totalMemberCount: Int,                 // 현재 파티에 참여 중인 전체 인원 수
    val status: PartyStatus = PartyStatus.InProgress // 파티 모집·진행·해체 상태
)

// 파티에서 선택할 챌린지의 상세 화면에 표시할 정보
data class PartyChallengeUi(
    val id: String,                            // 챌린지 고유 ID
    val title: String,                         // 챌린지 이름
    val category: String,                      // 챌린지 카테고리
    val participantCount: Int = 0,             // 챌린지 누적 참여 인원
    val summary: String = "",                  // 챌린지 소개 문구
    val benefits: List<String> = emptyList(),  // 챌린지를 통해 얻을 수 있는 효과 목록
    val recommendations: List<String> = emptyList(), // 챌린지 추천 대상 목록
    val verificationInstruction: String = "", // 인증 사진 촬영 방법
    val verificationImageUrl: String? = null   // 인증 예시 이미지 URL
)

// 파티 챌린지 선택 목록의 카드에 표시할 정보
data class PartyChallengeCardUi(
    val challenge: PartyChallengeUi,           // 카드 선택 시 상세 화면으로 전달할 챌린지 정보
    val participantCount: Int,                 // 카드에 표시할 챌린지 누적 참여 인원
    val imageUrl: String? = null,               // 서버에서 전달받은 챌린지 대표 이미지 URL
    @param:DrawableRes val fallbackImageRes: Int // 네트워크 이미지가 없을 때 표시할 로컬 이미지
)

// 파티 피드의 파티원 인증 카드에 표시할 정보
data class PartyFeedItemUi(
    val name: String,                          // 인증한 파티원의 닉네임
    val time: String,                          // 인증 시각 또는 미인증 상태 문구
    val profileImageUrl: String? = null,       // 파티원 프로필 이미지 URL
    val verificationImageUrl: String? = null,  // 서버에서 전달받은 인증 이미지 URL
    @param:DrawableRes val imageRes: Int? = null, // fake 테스트용 로컬 인증 이미지
    val memberId: String = name                // 인증한 파티원의 고유 ID
)

// 파티 대기방 화면에 표시할 파티 정보와 참여자 상태
data class PartyWaitingRoomUi(
    val partyId: String = "party-001",         // 현재 대기 중인 파티의 고유 ID
    val partyName: String = "갓생팟",           // 파티 이름
    val challengeName: String = "30일 헬스 챌린지", // 파티와 연계된 챌린지 이름
    val inviteCode: String = "82K3H9",         // 파티 참여에 사용하는 6자리 초대코드
    val period: String = "4주",                 // 파티원 전체에 동일하게 적용할 진행 기간
    val deposit: Int = 30_000,                 // 파티원 1인당 예치할 도전금
    val capacity: Int = 5,                     // 파티장을 포함한 모집 최대 인원
    val members: List<PartyMemberUi> = listOf( // 현재 대기방에 참여 중인 파티원 목록
        PartyMemberUi("민지", PartyMemberRole.Leader, PartyReadyStatus.NotApplicable, "leader-001", 0),
        PartyMemberUi("서연", PartyMemberRole.Member, PartyReadyStatus.Ready, "member-001", 1),
        PartyMemberUi("준호", PartyMemberRole.Member, PartyReadyStatus.Ready, "member-current", 2)
    )
) {
    // 방장을 포함해 2명 이상이고 일반 파티원이 모두 준비완료했을 때 true
    val canStart: Boolean
        get() = members.size >= 2 && members.filter { it.role == PartyMemberRole.Member }.all { it.readyStatus == PartyReadyStatus.Ready }
}

// 파티 목록 화면과 Preview에서 사용하는 fake 파티 카드 데이터
val samplePartyCards = listOf(
    PartyCardUi("party-1", "새벽 러너 파티", "30분 러닝", "D-12", "7:00 마감", "45분 남음", 2, 5),
    PartyCardUi("party-2", "책상 공부 인증 파티", "5시간 집중", "D-20", "6:00 마감", "1시간 남음", 3, 5)
)
