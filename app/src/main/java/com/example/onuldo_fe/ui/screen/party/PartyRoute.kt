package com.example.onuldo_fe.ui.screen.party

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.viewmodel.party.PartyChallengeSelectViewModel
import com.example.onuldo_fe.viewmodel.party.PartyInviteViewModel
import com.example.onuldo_fe.viewmodel.party.PartyFeedViewModel
import com.example.onuldo_fe.ui.screen.party.component.InviteCodeDialog
import androidx.compose.ui.tooling.preview.Preview
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.data.party.dummy.FakePartyInviteState
import com.example.onuldo_fe.data.party.dummy.FakePartyFeedState
import com.example.onuldo_fe.ui.screen.party.fake.PartyWaitingRoomFakeData
import com.example.onuldo_fe.ui.screen.party.fake.PartyWaitingRoomScenario

private enum class PartyScreen { List, Create, ChallengeSelect, ChallengeDetail, WaitingLeader, WaitingMember, Feed, Settlement }

// 다음 화면 전환 테스트용. 실제 생성 직후 상태를 확인할 때는 Recruiting으로 변경
private val developmentWaitingRoomScenario = PartyWaitingRoomScenario.ReadyToStart

@Composable
fun PartyRoute(
    challengeSelectViewModel: PartyChallengeSelectViewModel = viewModel(),
    inviteViewModel: PartyInviteViewModel = viewModel(),
    partyFeedViewModel: PartyFeedViewModel = viewModel()
) {
    var screen by remember { mutableStateOf(PartyScreen.List) }
    var showInviteDialog by remember { mutableStateOf(false) }
    var selectedChallenge by remember { mutableStateOf<PartyChallengeUi?>(null) }
    var partyName by remember { mutableStateOf("") }
    var capacity by remember { mutableIntStateOf(4) }
    var waitingMemberReady by remember { mutableStateOf(false) }
    var createdPeriod by remember { mutableStateOf("") }
    var createdDeposit by remember { mutableIntStateOf(0) }
    var leaderMembers by remember {
        mutableStateOf(PartyWaitingRoomFakeData.members(developmentWaitingRoomScenario, capacity))
    }
    var joinedPartyMembers by remember { mutableStateOf(PartyWaitingRoomUi().members) }

    when (screen) {
        PartyScreen.List -> PartyListScreen(
            parties = samplePartyCards.filter { it.status == PartyStatus.InProgress },
            onCreateClick = {
                partyName = ""
                selectedChallenge = null
                capacity = 4
                screen = PartyScreen.Create
            },
            onInviteCodeClick = {
                inviteViewModel.reset()
                showInviteDialog = true
            },
            onPartyClick = { screen = PartyScreen.Feed }
        )
        PartyScreen.Create -> PartyCreateScreen(
            partyName = partyName,
            onPartyNameChange = { partyName = it.take(20) },
            capacity = capacity,
            onCapacityChange = { capacity = it.coerceIn(2, 5) },
            selectedChallenge = selectedChallenge,
            onChallengeClick = { screen = PartyScreen.ChallengeSelect },
            onBack = { screen = PartyScreen.List },
            onCreate = { period, deposit ->
                createdPeriod = period
                createdDeposit = deposit
                leaderMembers = PartyWaitingRoomFakeData.members(developmentWaitingRoomScenario, capacity)
                FakePartyInviteState.activate("NEW123")
                screen = PartyScreen.WaitingLeader
            }
        )
        PartyScreen.ChallengeSelect -> PartyChallengeSelectScreen(
            challenges = challengeSelectViewModel.uiState.challenges,
            onSelect = { selectedChallenge = it },
            onConfirm = { screen = PartyScreen.ChallengeDetail }
        )
        PartyScreen.ChallengeDetail -> PartyChallengeDetailScreen(
            challenge = selectedChallenge ?: samplePartyChallenges.first(),
            onBack = { screen = PartyScreen.ChallengeSelect },
            onParticipate = { screen = PartyScreen.Create }
        )
        PartyScreen.WaitingLeader -> PartyWaitingRoomScreen(
            ui = PartyWaitingRoomUi(
                partyName = partyName,
                challengeName = selectedChallenge?.title.orEmpty(),
                inviteCode = "NEW123",
                period = createdPeriod,
                deposit = createdDeposit,
                capacity = capacity,
                members = leaderMembers
            ),
            isLeader = true,
            onBack = {
                // TODO 파티 탈퇴 API 연동 시 탈퇴 성공 후 목록 화면 이동
                leaderMembers = leaveParty(leaderMembers, "leader-current")
                if (leaderMembers.isEmpty()) FakePartyInviteState.markExpired("NEW123")
                screen = PartyScreen.List
            },
            onStartClick = {
                FakePartyInviteState.markStarted("NEW123")
                FakePartyFeedState.updateMemberCount(leaderMembers.size)
                partyFeedViewModel.loadPartyFeed("party-created")
                screen = PartyScreen.Feed
            }
        )
        PartyScreen.WaitingMember -> PartyWaitingRoomScreen(
            ui = PartyWaitingRoomUi(members = joinedPartyMembers),
            isLeader = false,
            availablePoint = 50_000,
            isCurrentUserReady = waitingMemberReady,
            onBack = {
                // TODO 파티 탈퇴 API 연동 시 탈퇴 성공 후 빈 슬롯 상태 갱신
                joinedPartyMembers = leaveParty(joinedPartyMembers, "member-current")
                waitingMemberReady = false
                screen = PartyScreen.List
            },
            onReadyClick = {
                // TODO 준비완료 API 연동 후 성공 응답 시 해당 파티원의 준비 상태 갱신
                joinedPartyMembers = joinedPartyMembers.map { member ->
                    if (member.id == "member-current") member.copy(readyStatus = PartyReadyStatus.Ready) else member
                }
                waitingMemberReady = true
            }
        )
        PartyScreen.Feed -> PartyFeedScreen(
            progress = partyFeedViewModel.uiState.progress,
            feedItems = partyFeedViewModel.uiState.feedItems,
            onBack = { screen = PartyScreen.List }
        )
        PartyScreen.Settlement -> PartySettlementScreen(onBack = { screen = PartyScreen.List })
    }

    if (showInviteDialog) {
        InviteCodeDialog(
            error = inviteViewModel.uiState.error,
            onDismiss = {
                showInviteDialog = false
                inviteViewModel.reset()
            },
            onRetry = inviteViewModel::reset,
            onSubmit = inviteViewModel::joinParty
        )
    }

    LaunchedEffect(inviteViewModel.uiState.joinedPartyId) {
        if (inviteViewModel.uiState.joinedPartyId != null) {
            showInviteDialog = false
            waitingMemberReady = false
            if (joinedPartyMembers.none { it.id == "member-current" }) {
                val nextOrder = (joinedPartyMembers.maxOfOrNull { it.joinedOrder } ?: 0) + 1
                joinedPartyMembers = joinedPartyMembers + PartyMemberUi(
                    name = "준호",
                    role = PartyMemberRole.Member,
                    readyStatus = PartyReadyStatus.Waiting,
                    id = "member-current",
                    joinedOrder = nextOrder
                )
            } else {
                joinedPartyMembers = joinedPartyMembers.map { member ->
                    if (member.id == "member-current") member.copy(readyStatus = PartyReadyStatus.Waiting) else member
                }
            }
            screen = PartyScreen.WaitingMember
            inviteViewModel.reset()
        }
    }
}

private fun leaveParty(members: List<PartyMemberUi>, leavingMemberId: String): List<PartyMemberUi> {
    val leavingMember = members.firstOrNull { it.id == leavingMemberId } ?: return members
    val remainingMembers = members.filterNot { it.id == leavingMemberId }
    if (remainingMembers.isEmpty() || leavingMember.role != PartyMemberRole.Leader) return remainingMembers

    val successorId = remainingMembers.minBy { it.joinedOrder }.id
    return remainingMembers.map { member ->
        if (member.id == successorId) {
            member.copy(role = PartyMemberRole.Leader, readyStatus = PartyReadyStatus.NotApplicable)
        } else member
    }
}

@Preview(name = "파티 전체 플로우", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartyRoutePreview() {
    OnulDo_FETheme { PartyRoute() }
}
