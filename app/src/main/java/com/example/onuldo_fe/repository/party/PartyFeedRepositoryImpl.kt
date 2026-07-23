package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.PartyFeedApi
import com.example.onuldo_fe.data.party.dto.PartyProgressDto
import com.example.onuldo_fe.data.party.dto.PartyFeedItemDto
import com.example.onuldo_fe.model.party.PartyFeedItem
import com.example.onuldo_fe.model.party.PartyProgress

class PartyFeedRepositoryImpl(
    private val api: PartyFeedApi
) : PartyFeedRepository {
    override fun getPartyProgress(partyId: String): PartyProgress =
        api.getPartyProgress(partyId).toModel()

    override fun getPartyFeedItems(partyId: String): List<PartyFeedItem> =
        api.getPartyFeedItems(partyId).map(PartyFeedItemDto::toModel)
}

private fun PartyProgressDto.toModel() = PartyProgress(
    completedMemberCount = completedMemberCount,
    totalMemberCount = totalMemberCount
)

private fun PartyFeedItemDto.toModel() = PartyFeedItem(
    memberId = memberId,
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    verificationImageUrl = verificationImageUrl,
    verifiedElapsedMinutes = verifiedElapsedMinutes
)
