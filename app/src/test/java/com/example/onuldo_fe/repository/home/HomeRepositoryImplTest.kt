package com.example.onuldo_fe.repository.home

import com.example.onuldo_fe.data.home.dto.RealHomeDailyChallengeDto
import com.example.onuldo_fe.data.challenge.dto.DailyCompletedChallengeDto
import com.example.onuldo_fe.data.challenge.dto.DailyCompletedPartyDto
import com.example.onuldo_fe.data.challenge.dto.DailyCompletedResultDto
import com.example.onuldo_fe.data.party.dto.PartyFeedDto
import com.example.onuldo_fe.data.party.dto.PartyFeedItemDto
import com.example.onuldo_fe.model.home.ChallengeStatus
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class HomeRepositoryImplTest {

    @Test
    fun `daily 응답을 개인과 파티 홈 카드로 분리한다`() {
        val items = listOf(
            dailyItem(type = "PERSONAL", name = "30일 걷기", verified = false),
            dailyItem(type = "PARTY", name = "아침 러닝", verified = true)
        )

        val result = items.toHomeData(LocalDateTime.of(2026, 8, 5, 22, 59))

        assertEquals(1, result.challenges.size)
        assertEquals("30일 걷기", result.challenges.single().title)
        assertEquals(ChallengeStatus.NeedCertification, result.challenges.single().status)
        assertEquals(60, result.challenges.single().remainingMinutes)
        assertEquals(1, result.partyChallenges.size)
        assertEquals(ChallengeStatus.Success, result.partyChallenges.single().status)
        assertEquals(1, result.todayChallenge?.completedCount)
        assertEquals(2, result.todayChallenge?.totalCount)
    }

    @Test
    fun `서버에 없는 파티원 정보는 임의 생성하지 않는다`() {
        val result = listOf(dailyItem(type = "PARTY", name = "파티 챌린지", verified = false))
            .toHomeData(LocalDateTime.of(2026, 8, 5, 12, 0))
            .partyChallenges
            .single()

        assertEquals(0, result.completedMemberCount)
        assertEquals(0, result.totalMemberCount)
        assertEquals(emptyList<Any>(), result.members)
    }

    @Test
    fun `오늘 챌린지가 없으면 진행 현황을 만들지 않는다`() {
        val result = emptyList<RealHomeDailyChallengeDto>()
            .toHomeData(LocalDateTime.of(2026, 8, 5, 12, 0))

        assertNull(result.todayChallenge)
        assertFalse(result.partyChallenges.isNotEmpty())
        assertFalse(result.challenges.isNotEmpty())
    }

    @Test
    fun `미인증 상태로 마감 시각이 지나면 실패로 표시한다`() {
        val result = listOf(dailyItem(type = "PERSONAL", name = "아침 운동", verified = false))
            .toHomeData(LocalDateTime.of(2026, 8, 5, 23, 59, 1))
            .challenges
            .single()

        assertEquals(ChallengeStatus.Failed, result.status)
        assertEquals(false, result.canVerify)
        assertNull(result.remainingMinutes)
    }

    @Test
    fun `연속 성공 일수를 개인 챌린지 카드에 전달한다`() {
        val result = listOf(
            dailyItem(type = "PERSONAL", name = "매일 걷기", verified = false, streakDays = 12)
        ).toHomeData(LocalDateTime.of(2026, 8, 5, 12, 0))

        assertEquals(12, result.challenges.single().streakDays)
    }

    @Test
    fun `partyId가 있으면 파티 피드의 인원과 프로필을 카드에 반영한다`() {
        val item = dailyItem(type = "PARTY", name = "아침 운동", verified = false)
            .copy(partyId = 10, partyName = "갓생팟")
        val feed = PartyFeedDto(
            partyId = 10,
            name = "갓생팟",
            challengeTitle = "아침 운동",
            progressRate = 0.5,
            verifiedMemberCount = 1,
            totalMemberCount = 2,
            members = listOf(
                PartyFeedItemDto(1, "오늘두", "https://cdn/profile.png", true, null, null)
            )
        )

        val result = listOf(item)
            .toHomeData(LocalDateTime.of(2026, 8, 5, 12, 0), mapOf(10L to feed))
            .partyChallenges
            .single()

        assertEquals("갓생팟", result.title)
        assertEquals("아침 운동", result.subtitle)
        assertEquals(1, result.completedMemberCount)
        assertEquals(2, result.totalMemberCount)
        assertEquals("https://cdn/profile.png", result.members.single().profileImageUrl)
    }

    @Test
    fun `오늘 완료 목록을 개인과 파티 완료 카드로 변환한다`() {
        val completed = DailyCompletedResultDto(
            parties = listOf(DailyCompletedPartyDto(10, "갓생팟", 12, "2026-08-05T06:30:00", 3, 2)),
            challenges = listOf(DailyCompletedChallengeDto(1, 12, "매일 걷기", "2026-08-05T07:10:00", 8))
        )

        val result = emptyList<RealHomeDailyChallengeDto>()
            .toHomeData(LocalDateTime.of(2026, 8, 5, 12, 0), completed = completed)

        assertEquals(2, result.completedChallenges.size)
        assertEquals("06:30", result.completedChallenges.first().time)
        assertEquals("07:10", result.completedChallenges.last().time)
    }

    private fun dailyItem(
        type: String,
        name: String,
        verified: Boolean,
        streakDays: Int? = null
    ) = RealHomeDailyChallengeDto(
        participationId = 1,
        participationStatus = "ONGOING",
        participationType = type,
        challengeId = 12,
        challengeName = name,
        timeStart = "06:00:00",
        timeEnd = "23:59:00",
        startDate = "2026-08-01",
        endDate = "2026-08-20",
        verifiedOnDate = verified,
        streakDays = streakDays
    )
}
