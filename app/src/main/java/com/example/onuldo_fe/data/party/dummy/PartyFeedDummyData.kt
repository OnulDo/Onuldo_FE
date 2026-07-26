package com.example.onuldo_fe.data.party.dummy

import com.example.onuldo_fe.data.party.dto.PartyFeedItemDto
import com.example.onuldo_fe.data.party.dto.PartyProgressDto

object PartyFeedDummyData {
    val progress = PartyProgressDto(
        completedMemberCount = 4,
        totalMemberCount = 5
    )

    val feedItems = listOf(
        PartyFeedItemDto("member-1", "민지", null, "https://example.com/verifications/1.jpg", 120, 4),
        PartyFeedItemDto("member-2", "서연", null, "https://example.com/verifications/2.jpg", 300, 2),
        PartyFeedItemDto("member-3", "지호", null, "https://example.com/verifications/3.jpg", 360, 5),
        PartyFeedItemDto("member-4", "수아", null, "https://example.com/verifications/4.jpg", 480, 3),
        PartyFeedItemDto("current-user", "하늘", null, null, null, 8)
    )
}
