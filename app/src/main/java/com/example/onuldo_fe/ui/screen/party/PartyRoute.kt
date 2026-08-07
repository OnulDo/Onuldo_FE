package com.example.onuldo_fe.ui.screen.party

import android.Manifest
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.data.party.dummy.PartyTestConfig
import com.example.onuldo_fe.data.party.config.PartyApiConfig
import com.example.onuldo_fe.model.party.CreatePartyCommand
import com.example.onuldo_fe.ui.screen.challenge.detail.DetailRoute
import com.example.onuldo_fe.ui.component.PermissionDialogType
import com.example.onuldo_fe.ui.component.PermissionSettingDialog
import com.example.onuldo_fe.ui.screen.party.components.InviteCodeDialog
import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge
import com.example.onuldo_fe.ui.screen.challenge.gallery.GalleryRoute
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.util.moveToAppSettings
import com.example.onuldo_fe.viewmodel.party.PartyAction
import com.example.onuldo_fe.viewmodel.party.PartyFeedViewModel
import com.example.onuldo_fe.viewmodel.party.PartyInviteViewModel
import com.example.onuldo_fe.viewmodel.party.PartySettlementViewModel
import com.example.onuldo_fe.viewmodel.party.PartyStatus
import com.example.onuldo_fe.viewmodel.party.PartyViewModel
import java.text.Normalizer

// Navigation 라이브러리 연동 전 파티 내부 화면 전환을 구분하는 테스트용 화면 상태
private enum class PartyScreen {
    List,
    Create,
    ChallengeSelect,
    ChallengeDetail,
    WaitingRoom,
    Feed,
    Settlement
}

// 파티 목록부터 생성·참여·대기방·피드까지 화면 전환과 ViewModel 상태 연결
@Composable
fun PartyRoute(
    partyViewModel: PartyViewModel = viewModel(),
    inviteViewModel: PartyInviteViewModel = viewModel(),
    partyFeedViewModel: PartyFeedViewModel = viewModel(),
    partySettlementViewModel: PartySettlementViewModel = viewModel(),
    onBottomBarVisibilityChange: (Boolean) -> Unit = {},
    onCameraNavigate: () -> Unit = {},
    onChargePoint: () -> Unit = {},
    onHomeNavigate: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 현재 화면과 다이얼로그 노출 여부는 Route에서만 관리
    var screen by remember { mutableStateOf(PartyScreen.List) }
    var showInviteDialog by remember { mutableStateOf(false) }
    var showCameraPermissionDialog by rememberSaveable {
        mutableStateOf(false)
    }
    // pendingChallenge는 상세 확인 중인 임시 선택, selectedChallenge는 생성 화면에서 확정된 선택
    var selectedChallenge by remember { mutableStateOf<Challenge?>(null) }
    var pendingChallenge by remember { mutableStateOf<Challenge?>(null) }

    // 생성 화면을 벗어나 챌린지를 탐색해도 입력값을 유지하도록 Route가 생성 폼 상태 보관
    var partyName by remember { mutableStateOf("") }
    var capacity by remember { mutableIntStateOf(5) }

    // 피드 재조회와 대기방 오류 재시도에 사용할 마지막 partyId 보관
    var feedPartyId by remember { mutableStateOf("1") }
    var waitingPartyId by remember { mutableStateOf<String?>(null) }

    val partyState = partyViewModel.uiState
    val waitingRoom = partyState.waitingRoom

    fun handleVerifyClick() {
        val isCameraPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (isCameraPermissionGranted) {
            onCameraNavigate()
        } else {
            showCameraPermissionDialog = true
        }
    }

    DisposableEffect(lifecycleOwner, showCameraPermissionDialog) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && showCameraPermissionDialog) {
                val isCameraPermissionGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (isCameraPermissionGranted) {
                    showCameraPermissionDialog = false
                    onCameraNavigate()
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DisposableEffect(lifecycleOwner, screen, waitingPartyId, waitingRoom != null) {
        val partyId = waitingPartyId
        val shouldPoll = screen == PartyScreen.WaitingRoom && partyId != null && waitingRoom != null
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> if (shouldPoll) {
                    partyId?.let(partyViewModel::startWaitingRoomPolling)
                }
                Lifecycle.Event.ON_STOP -> partyViewModel.stopWaitingRoomPolling()
                else -> Unit
            }
        }

        // 대기방 진입 시 바로 갱신하고, 백그라운드에서는 요청을 멈춘다.
        if (shouldPoll && lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            partyId?.let(partyViewModel::startWaitingRoomPolling)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            partyViewModel.stopWaitingRoomPolling()
        }
    }

    LaunchedEffect(screen, waitingRoom?.status) {
        if (screen == PartyScreen.WaitingRoom && waitingRoom?.status == PartyStatus.InProgress) {
            // 다른 사용자가 파티를 시작하면 폴링을 멈추고 홈으로 이동한다.
            partyViewModel.stopWaitingRoomPolling()
            onHomeNavigate()
        }
    }

    LaunchedEffect(screen) {
        onBottomBarVisibilityChange(
            screen == PartyScreen.List || screen == PartyScreen.Feed
        )
    }
    DisposableEffect(Unit) {
        onDispose { onBottomBarVisibilityChange(true) }
    }

    when (screen) {
        PartyScreen.List -> PartyListScreen(
            // 정책상 파티 홈에는 모집 중 파티를 제외하고 진행 중 파티만 노출
            parties = partyState.parties.filter { it.status == PartyStatus.InProgress },
            onVerifyClick = ::handleVerifyClick,
            isLoading = partyState.isListLoading,
            errorMessage = partyState.errorMessage,
            onRetry = partyViewModel::loadParties,
            onCreateClick = {
                // 새 파티 만들기 시작 시 이전 생성 폼의 임시 값을 모두 초기화
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
                // 진행 중 파티 카드는 해당 파티 피드로만 이동
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
            // API category Enum에 연결된 화면 표시명을 선택 카드의 카테고리 칩에 전달
            selectedChallengeCategoryLabel = selectedChallenge?.category?.displayName,
            onChallengeClick = {
                // 기존 확정 선택은 유지하고 새로 탐색할 임시 선택만 초기화
                pendingChallenge = null
                screen = PartyScreen.ChallengeSelect
            },
            onBack = {
                partyViewModel.clearError()
                screen = PartyScreen.List
            },
            isSubmitting = partyState.action == PartyAction.Creating,
            errorMessage = partyState.errorMessage,
            // fake 포인트 부족 테스트 시 PartyTestConfig.AVAILABLE_POINT를 5_000으로 변경
            // Real 생성에서는 서버가 보유 포인트를 최종 검증하므로 Fake 포인트로 요청을 막지 않는다.
            availablePoint = if (PartyApiConfig.USE_REAL_CREATE) {
                partyState.availablePoint
            } else {
                PartyTestConfig.AVAILABLE_POINT
            },
            showPointShortageFromServer = partyState.isCreatePointInsufficient,
            onPointShortageDismiss = partyViewModel::dismissCreatePointDialog,
            onChargePoint = onChargePoint,
            onCreate = { period, deposit ->
                // 필수 선택값이 모두 준비된 경우에만 ViewModel에 생성 명령 전달
                val challenge = selectedChallenge ?: return@PartyCreateScreen
                partyViewModel.createParty(
                    command = CreatePartyCommand(
                        // 화면 검증과 동일하게 정규화된 파티 이름을 생성 요청에 전달
                        name = Normalizer.normalize(partyName.trim(), Normalizer.Form.NFC),
                        challengeId = challenge.id.toString(),
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

        PartyScreen.ChallengeSelect -> GalleryRoute(
            onChallengeClick = { challenge ->
                pendingChallenge = challenge
                screen = PartyScreen.ChallengeDetail
            }
        )

        PartyScreen.ChallengeDetail -> {
            // 상세 진입 시 임시 선택을 우선 사용하고 없으면 기존 확정 선택 사용
            val challenge = pendingChallenge ?: selectedChallenge
            if (challenge == null) {
                LaunchedEffect(Unit) { screen = PartyScreen.ChallengeSelect }
            } else {
                // 챌린지 탭과 동일하게 상세 API로 실제 데이터를 조회해 표시
                DetailRoute(
                    challengeId = challenge.id,
                    onBackClick = { screen = PartyScreen.ChallengeSelect },
                    // 파티 생성 경로에서는 즉시 참여하지 않고 선택 결과를 생성 화면으로 전달
                    actionText = "파티 만들기",
                    onActionClick = { data ->
                        // 상세 CTA 선택 시에만 임시 챌린지를 최종 선택으로 확정(제목은 API 값으로 갱신)
                        selectedChallenge = challenge.copy(title = data.title)
                        pendingChallenge = null
                        screen = PartyScreen.Create
                    }
                )
            }
        }

        PartyScreen.WaitingRoom -> {
            // 생성·참여 직후 대기방 응답 대기 중 로딩 또는 재시도 화면 표시
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
                    // Real 준비 완료에서는 서버가 포인트를 검증하므로 Fake 포인트로 요청을 막지 않는다.
                    availablePoint = if (PartyApiConfig.USE_REAL_READY) {
                        partyState.availablePoint
                    } else {
                        PartyTestConfig.AVAILABLE_POINT
                    },
                    isReadySubmitted = partyState.isReadySubmitted,
                    isActionInProgress = partyState.action != PartyAction.Idle,
                    errorMessage = partyState.errorMessage,
                    showPointShortageFromServer = partyState.isReadyPointInsufficient,
                    onPointShortageDismiss = partyViewModel::dismissReadyPointDialog,
                    onChargePoint = onChargePoint,
                    onBack = {
                        // 뒤로가기도 파티 탈퇴 요청으로 처리하고 성공 시에만 목록으로 이동
                        partyViewModel.leaveParty { screen = PartyScreen.List }
                    },
                    onStartClick = {
                        // 시작 API 성공 후 명세에 따라 홈 화면으로 이동
                        partyViewModel.startParty {
                            onHomeNavigate()
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

        PartyScreen.Settlement -> {
            PartySettlementRoute(
                partyId = feedPartyId.toLong(),
                onBack = { screen = PartyScreen.List },
                viewModel = partySettlementViewModel
            )
        }
    }

    if (showInviteDialog) {
        // 초대코드 오류는 다이얼로그 닫기 또는 다시 입력 시 초기화
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

    LaunchedEffect(inviteViewModel.uiState.joinedWaitingRoom) {
        // 참여 POST 성공 응답의 대기방을 그대로 적용해 추가 GET 없이 이동한다.
        inviteViewModel.uiState.joinedWaitingRoom?.let { room ->
            showInviteDialog = false
            waitingPartyId = room.partyId
            partyViewModel.applyJoinedWaitingRoom(room)
            screen = PartyScreen.WaitingRoom
            inviteViewModel.reset()
        }
    }

    if (showCameraPermissionDialog) {
        PermissionSettingDialog(
            type = PermissionDialogType.CAMERA,
            onDismiss = { showCameraPermissionDialog = false },
            onMoveToSettings = { moveToAppSettings(context) }
        )
    }
}

@Composable
internal fun PartyLoadingScreen(
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
