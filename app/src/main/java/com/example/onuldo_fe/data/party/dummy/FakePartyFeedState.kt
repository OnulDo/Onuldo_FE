package com.example.onuldo_fe.data.party.dummy

object FakePartyFeedState {
    var memberCount: Int = PartyFeedDummyData.progress.totalMemberCount
        private set

    fun updateMemberCount(count: Int) {
        memberCount = count.coerceAtLeast(0)
    }
}
