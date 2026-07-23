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
        // 파티 생성 직후 상태 확인용: 방장만 참여하여 시작하기 버튼 비활성화
        PartyWaitingRoomScenario.Recruiting -> listOf(leader)

        // 다음 화면 전환 테스트용: 준비완료 파티원을 추가하여 시작하기 버튼 활성화
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
