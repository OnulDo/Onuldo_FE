package com.example.onuldo_fe.data.party.dummy

import com.example.onuldo_fe.data.party.dto.PartyFeedItemDto

object PartyFeedDummyData {
    val feedItems = listOf(
        PartyFeedItemDto(1L, "민지", null, true, "https://example.com/verifications/1.jpg", "2026-07-27T09:00:00"),
        PartyFeedItemDto(2L, "서연", null, true, "https://example.com/verifications/2.jpg", "2026-07-27T08:00:00"),
        PartyFeedItemDto(3L, "지호", null, true, "https://example.com/verifications/3.jpg", "2026-07-27T07:00:00"),
        PartyFeedItemDto(4L, "수아", null, true, "https://example.com/verifications/4.jpg", "2026-07-27T06:00:00"),
        PartyFeedItemDto(FakePartyStore.CURRENT_USER_ID, "하늘", null, false, null, null)
    )
}
