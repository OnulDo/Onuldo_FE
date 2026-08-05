package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.PartyFeedApi
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
    private val fakeApi: PartyFeedApi,
    private val realApi: RealPartyApi,
    private val useRealPartyFeedApi: Boolean
) : PartyFeedRepository {
    override suspend fun getPartyFeed(partyId: String): PartyFeed {
        // 피드 조회만 독립적으로 전환해 아직 Fake인 다른 파티 API에 영향을 주지 않는다.
        if (!useRealPartyFeedApi) return fakeApi.getPartyFeed(partyId.toLong()).toModel()

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
