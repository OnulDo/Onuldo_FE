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
import com.example.onuldo_fe.data.party.dummy.PartyInviteDummyData
import com.example.onuldo_fe.ui.screen.party.fake.PartyWaitingRoomFakeData
import com.example.onuldo_fe.ui.screen.party.fake.PartyWaitingRoomScenario

private enum class PartyScreen { List, Create, ChallengeSelect, ChallengeDetail, WaitingLeader, WaitingMember, Feed, Settlement }

// 다음 화면 전환 테스트용. 실제 생성 직후 상태를 확인할 때는 Recruiting으로 변경
// 대기방 UI 테스트 설정
// Recruiting: 파티 생성 직후처럼 방장만 표시하고 [시작하기] 버튼을 비활성화합니다.
// ReadyToStart: 준비완료 파티원을 채워 [시작하기] 버튼과 다음 화면 이동을 테스트합니다.
// 실제 파티 생성 직후 상태를 확인할 때는 Recruiting으로 변경합니다.
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
    var pendingChallenge by remember { mutableStateOf<PartyChallengeUi?>(null) }
    var partyName by remember { mutableStateOf("") }
    var capacity by remember { mutableIntStateOf(5) }
    var waitingMemberReady by remember { mutableStateOf(false) }
    var partyMembers by remember {
        mutableStateOf(PartyWaitingRoomFakeData.members(developmentWaitingRoomScenario, capacity))
    }
    var waitingPartyName by remember { mutableStateOf(PartyWaitingRoomUi().partyName) }
    var waitingChallengeName by remember { mutableStateOf(PartyWaitingRoomUi().challengeName) }
    var waitingInviteCode by remember { mutableStateOf(PartyWaitingRoomUi().inviteCode) }
    var waitingPeriod by remember { mutableStateOf(PartyWaitingRoomUi().period) }
    var waitingDeposit by remember { mutableIntStateOf(PartyWaitingRoomUi().deposit) }
    var waitingCapacity by remember { mutableIntStateOf(PartyWaitingRoomUi().capacity) }
    var feedPartyName by remember { mutableStateOf("") }
    var feedChallengeName by remember { mutableStateOf("") }

    val currentUserId = if (screen == PartyScreen.WaitingLeader) "leader-current" else "member-current"
    val isCurrentUserLeader =
        partyMembers.firstOrNull { it.id == currentUserId }?.role == PartyMemberRole.Leader

    val startParty = {
        FakePartyInviteState.markStarted(waitingInviteCode)
        FakePartyFeedState.updateMemberCount(partyMembers.size)
        partyFeedViewModel.loadPartyFeed("party-created")
        feedPartyName = waitingPartyName
        feedChallengeName = waitingChallengeName
        screen = PartyScreen.Feed
    }

    when (screen) {
        PartyScreen.List -> PartyListScreen(
            parties = samplePartyCards.filter { it.status == PartyStatus.InProgress },
            onCreateClick = {
                partyName = ""
                selectedChallenge = null
                pendingChallenge = null
                capacity = 5
                screen = PartyScreen.Create
            },
            onInviteCodeClick = {
                inviteViewModel.reset()
                showInviteDialog = true
            },
            onPartyClick = { partyId ->
                samplePartyCards.firstOrNull { it.id == partyId }?.let { party ->
                    feedPartyName = party.partyName
                    feedChallengeName = party.challengeName
                }
                partyFeedViewModel.loadPartyFeed(partyId)
                screen = PartyScreen.Feed
            }
        )
        PartyScreen.Create -> PartyCreateScreen(
            partyName = partyName,
            onPartyNameChange = { partyName = it.take(20) },
            capacity = capacity,
            onCapacityChange = { capacity = it.coerceIn(2, 5) },
            selectedChallenge = selectedChallenge,
            onChallengeClick = {
                pendingChallenge = null
                screen = PartyScreen.ChallengeSelect
            },
            onBack = { screen = PartyScreen.List },
            onCreate = { period, deposit ->
                waitingPartyName = partyName
                waitingChallengeName = selectedChallenge?.title.orEmpty()
                waitingInviteCode = PartyInviteDummyData.CREATED_PARTY_CODE
                waitingPeriod = period
                waitingDeposit = deposit
                waitingCapacity = capacity
                partyMembers = PartyWaitingRoomFakeData.members(developmentWaitingRoomScenario, capacity)
                FakePartyInviteState.registerActiveParty(
                    code = PartyInviteDummyData.CREATED_PARTY_CODE,
                    partyId = "party-created",
                    capacity = capacity,
                    memberCount = partyMembers.size
                )
                screen = PartyScreen.WaitingLeader
            }
        )
        PartyScreen.ChallengeSelect -> PartyChallengeSelectScreen(
            challenges = challengeSelectViewModel.uiState.challenges,
            onSelect = { pendingChallenge = it },
            onConfirm = { screen = PartyScreen.ChallengeDetail },
            onBack = {
                pendingChallenge = null
                screen = PartyScreen.Create
            }
        )
        PartyScreen.ChallengeDetail -> PartyChallengeDetailScreen(
            challenge = pendingChallenge ?: selectedChallenge ?: samplePartyChallenges.first(),
            onBack = { screen = PartyScreen.ChallengeSelect },
            onParticipate = {
                selectedChallenge = pendingChallenge
                pendingChallenge = null
                screen = PartyScreen.Create
            }
        )
        PartyScreen.WaitingLeader -> PartyWaitingRoomScreen(
            ui = PartyWaitingRoomUi(
                partyName = waitingPartyName,
                challengeName = waitingChallengeName,
                inviteCode = waitingInviteCode,
                period = waitingPeriod,
                deposit = waitingDeposit,
                capacity = waitingCapacity,
                members = partyMembers
            ),
            isLeader = isCurrentUserLeader,
            onBack = {
                // TODO 파티 탈퇴 API 연동 시 탈퇴 성공 후 목록 화면 이동
                partyMembers = leaveParty(partyMembers, currentUserId)
                if (partyMembers.isEmpty()) {
                    FakePartyInviteState.markExpired(waitingInviteCode)
                } else {
                    FakePartyInviteState.updateMemberCount(waitingInviteCode, partyMembers.size)
                }
                screen = PartyScreen.List
            },
            onStartClick = startParty
        )
        PartyScreen.WaitingMember -> PartyWaitingRoomScreen(
            ui = PartyWaitingRoomUi(
                partyName = waitingPartyName,
                challengeName = waitingChallengeName,
                inviteCode = waitingInviteCode,
                period = waitingPeriod,
                deposit = waitingDeposit,
                capacity = waitingCapacity,
                members = partyMembers
            ),
            isLeader = isCurrentUserLeader,
            availablePoint = 50_000,
            isCurrentUserReady = waitingMemberReady,
            onBack = {
                // TODO 파티 탈퇴 API 연동 시 탈퇴 성공 후 빈 슬롯 상태 갱신
                partyMembers = leaveParty(partyMembers, currentUserId)
                if (partyMembers.isEmpty()) {
                    FakePartyInviteState.markExpired(waitingInviteCode)
                } else {
                    FakePartyInviteState.updateMemberCount(waitingInviteCode, partyMembers.size)
                }
                waitingMemberReady = false
                screen = PartyScreen.List
            },
            onStartClick = startParty,
            onReadyClick = {
                // TODO 준비완료 API 연동 후 성공 응답 시 해당 파티원의 준비 상태 갱신
                partyMembers = partyMembers.map { member ->
                    if (member.id == currentUserId) member.copy(readyStatus = PartyReadyStatus.Ready) else member
                }
                waitingMemberReady = true
            }
        )
        PartyScreen.Feed -> PartyFeedScreen(
            partyName = feedPartyName,
            challengeName = feedChallengeName,
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
            if (partyMembers.none { it.id == "member-current" }) {
                val nextOrder = (partyMembers.maxOfOrNull { it.joinedOrder } ?: 0) + 1
                partyMembers = partyMembers + PartyMemberUi(
                    name = "준호",
                    role = PartyMemberRole.Member,
                    readyStatus = PartyReadyStatus.Waiting,
                    id = "member-current",
                    joinedOrder = nextOrder
                )
            } else {
                partyMembers = partyMembers.map { member ->
                    if (member.id == "member-current") member.copy(readyStatus = PartyReadyStatus.Waiting) else member
                }
            }
            // fake API에서 예약한 참여 인원과 실제 화면의 멤버 목록을 동일하게 맞춥니다.
            FakePartyInviteState.updateMemberCount(waitingInviteCode, partyMembers.size)
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
