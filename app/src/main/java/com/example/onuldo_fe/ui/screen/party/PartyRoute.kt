package com.example.onuldo_fe.ui.screen.party

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.viewmodel.party.PartyChallengeSelectViewModel
import com.example.onuldo_fe.ui.screen.party.component.InviteCodeDialog
import androidx.compose.ui.tooling.preview.Preview
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

private enum class PartyScreen { List, Create, ChallengeSelect, ChallengeDetail, WaitingLeader, WaitingMember, Feed, Settlement }

@Composable
fun PartyRoute(
    challengeSelectViewModel: PartyChallengeSelectViewModel = viewModel()
) {
    var screen by remember { mutableStateOf(PartyScreen.List) }
    var showInviteDialog by remember { mutableStateOf(false) }
    var inviteError by remember { mutableStateOf<InviteCodeError?>(null) }
    var selectedChallenge by remember { mutableStateOf<PartyChallengeUi?>(null) }
    var partyName by remember { mutableStateOf("") }
    var capacity by remember { mutableIntStateOf(4) }
    var waitingMemberReady by remember { mutableStateOf(false) }
    var createdPeriod by remember { mutableStateOf("") }
    var createdDeposit by remember { mutableIntStateOf(0) }

    when (screen) {
        PartyScreen.List -> PartyListScreen(
            parties = samplePartyCards,
            onCreateClick = {
                partyName = ""
                selectedChallenge = null
                capacity = 4
                screen = PartyScreen.Create
            },
            onInviteCodeClick = { showInviteDialog = true },
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
                period = createdPeriod,
                deposit = createdDeposit,
                capacity = capacity
            ),
            isLeader = true,
            onBack = { screen = PartyScreen.List },
            onStartClick = { screen = PartyScreen.Feed }
        )
        PartyScreen.WaitingMember -> PartyWaitingRoomScreen(
            ui = PartyWaitingRoomUi().let { waitingRoom ->
                waitingRoom.copy(
                    members = waitingRoom.members.mapIndexed { index, member ->
                        if (index == waitingRoom.members.lastIndex) {
                            member.copy(readyStatus = if (waitingMemberReady) PartyReadyStatus.Ready else PartyReadyStatus.Waiting)
                        } else member
                    }
                )
            },
            isLeader = false,
            availablePoint = 50_000,
            isCurrentUserReady = waitingMemberReady,
            onBack = { screen = PartyScreen.List },
            onReadyClick = {
                // TODO 준비완료 API 연동 후 성공 응답 시 해당 파티원의 준비 상태 갱신
                waitingMemberReady = true
            }
        )
        PartyScreen.Feed -> PartyFeedScreen(
            onBack = { screen = PartyScreen.List },
            onSettlementClick = { screen = PartyScreen.Settlement }
        )
        PartyScreen.Settlement -> PartySettlementScreen(onBack = { screen = PartyScreen.List })
    }

    if (showInviteDialog) {
        InviteCodeDialog(
            error = inviteError,
            onDismiss = { showInviteDialog = false; inviteError = null },
            onRetry = { inviteError = null },
            onSubmit = { code ->
                if (code.equals("82K3H9", ignoreCase = true)) {
                    showInviteDialog = false
                    screen = PartyScreen.WaitingMember
                } else {
                    inviteError = InviteCodeError.Invalid
                }
            }
        )
    }
}

@Preview(name = "파티 전체 플로우", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartyRoutePreview() {
    OnulDo_FETheme { PartyRoute() }
}
