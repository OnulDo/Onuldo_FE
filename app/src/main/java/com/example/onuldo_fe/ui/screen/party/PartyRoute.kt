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
            onCreate = { screen = PartyScreen.WaitingLeader }
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
            ui = PartyWaitingRoomUi(),
            isLeader = true,
            onBack = { screen = PartyScreen.List },
            onPrimaryClick = { screen = PartyScreen.Feed }
        )
        PartyScreen.WaitingMember -> PartyWaitingRoomScreen(
            ui = PartyWaitingRoomUi(),
            isLeader = false,
            onBack = { screen = PartyScreen.List },
            onPrimaryClick = {}
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
