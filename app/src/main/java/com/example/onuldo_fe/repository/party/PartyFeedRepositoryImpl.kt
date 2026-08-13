package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.RealPartyApi
import com.example.onuldo_fe.data.party.dto.PartyFeedDto
import com.example.onuldo_fe.data.party.dto.PartyFeedItemDto
import com.example.onuldo_fe.model.party.PartyFeed
import com.example.onuldo_fe.model.party.PartyFeedItem
import com.example.onuldo_fe.model.party.PartyProgress
import java.time.Duration
import java.time.LocalDateTime
import retrofit2.HttpException
import java.io.IOException

class PartyFeedRepositoryImpl(
    private val realApi: RealPartyApi
) : PartyFeedRepository {
    override suspend fun getPartyFeed(partyId: String): PartyFeed {
        val response = realApi.getPartyFeed(partyId.toLong())
        if (!response.isSuccessful) throw HttpException(response)
        val body = response.body() ?: throw IOException("파티 피드 응답 본문이 비어 있습니다.")
        return body.result.toModel()
    }
}

private fun PartyFeedDto.toModel() = PartyFeed(
    partyId = partyId.toString(),
    partyName = name,
    challengeName = challengeTitle,
    progress = PartyProgress(
        progressRate = progressRate,
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
    isVerifiedToday = isVerifiedToday,
    verifiedElapsedMinutes = verifiedAt?.toElapsedMinutes(),
)

private fun String.toElapsedMinutes(): Int? = runCatching {
    Duration.between(LocalDateTime.parse(this), LocalDateTime.now()).toMinutes().coerceAtLeast(0).toInt()
}.getOrNull()
