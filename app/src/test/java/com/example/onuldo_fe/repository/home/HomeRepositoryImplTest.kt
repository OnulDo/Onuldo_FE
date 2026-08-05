package com.example.onuldo_fe.repository.home

import com.example.onuldo_fe.data.home.dto.RealHomeDailyChallengeDto
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

    private fun dailyItem(
        type: String,
        name: String,
        verified: Boolean
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
        verifiedOnDate = verified
    )
}
