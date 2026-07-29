package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.PartyFeedApi
import com.example.onuldo_fe.data.party.dto.PartyFeedDto
import com.example.onuldo_fe.data.party.dto.PartyFeedItemDto
import com.example.onuldo_fe.model.party.PartyFeed
import com.example.onuldo_fe.model.party.PartyFeedItem
import com.example.onuldo_fe.model.party.PartyProgress
import java.time.Duration
import java.time.LocalDateTime

class PartyFeedRepositoryImpl(private val api: PartyFeedApi) : PartyFeedRepository {
    override suspend fun getPartyFeed(partyId: String): PartyFeed =
        api.getPartyFeed(partyId.toLong()).toModel()
}

private fun PartyFeedDto.toModel() = PartyFeed(
    partyId = partyId.toString(),
    partyName = name,
    challengeName = challengeTitle,
    progress = PartyProgress(
        completedMemberCount = verifiedMemberCount,
        totalMemberCount = totalMemberCount
    ),
    items = members.map(PartyFeedItemDto::toModel)
)

private fun PartyFeedItemDto.toModel() = PartyFeedItem(
    memberId = userId.toString(),
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    verificationImageUrl = verificationPhotoUrl,
    verifiedElapsedMinutes = verifiedAt?.toElapsedMinutes(),
    // TODO: 서버가 기본 캐릭터 정보를 제공하면 userId 기반 임시 배정을 제거한다.
    defaultCharacterId = ((userId % 9) + 1).toInt()
)

private fun String.toElapsedMinutes(): Int? = runCatching {
    Duration.between(LocalDateTime.parse(this), LocalDateTime.now()).toMinutes().coerceAtLeast(0).toInt()
}.getOrNull()
