package com.example.onuldo_fe.model.party

import java.text.Normalizer

// 파티 대기방에서 사용하는 참여자 권한
enum class PartyRole {
    Leader,
    Member
}

// 파티 대기방에서 사용하는 파티원 준비 상태
enum class PartyMemberReadyStatus {
    NotApplicable,
    Waiting,
    Ready
}

// 파티 생성부터 시작·해체까지의 진행 상태
enum class PartyLifecycleStatus {
    Recruiting,
    InProgress,
    Disbanded
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
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String?,
    val isVerifiedToday: Boolean
)

// 파티 대기방에 참여 중인 파티원 정보
data class PartyMember(
    val id: String,
    val nickname: String,
    val profileImageUrl: String?,
    val role: PartyRole,
    val readyStatus: PartyMemberReadyStatus,
    val joinedOrder: Int,
    val defaultCharacterId: Int?
)

// 파티의 모집과 준비 상태를 표시하는 대기방 정보
data class PartyWaitingRoom(
    val partyId: String,
    val partyName: String,
    val inviteCode: String,
    val period: String,
    val deposit: Int,
    val capacity: Int,
    val members: List<PartyMember>,
    val isHost: Boolean,
    val canStart: Boolean,
    val status: PartyLifecycleStatus = PartyLifecycleStatus.Recruiting
)

// 파티 탭의 진행 중인 파티 카드에 표시할 요약 정보
data class PartySummary(
    val partyId: String,
    val partyName: String,
    val challengeName: String,
    val dDay: String,
    val deadline: String,
    val remainingText: String?,
    val completedMemberCount: Int,
    val totalMemberCount: Int,
    val status: PartyLifecycleStatus,
    val goal: String = challengeName,
    val verificationStatus: PartyVerificationStatus = PartyVerificationStatus.NotVerified,
    val myDailyStatus: String = "WAITING",
    val verifiedAt: String? = null,
    val members: List<PartySummaryMember> = emptyList(),
    val challengeId: Long = 0
)

// 파티 생성 화면의 입력값을 Repository에 전달하는 명령
data class CreatePartyCommand(
    val name: String,
    val challengeId: String,
    val challengeName: String,
    val period: String,
    val deposit: Int,
    val capacity: Int
)

private val partyNamePattern = Regex("^[가-힣A-Za-z0-9 ]{2,10}$")

// 화면과 ViewModel이 동일한 파티 이름 정책을 사용하도록 입력을 정규화한다.
fun normalizePartyName(name: String): String =
    Normalizer.normalize(name.trim(), Normalizer.Form.NFC)

fun isValidPartyName(name: String): Boolean =
    partyNamePattern.matches(normalizePartyName(name))

// 파티 생성 성공 후 화면 이동에 사용하는 발급 정보
data class CreatedParty(
    val partyId: String,
    val inviteCode: String
)
