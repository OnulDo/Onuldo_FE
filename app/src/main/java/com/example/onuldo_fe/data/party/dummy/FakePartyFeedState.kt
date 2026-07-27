package com.example.onuldo_fe.data.party.dummy

object FakePartyFeedState {
    data class PartyFeedMeta(val partyName: String, val challengeName: String, val memberCount: Int)

    private val parties = mutableMapOf(
        101L to PartyFeedMeta("갓생팟", "새벽 6시 기상", 5),
        1L to PartyFeedMeta("새벽 러너 파티", "30분 러닝", 5),
        2L to PartyFeedMeta("책상 공부 인증 파티", "5시간 집중", 5)
    )

    @Synchronized
    fun updateParty(partyId: Long, partyName: String, challengeName: String, memberCount: Int) {
        parties[partyId] = PartyFeedMeta(partyName, challengeName, memberCount.coerceAtLeast(0))
    }

    @Synchronized fun removeParty(partyId: Long) { parties.remove(partyId) }

    @Synchronized
    fun get(partyId: Long): PartyFeedMeta = parties[partyId] ?: PartyFeedMeta("파티", "챌린지", 0)
}
