package com.example.onuldo_fe.data.party.dummy

enum class FakePartyInviteStatus {
    Active,
    Started,
    Expired
}

data class FakePartyInviteParty(
    val partyId: String,
    val inviteCode: String,
    val capacity: Int,
    val memberCount: Int,
    val status: FakePartyInviteStatus
)

sealed interface FakePartyJoinState {
    data class Joined(val partyId: String) : FakePartyJoinState
    data object NotFound : FakePartyJoinState
    data object AlreadyStarted : FakePartyJoinState
    data object Full : FakePartyJoinState
    data object Expired : FakePartyJoinState
}

/**
 * API 연동 전 초대코드 참여 흐름을 검증하기 위한 메모리 기반 fake 파티 저장소입니다.
 * 실제 API 연동 시 서버가 초대코드 상태와 정원을 검사하므로 이 객체는 제거합니다.
 */
object FakePartyInviteState {
    private val parties = mutableMapOf(
        PartyInviteDummyData.VALID_CODE to FakePartyInviteParty(
            partyId = PartyInviteDummyData.VALID_PARTY_ID,
            inviteCode = PartyInviteDummyData.VALID_CODE,
            capacity = 5,
            memberCount = 3,
            status = FakePartyInviteStatus.Active
        )
    )

    fun registerActiveParty(
        code: String,
        partyId: String,
        capacity: Int,
        memberCount: Int
    ) {
        val normalizedCode = code.uppercase()
        parties[normalizedCode] = FakePartyInviteParty(
            partyId = partyId,
            inviteCode = normalizedCode,
            capacity = capacity,
            memberCount = memberCount.coerceIn(0, capacity),
            status = FakePartyInviteStatus.Active
        )
    }

    /**
     * 현재 인원과 모집 정원을 한 번에 검사하고 참여 인원을 증가시킵니다.
     * 같은 순간 여러 참여 요청이 들어오는 실제 환경에서는 서버가 이 검증을 담당합니다.
     */
    @Synchronized
    fun tryJoin(code: String): FakePartyJoinState {
        val normalizedCode = code.uppercase()
        val party = parties[normalizedCode] ?: return FakePartyJoinState.NotFound

        return when {
            party.status == FakePartyInviteStatus.Started -> FakePartyJoinState.AlreadyStarted
            party.status == FakePartyInviteStatus.Expired -> FakePartyJoinState.Expired
            party.memberCount >= party.capacity -> FakePartyJoinState.Full
            else -> {
                parties[normalizedCode] = party.copy(memberCount = party.memberCount + 1)
                FakePartyJoinState.Joined(party.partyId)
            }
        }
    }

    fun updateMemberCount(code: String, memberCount: Int) {
        val normalizedCode = code.uppercase()
        val party = parties[normalizedCode] ?: return
        parties[normalizedCode] = party.copy(memberCount = memberCount.coerceIn(0, party.capacity))
    }

    fun markStarted(code: String) {
        updateStatus(code, FakePartyInviteStatus.Started)
    }

    fun markExpired(code: String) {
        updateStatus(code, FakePartyInviteStatus.Expired)
    }

    private fun updateStatus(code: String, status: FakePartyInviteStatus) {
        val normalizedCode = code.uppercase()
        val party = parties[normalizedCode] ?: return
        parties[normalizedCode] = party.copy(status = status)
    }
}
