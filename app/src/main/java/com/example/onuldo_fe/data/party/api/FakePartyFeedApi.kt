package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dto.PartyFeedDto
import com.example.onuldo_fe.data.party.dto.PartyFeedItemDto
import com.example.onuldo_fe.data.party.dummy.FakePartyStore
import com.example.onuldo_fe.data.party.dummy.PartyFeedDummyData

class FakePartyFeedApi : PartyFeedApi {
    override suspend fun getPartyFeed(partyId: Long): PartyFeedDto {
        val party = FakePartyStore.getFeedSnapshot(partyId)
        val feedFixtureByUserId = PartyFeedDummyData.feedItems.associateBy { it.userId }
        // 실제 API 연동 전에는 파티별 실제 멤버를 기준으로 카드 수를 만들고 사진과 시간만 더미 fixture에서 사용
        val members = party.members.map { member ->
            val isVerified = member.userId in party.verifiedMemberIds
            val fixture = feedFixtureByUserId[member.userId]
            PartyFeedItemDto(
                userId = member.userId,
                nickname = member.nickname,
                profileImageUrl = member.profileImageUrl,
                isVerifiedToday = isVerified,
                verificationPhotoUrl = if (isVerified) fixture?.verificationPhotoUrl else null,
                verifiedAt = if (isVerified) fixture?.verifiedAt else null
            )
        }
        val total = members.size
        val verified = members.count { it.isVerifiedToday }
        return PartyFeedDto(
            partyId = partyId,
            name = party.partyName,
            challengeTitle = party.challengeName,
            progressRate = if (total == 0) 0.0 else verified.toDouble() / total,
            verifiedMemberCount = verified,
            totalMemberCount = total,
            members = members
        )
    }
}
