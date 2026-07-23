package com.example.onuldo_fe.model.party

enum class PartyRole { Leader, Member }
enum class PartyMemberReadyStatus { NotApplicable, Waiting, Ready }
enum class PartyLifecycleStatus { Recruiting, InProgress, Disbanded }

data class PartyMember(
    val id: String,
    val nickname: String,
    val profileImageUrl: String?,
    val role: PartyRole,
    val readyStatus: PartyMemberReadyStatus,
    val joinedOrder: Int
)

data class PartyWaitingRoom(
    val partyId: String,
    val partyName: String,
    val challengeName: String,
    val inviteCode: String,
    val period: String,
    val deposit: Int,
    val capacity: Int,
    val members: List<PartyMember>
)

data class PartySummary(
    val partyId: String,
    val partyName: String,
    val challengeName: String,
    val dDay: String,
    val deadline: String,
    val remainingText: String?,
    val completedMemberCount: Int,
    val totalMemberCount: Int,
    val status: PartyLifecycleStatus
)

data class CreatePartyCommand(
    val name: String,
    val challengeId: String,
    val challengeName: String,
    val period: String,
    val deposit: Int,
    val capacity: Int
)

data class CreatedParty(
    val partyId: String,
    val inviteCode: String
)
