package com.example.onuldo_fe.ui.screen.party

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.data.party.dummy.PartyTestConfig
import com.example.onuldo_fe.model.party.CreatePartyCommand
import com.example.onuldo_fe.ui.screen.party.component.InviteCodeDialog
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.viewmodel.party.PartyAction
import com.example.onuldo_fe.viewmodel.party.PartyChallengeSelectViewModel
import com.example.onuldo_fe.viewmodel.party.PartyFeedViewModel
import com.example.onuldo_fe.viewmodel.party.PartyInviteViewModel
import com.example.onuldo_fe.viewmodel.party.PartyViewModel

private enum class PartyScreen {
    List,
    Create,
    ChallengeSelect,
    ChallengeDetail,
    WaitingRoom,
    Feed,
    Settlement
}

@Composable
fun PartyRoute(
    partyViewModel: PartyViewModel = viewModel(),
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
    var feedPartyId by remember { mutableStateOf("party-001") }
    var waitingPartyId by remember { mutableStateOf<String?>(null) }

    val partyState = partyViewModel.uiState
    val waitingRoom = partyState.waitingRoom
    val currentMember = waitingRoom?.members?.firstOrNull { it.id == partyViewModel.currentUserId }
    val isCurrentUserLeader = currentMember?.role == PartyMemberRole.Leader
    val isCurrentUserReady = currentMember?.readyStatus == PartyReadyStatus.Ready

    when (screen) {
        PartyScreen.List -> PartyListScreen(
            parties = partyState.parties.filter { it.status == PartyStatus.InProgress },
            isLoading = partyState.isListLoading,
            errorMessage = partyState.errorMessage,
            onRetry = partyViewModel::loadParties,
            onCreateClick = {
                partyViewModel.clearError()
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
                feedPartyId = partyId
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
            onBack = {
                partyViewModel.clearError()
                screen = PartyScreen.List
            },
            isSubmitting = partyState.action == PartyAction.Creating,
            errorMessage = partyState.errorMessage,
            // fake 포인트 부족 테스트는 PartyTestConfig.AVAILABLE_POINT를 5_000으로 변경합니다.
            availablePoint = PartyTestConfig.AVAILABLE_POINT,
            onCreate = { period, deposit ->
                val challenge = selectedChallenge ?: return@PartyCreateScreen
                partyViewModel.createParty(
                    command = CreatePartyCommand(
                        name = partyName,
                        challengeId = challenge.id,
                        challengeName = challenge.title,
                        period = period,
                        deposit = deposit,
                        capacity = capacity
                    ),
                    onSuccess = { partyId ->
                        waitingPartyId = partyId
                        screen = PartyScreen.WaitingRoom
                    }
                )
            }
        )

        PartyScreen.ChallengeSelect -> PartyChallengeSelectScreen(
            challenges = challengeSelectViewModel.uiState.challenges,
            isLoading = challengeSelectViewModel.uiState.isLoading,
            errorMessage = challengeSelectViewModel.uiState.errorMessage,
            onRetry = challengeSelectViewModel::loadPartyChallenges,
            onSelect = { pendingChallenge = it },
            onConfirm = { screen = PartyScreen.ChallengeDetail },
            onBack = {
                pendingChallenge = null
                screen = PartyScreen.Create
            }
        )

        PartyScreen.ChallengeDetail -> {
            val challenge = pendingChallenge ?: selectedChallenge
            if (challenge == null) {
                LaunchedEffect(Unit) { screen = PartyScreen.ChallengeSelect }
            } else {
                PartyChallengeDetailScreen(
                    challenge = challenge,
                    onBack = { screen = PartyScreen.ChallengeSelect },
                    onParticipate = {
                        selectedChallenge = pendingChallenge
                        pendingChallenge = null
                        screen = PartyScreen.Create
                    }
                )
            }
        }

        PartyScreen.WaitingRoom -> {
            if (waitingRoom == null) {
                PartyLoadingScreen(
                    errorMessage = partyState.errorMessage,
                    onRetry = {
                        waitingPartyId?.let(partyViewModel::loadWaitingRoom)
                    },
                    onBack = {
                        partyViewModel.clearError()
                        screen = PartyScreen.List
                    }
                )
            } else {
                PartyWaitingRoomScreen(
                    ui = waitingRoom,
                    isLeader = isCurrentUserLeader,
                    // 파티원 준비완료 시에도 파티 생성과 동일한 fake 포인트 설정을 사용합니다.
                    availablePoint = PartyTestConfig.AVAILABLE_POINT,
                    isCurrentUserReady = isCurrentUserReady,
                    isActionInProgress = partyState.action != PartyAction.Idle,
                    errorMessage = partyState.errorMessage,
                    onBack = {
                        partyViewModel.leaveParty { screen = PartyScreen.List }
                    },
                    onStartClick = {
                        // 시작 API 성공 후 파티 피드를 새로 조회합니다.
                        partyViewModel.startParty { partyId ->
                            // TODO 테스트 종료 후 명세대로 홈 이동으로 교체합니다.
                            feedPartyId = partyId
                            partyFeedViewModel.loadPartyFeed(partyId)
                            screen = PartyScreen.Feed
                        }
                    },
                    onReadyClick = partyViewModel::readyParty
                )
            }
        }

        PartyScreen.Feed -> PartyFeedScreen(
            partyName = partyFeedViewModel.uiState.partyName,
            challengeName = partyFeedViewModel.uiState.challengeName,
            progress = partyFeedViewModel.uiState.progress,
            feedItems = partyFeedViewModel.uiState.feedItems,
            isLoading = partyFeedViewModel.uiState.isLoading,
            errorMessage = partyFeedViewModel.uiState.errorMessage,
            onRetry = { partyFeedViewModel.loadPartyFeed(feedPartyId) },
            onBack = { screen = PartyScreen.List }
        )

        PartyScreen.Settlement -> PartySettlementScreen(onBack = { screen = PartyScreen.List })
    }

    if (showInviteDialog) {
        InviteCodeDialog(
            error = inviteViewModel.uiState.error,
            isSubmitting = inviteViewModel.uiState.isJoining,
            networkErrorMessage = inviteViewModel.uiState.networkErrorMessage,
            onDismiss = {
                showInviteDialog = false
                inviteViewModel.reset()
            },
            onRetry = inviteViewModel::reset,
            onSubmit = inviteViewModel::joinParty
        )
    }

    LaunchedEffect(inviteViewModel.uiState.joinedPartyId) {
        inviteViewModel.uiState.joinedPartyId?.let { partyId ->
            // 초대코드 참여 성공 후 partyId로 대기방 전체 정보를 다시 조회합니다.
            showInviteDialog = false
            waitingPartyId = partyId
            screen = PartyScreen.WaitingRoom
            partyViewModel.loadWaitingRoom(partyId)
            inviteViewModel.reset()
        }
    }
}

@Composable
private fun PartyLoadingScreen(
    errorMessage: String?,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        if (errorMessage == null) {
            androidx.compose.material3.CircularProgressIndicator(
                color = com.example.onuldo_fe.ui.theme.Persimmon
            )
        } else {
            androidx.compose.material3.Text(errorMessage)
            androidx.compose.material3.TextButton(onClick = onRetry) {
                androidx.compose.material3.Text("다시 시도")
            }
            androidx.compose.material3.TextButton(onClick = onBack) {
                androidx.compose.material3.Text("목록으로")
            }
        }
    }
}

@Preview(name = "파티 전체 플로우", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartyRoutePreview() {
    OnulDo_FETheme { PartyRoute() }
}
