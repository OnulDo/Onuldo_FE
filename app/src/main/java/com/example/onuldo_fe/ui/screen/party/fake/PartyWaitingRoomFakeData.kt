package com.example.onuldo_fe.ui.screen.party.fake

import com.example.onuldo_fe.ui.screen.party.PartyMemberRole
import com.example.onuldo_fe.ui.screen.party.PartyMemberUi
import com.example.onuldo_fe.ui.screen.party.PartyReadyStatus

enum class PartyWaitingRoomScenario {
    Recruiting,
    ReadyToStart
}

object PartyWaitingRoomFakeData {
    fun members(scenario: PartyWaitingRoomScenario, capacity: Int = 5): List<PartyMemberUi> = when (scenario) {
        // 파티 생성 직후 상태 확인용: 방장만 반환하고 나머지는 빈 모집 슬롯으로 표시합니다.
        PartyWaitingRoomScenario.Recruiting -> listOf(leader)

        // 화면 전환 테스트용: 모집 인원에 맞춰 준비완료 파티원을 반환하여 시작하기 버튼을 활성화합니다.
        PartyWaitingRoomScenario.ReadyToStart ->
            listOf(leader) + readyMembers.take((capacity - 1).coerceIn(1, readyMembers.size))
    }

    private val leader = PartyMemberUi(
        name = "민지",
        role = PartyMemberRole.Leader,
        readyStatus = PartyReadyStatus.NotApplicable,
        id = "leader-current",
        joinedOrder = 0
    )

    private val readyMembers = listOf(
        PartyMemberUi("서연", PartyMemberRole.Member, PartyReadyStatus.Ready, "member-test-1", 1),
        PartyMemberUi("지호", PartyMemberRole.Member, PartyReadyStatus.Ready, "member-test-2", 2),
        PartyMemberUi("수아", PartyMemberRole.Member, PartyReadyStatus.Ready, "member-test-3", 3),
        PartyMemberUi("도윤", PartyMemberRole.Member, PartyReadyStatus.Ready, "member-test-4", 4)
    )
}
