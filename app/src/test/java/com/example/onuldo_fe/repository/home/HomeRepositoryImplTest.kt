package com.example.onuldo_fe.repository.home

import com.example.onuldo_fe.data.home.dto.RealHomeDailyChallengeDto
import com.example.onuldo_fe.data.challenge.dto.DailyCompletedChallengeDto
import com.example.onuldo_fe.data.challenge.dto.DailyCompletedPartyDto
import com.example.onuldo_fe.data.challenge.dto.DailyCompletedResultDto
import com.example.onuldo_fe.data.party.dto.PartyHomeItemDto
import com.example.onuldo_fe.data.party.dto.PartyHomeMemberDto
import com.example.onuldo_fe.data.party.dto.PartyHomeResultDto
import com.example.onuldo_fe.data.party.dto.PartySettlementBannerDto
import com.example.onuldo_fe.model.home.ChallengeStatus
import com.example.onuldo_fe.model.home.HomeCompletedChallenge
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

        val result = items.toHomeData(
            now = LocalDateTime.of(2026, 8, 5, 22, 59),
            partyHome = PartyHomeResultDto(parties = listOf(partyHomeItem()))
        )

        assertEquals(1, result.challenges.size)
        assertEquals("30일 걷기", result.challenges.single().title)
        assertEquals(ChallengeStatus.NeedCertification, result.challenges.single().status)
        assertEquals(60, result.challenges.single().remainingMinutes)
        assertEquals(1, result.partyChallenges.size)
        assertEquals(ChallengeStatus.NeedCertification, result.partyChallenges.single().status)
        assertEquals(1, result.todayChallenge?.completedCount)
        assertEquals(2, result.todayChallenge?.totalCount)
    }

    @Test
    fun `서버에 없는 파티원 정보는 임의 생성하지 않는다`() {
        val result = listOf(dailyItem(type = "PARTY", name = "파티 챌린지", verified = false))
            .toHomeData(LocalDateTime.of(2026, 8, 5, 12, 0))

        assertEquals(emptyList<Any>(), result.partyChallenges)
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
    fun `홈 전용 파티 응답의 인원과 프로필을 카드에 반영한다`() {
        val item = dailyItem(type = "PARTY", name = "아침 운동", verified = false)

        val result = listOf(item)
            .toHomeData(
                now = LocalDateTime.of(2026, 8, 5, 12, 0),
                partyHome = PartyHomeResultDto(parties = listOf(partyHomeItem()))
            )
            .partyChallenges
            .single()

        assertEquals("갓생팟", result.title)
        assertEquals("아침 운동", result.subtitle)
        assertEquals(1, result.completedMemberCount)
        assertEquals(2, result.totalMemberCount)
        assertEquals("https://cdn/profile.png", result.members.first().profileImageUrl)
    }

    @Test
    fun `파티 인증하기에 필요한 challengeId와 category는 daily 매칭 없이 parties-home 응답 값을 그대로 쓴다`() {
        val withChallengeId = listOf(dailyItem(type = "PARTY", name = "아침 운동", verified = false))
            .toHomeData(
                now = LocalDateTime.of(2026, 8, 5, 12, 0),
                partyHome = PartyHomeResultDto(
                    parties = listOf(partyHomeItem(challengeId = 99, category = "운동"))
                )
            )
            .partyChallenges
            .single()

        assertEquals(99L, withChallengeId.challengeId)
        assertEquals("운동", withChallengeId.category)

        // 백엔드가 아직 challengeId를 안 내려주면 daily 쪽에 같은 partyId 항목이 있어도 null을 유지한다.
        val withoutChallengeId = listOf(dailyItem(type = "PARTY", name = "아침 운동", verified = false))
            .toHomeData(
                now = LocalDateTime.of(2026, 8, 5, 12, 0),
                partyHome = PartyHomeResultDto(parties = listOf(partyHomeItem()))
            )
            .partyChallenges
            .single()

        assertNull(withoutChallengeId.challengeId)
        assertEquals("", withoutChallengeId.category)
    }

    @Test
    fun `challengeId가 없으면 마감 전이라도 canVerify는 false다`() {
        val result = listOf(dailyItem(type = "PARTY", name = "아침 운동", verified = false))
            .toHomeData(
                now = LocalDateTime.of(2026, 8, 5, 12, 0),
                partyHome = PartyHomeResultDto(
                    // challengeId를 지정하지 않아 null인 상태 — 마감 전(showRemainingTime 기본 true)이라
                    // isDeadlinePassed는 false지만, challengeId가 없으니 canVerify도 false여야 한다.
                    parties = listOf(partyHomeItem(status = "NOT_VERIFIED"))
                )
            )
            .partyChallenges
            .single()

        assertNull(result.challengeId)
        assertFalse(result.canVerify)
    }

    @Test
    fun `홈 전용 파티 응답의 상태와 첫 정산 배너를 반영한다`() {
        val partyHome = PartyHomeResultDto(
            settlementBanners = listOf(
                PartySettlementBannerDto(10, "갓생팟"),
                PartySettlementBannerDto(20, "저녁팟")
            ),
            parties = listOf(
                partyHomeItem(
                    status = "PENDING",
                    verifiedAt = "2026-08-05T07:10:00",
                    showRemainingTime = true
                )
            )
        )

        val result = listOf(dailyItem(type = "PARTY", name = "아침 운동", verified = false))
            .toHomeData(
                now = LocalDateTime.of(2026, 8, 5, 12, 0),
                partyHome = partyHome
            )

        val party = result.partyChallenges.single()
        assertEquals(ChallengeStatus.WaitingReview, party.status)
        assertEquals("07:10", party.verifiedAt.toString())
        assertFalse(party.canVerify)
        assertNull(party.remainingMinutes)
        assertEquals("갓생팟", result.settlementBanner?.partyName)
        assertEquals(10L, result.settlementBanner?.partyId)
    }

    @Test
    fun `홈 파티 인증 상태를 화면 상태로 변환한다`() {
        val expectedStatuses = listOf(
            "NOT_VERIFIED" to ChallengeStatus.NeedCertification,
            "PENDING" to ChallengeStatus.WaitingReview,
            "SUCCESS" to ChallengeStatus.Success,
            "FAIL" to ChallengeStatus.Failed
        )

        expectedStatuses.forEach { (serverStatus, expectedStatus) ->
            val party = emptyList<RealHomeDailyChallengeDto>().toHomeData(
                now = LocalDateTime.of(2026, 8, 5, 12, 0),
                partyHome = PartyHomeResultDto(
                    parties = listOf(partyHomeItem(status = serverStatus))
                )
            ).partyChallenges.single()

            assertEquals(expectedStatus, party.status)
        }
    }

    @Test
    fun `서버가 요청한 경우에만 파티 인증 남은 시간을 표시한다`() {
        val daily = listOf(dailyItem(type = "PARTY", name = "아침 운동", verified = false))
        val now = LocalDateTime.of(2026, 8, 5, 23, 0)

        val visible = daily.toHomeData(
            now = now,
            partyHome = PartyHomeResultDto(
                parties = listOf(partyHomeItem(showRemainingTime = true))
            )
        ).partyChallenges.single()
        val hidden = daily.toHomeData(
            now = now,
            partyHome = PartyHomeResultDto(
                parties = listOf(partyHomeItem(showRemainingTime = false))
            )
        ).partyChallenges.single()

        assertEquals(59, visible.remainingMinutes)
        assertNull(hidden.remainingMinutes)
    }

    @Test
    fun `오늘 완료 목록을 개인과 파티 완료 카드로 변환한다`() {
        val completed = DailyCompletedResultDto(
            parties = listOf(DailyCompletedPartyDto(10, "갓생팟", 12, "2026-08-05T07:10:00", 3, 2)),
            challenges = listOf(DailyCompletedChallengeDto(1, 12, "매일 걷기", "2026-08-05T06:30:00", 8))
        )

        val result = emptyList<RealHomeDailyChallengeDto>()
            .toHomeData(LocalDateTime.of(2026, 8, 5, 12, 0), completed = completed)

        assertEquals(2, result.completedChallenges.size)
        assertEquals("06:30", result.completedChallenges.first().time)
        assertEquals("07:10", result.completedChallenges.last().time)
        val completedParty = result.completedChallenges.filterIsInstance<HomeCompletedChallenge.Party>().single()
        assertEquals(2, completedParty.completedMemberCount)
        assertEquals(3, completedParty.totalMemberCount)
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
        partyId = 10L.takeIf { type == "PARTY" },
        challengeId = 12,
        challengeName = name,
        timeStart = "06:00:00",
        timeEnd = "23:59:00",
        startDate = "2026-08-01",
        endDate = "2026-08-20",
        verifiedOnDate = verified,
        streakDays = streakDays
    )

    private fun partyHomeItem(
        status: String = "NOT_VERIFIED",
        verifiedAt: String? = null,
        showRemainingTime: Boolean = true,
        challengeId: Long? = null,
        category: String? = null
    ) = PartyHomeItemDto(
        partyId = 10,
        name = "갓생팟",
        challengeTitle = "아침 운동",
        endDate = "2026-08-20",
        verificationDeadline = "23:59:00",
        showRemainingTime = showRemainingTime,
        status = status,
        verifiedAt = verifiedAt,
        challengeId = challengeId,
        category = category,
        members = listOf(
            PartyHomeMemberDto(
                userId = 1,
                profileImageUrl = "https://cdn/profile.png",
                isVerifiedToday = true
            ),
            PartyHomeMemberDto(
                userId = 2,
                profileImageUrl = null,
                isVerifiedToday = false
            )
        )
    )
}
