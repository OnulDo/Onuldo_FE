package com.example.onuldo_fe.ui.screen.party

import androidx.annotation.DrawableRes

enum class PartyMemberRole { Leader, Member }
enum class PartyReadyStatus { NotApplicable, Waiting, Ready }
enum class InviteCodeError(val message: String) {
    Invalid("잘못된 초대코드예요. 코드를 다시 확인해주세요"),
    AlreadyStarted("이미 시작된 파티예요."),
    Full("파티 인원이 가득 찼어요."),
    Expired("만료된 초대코드예요.")
}

data class PartyMemberUi(val name: String, val role: PartyMemberRole, val readyStatus: PartyReadyStatus)

data class PartyCardUi(
    val id: String,
    val partyName: String,
    val challengeName: String,
    val dDay: String,
    val deadline: String,
    val remainingText: String? = null,
    val completedMemberCount: Int,
    val totalMemberCount: Int
)

data class PartyChallengeUi(val id: String, val title: String, val period: String, val deposit: Int)

data class PartyChallengeCardUi(
    val challenge: PartyChallengeUi,
    val participantCount: Int,
    val imageUrl: String? = null,
    @param:DrawableRes val fallbackImageRes: Int
)

data class PartyWaitingRoomUi(
    val partyName: String = "갓생팟",
    val challengeName: String = "30일 헬스 챌린지",
    val inviteCode: String = "82K3H9",
    val period: String = "4주",
    val deposit: Int = 30_000,
    val capacity: Int = 5,
    val members: List<PartyMemberUi> = listOf(
        PartyMemberUi("민지", PartyMemberRole.Leader, PartyReadyStatus.NotApplicable),
        PartyMemberUi("서연", PartyMemberRole.Member, PartyReadyStatus.Ready),
        PartyMemberUi("준호", PartyMemberRole.Member, PartyReadyStatus.Ready)
    )
) {
    val canStart: Boolean
        get() = members.size >= 2 && members.filter { it.role == PartyMemberRole.Member }.all { it.readyStatus == PartyReadyStatus.Ready }
}

val samplePartyCards = listOf(
    PartyCardUi("party-1", "새벽 러너 파티", "30분 러닝", "D-12", "7:00 마감", "45분 남음", 2, 5),
    PartyCardUi("party-2", "책상 공부 인증 파티", "5시간 집중", "D-20", "6:00 마감", "1시간 남음", 3, 5)
)

val samplePartyChallenges = listOf(
    PartyChallengeUi("challenge-1", "30일 헬스 챌린지", "4주", 10_000),
    PartyChallengeUi("challenge-2", "30분 러닝", "2주", 10_000),
    PartyChallengeUi("challenge-3", "하루 독서 30분", "8주", 20_000)
)
