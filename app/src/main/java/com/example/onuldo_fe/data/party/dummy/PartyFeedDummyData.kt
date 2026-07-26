package com.example.onuldo_fe.data.party.dummy

import com.example.onuldo_fe.data.party.dto.PartyFeedItemDto
import com.example.onuldo_fe.data.party.dto.PartyProgressDto

object PartyFeedDummyData {
    val progress = PartyProgressDto(
        completedMemberCount = 4,
        totalMemberCount = 5
    )

    val feedItems = listOf(
        PartyFeedItemDto("member-1", "민지", "https://example.com/profiles/1.png", "https://example.com/verifications/1.jpg", 120),
        PartyFeedItemDto("member-2", "서연", "https://example.com/profiles/2.png", "https://example.com/verifications/2.jpg", 300),
        PartyFeedItemDto("member-3", "지호", "https://example.com/profiles/3.png", "https://example.com/verifications/3.jpg", 360),
        PartyFeedItemDto("member-4", "수아", "https://example.com/profiles/4.png", "https://example.com/verifications/4.jpg", 480),
        PartyFeedItemDto("current-user", "하늘", "https://example.com/profiles/5.png", null, null)
    )
}
