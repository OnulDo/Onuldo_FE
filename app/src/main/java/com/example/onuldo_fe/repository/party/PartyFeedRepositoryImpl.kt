package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.PartyFeedApi
import com.example.onuldo_fe.data.party.dto.PartyProgressDto
import com.example.onuldo_fe.data.party.dto.PartyFeedItemDto
import com.example.onuldo_fe.data.party.dto.PartyFeedDto
import com.example.onuldo_fe.model.party.PartyFeed
import com.example.onuldo_fe.model.party.PartyFeedItem
import com.example.onuldo_fe.model.party.PartyProgress

// 파티 피드 API 응답을 진행 현황과 인증 목록 모델로 변환
class PartyFeedRepositoryImpl(
    private val api: PartyFeedApi
) : PartyFeedRepository {
    override suspend fun getPartyFeed(partyId: String): PartyFeed =
        api.getPartyFeed(partyId).toModel()
}

private fun PartyFeedDto.toModel() = PartyFeed(
    partyId = partyId,
    partyName = partyName,
    challengeName = challengeName,
    progress = progress.toModel(),
    items = items.map(PartyFeedItemDto::toModel)
)

private fun PartyProgressDto.toModel() = PartyProgress(
    completedMemberCount = completedMemberCount,
    totalMemberCount = totalMemberCount
)

private fun PartyFeedItemDto.toModel() = PartyFeedItem(
    memberId = memberId,
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    verificationImageUrl = verificationImageUrl,
    verifiedElapsedMinutes = verifiedElapsedMinutes,
    defaultCharacterId = defaultCharacterId
)
