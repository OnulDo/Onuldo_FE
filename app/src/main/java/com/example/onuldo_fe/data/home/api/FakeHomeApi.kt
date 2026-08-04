package com.example.onuldo_fe.data.home.api

import com.example.onuldo_fe.data.home.dto.HomePartyChallengeDto
import com.example.onuldo_fe.data.home.dto.HomeResponseDto
import com.example.onuldo_fe.data.home.dummy.FakeHomeScenario
import com.example.onuldo_fe.data.home.dummy.HomeDummyData
import com.example.onuldo_fe.data.party.dummy.FakePartyStore

class FakeHomeApi(
    private val scenario: FakeHomeScenario = FakeHomeScenario.Default
) : HomeApi {
    override fun getHome(): HomeResponseDto {
        // 선택한 시나리오의 더미 API 응답 반환
        return when (scenario) {
            FakeHomeScenario.Default -> defaultHomeWithCreatedParties()
            FakeHomeScenario.Empty -> HomeDummyData.empty
            FakeHomeScenario.AllCompleted -> HomeDummyData.allCompleted
        }
    }

    private fun defaultHomeWithCreatedParties(): HomeResponseDto {
        val base = HomeDummyData.withChallenges
        val createdParties = FakePartyStore.getCreatedInProgressParties()
            .map { party ->
                HomePartyChallengeDto(
                    title = party.name,
                    subtitle = party.goal,
                    remainingDays = party.dDay,
                    deadlineAt = party.deadline.orEmpty(),
                    completedMemberCount = party.verifiedToday,
                    totalMemberCount = party.totalMembers
                )
            }

        // 파티 시작 후 저장된 항목을 홈 상단에 추가하고 오늘 활동 분모도 함께 갱신
        return base.copy(
            todayChallenge = base.todayChallenge?.copy(
                totalCount = base.todayChallenge.totalCount + createdParties.size
            ),
            partyChallenges = createdParties + base.partyChallenges
        )
    }
}
