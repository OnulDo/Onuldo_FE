package com.example.onuldo_fe.ui.screen.party

import android.Manifest
import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.onuldo_fe.model.challenge.ChallengeCategory
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
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
import com.example.onuldo_fe.viewmodel.party.PartyCardUi
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

// 파티 생성 화면에서 확정 선택한 챌린지를 구성 변경·화면 이탈(포인트 충전 등) 후에도
// 복원할 수 있도록 저장/복원하는 Saver. Challenge가 Parcelable이 아니라 직접 정의했다.
private val ChallengeSaver: Saver<Challenge?, Any> = listSaver(
    save = { challenge ->
        if (challenge == null) {
            emptyList()
        } else {
            // listSaver는 원소가 null이 아니어야 해서, imageUrl이 없으면 빈 문자열로
            // 대신 저장하고 복원할 때 다시 null로 되돌린다.
            listOf(
                challenge.id,
                challenge.title,
                challenge.participantCount,
                challenge.category.name,
                challenge.imageUrl.orEmpty(),
                challenge.imageRes
            )
        }
    },
    restore = { saved ->
        if (saved.isEmpty()) {
            null
        } else {
            Challenge(
                id = saved[0] as Long,
                title = saved[1] as String,
                participantCount = saved[2] as Int,
                category = ChallengeCategory.valueOf(saved[3] as String),
                imageUrl = (saved[4] as String).ifEmpty { null },
                imageRes = saved[5] as Int
            )
        }
    }
)

// 파티 목록부터 생성·참여·대기방·피드까지 화면 전환과 ViewModel 상태 연결
@Composable
fun PartyRoute(
    partyViewModel: PartyViewModel = viewModel(),
    inviteViewModel: PartyInviteViewModel = viewModel(),
    partyFeedViewModel: PartyFeedViewModel = viewModel(),
    partySettlementViewModel: PartySettlementViewModel = viewModel(),
    onBottomBarVisibilityChange: (Boolean) -> Unit = {},
    onCameraNavigate: (Long, String, String) -> Unit = { _, _, _ -> },
    onChargePoint: () -> Unit = {},
    onHomeNavigate: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 현재 화면과 다이얼로그 노출 여부는 Route에서만 관리
    // PartyScreen은 Kotlin enum이라 기본적으로 Serializable이라 rememberSaveable로 바로 저장된다.
    // 포인트 충전 화면 왕복처럼 이 컴포저블이 사라졌다 다시 생기는 경우, 대부분은 저장된 값
    // 그대로(Create 등) 복원된다. 다만 진짜 프로세스가 죽어 저장된 값 자체가 없을 때는,
    // ViewModel에 이미 남아있는 waitingRoom을 기준으로 대기방 화면을 복원한다(ViewModel은
    // 구성 변경에도 유지되므로 leaveParty를 안 보냈다면 waitingRoom이 그대로 남아있다).
    var screen by rememberSaveable {
        mutableStateOf(
            if (partyViewModel.uiState.waitingRoom != null) PartyScreen.WaitingRoom else PartyScreen.List
        )
    }
    var showInviteDialog by remember { mutableStateOf(false) }
    var showCameraPermissionDialog by rememberSaveable {
        mutableStateOf(false)
    }
    // pendingChallenge는 상세 확인 중인 선택, selectedChallenge는 생성 화면에서 확정된 선택이다.
    // selectedChallenge는 포인트 부족 → 충전 화면 왕복처럼 화면이 사라졌다 다시 생겨도
    // 유지되어야 해서 rememberSaveable(+커스텀 Saver)을 쓴다. pendingChallenge는 탐색 중인
    // 실제 API 챌린지 선택 상태이며, 화면이 사라지면 다시 선택할 수 있어 remember로 충분하다.
    var selectedChallenge by rememberSaveable(stateSaver = ChallengeSaver) { mutableStateOf<Challenge?>(null) }
    var pendingChallenge by remember { mutableStateOf<Challenge?>(null) }

    // 권한 설정 화면에서 Activity가 재생성돼도 인증 대상을 복원할 수 있도록
    // PartyCardUi 전체 대신 카메라 이동에 필요한 값만 rememberSaveable로 저장한다.
    var pendingVerifyChallengeId by rememberSaveable { mutableStateOf<Long?>(null) }
    var pendingVerifyTitle by rememberSaveable { mutableStateOf("") }
    var pendingVerifyDeadline by rememberSaveable { mutableStateOf("") }

    // 생성 화면을 벗어나 챌린지를 탐색해도, 포인트 충전 화면을 다녀와도 입력값을 유지하도록
    // Route가 생성 폼 상태를 rememberSaveable로 보관한다.
    var partyName by rememberSaveable { mutableStateOf("") }
    var capacity by rememberSaveable { mutableIntStateOf(5) }

    // 피드 재조회와 대기방 오류 재시도에 사용할 마지막 partyId 보관
    var feedPartyId by remember { mutableStateOf("1") }
    // screen과 마찬가지로 rememberSaveable로 저장한다. 그렇지 않으면 screen만 WaitingRoom으로
    // 복원되고(특히 진짜 프로세스가 죽어 ViewModel의 waitingRoom도 함께 사라진 경우)
    // waitingPartyId는 null로 초기화되어, 대기방 조회 재시도·폴링을 시작할 ID 자체가 없어진다.
    var waitingPartyId by rememberSaveable {
        mutableStateOf(partyViewModel.uiState.waitingRoom?.partyId)
    }

    val partyState = partyViewModel.uiState
    val waitingRoom = partyState.waitingRoom

    fun clearPendingVerification() {
        pendingVerifyChallengeId = null
        pendingVerifyTitle = ""
        pendingVerifyDeadline = ""
    }

    fun handleVerifyClick(party: PartyCardUi) {
        if (party.challengeId <= 0L) return
        pendingVerifyChallengeId = party.challengeId
        pendingVerifyTitle = party.challengeName
        pendingVerifyDeadline = party.deadline
        val isCameraPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (isCameraPermissionGranted) {
            onCameraNavigate(party.challengeId, party.challengeName, party.deadline)
            clearPendingVerification()
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
                    pendingVerifyChallengeId?.let { challengeId ->
                        onCameraNavigate(challengeId, pendingVerifyTitle, pendingVerifyDeadline)
                    }
                    clearPendingVerification()
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DisposableEffect(lifecycleOwner, screen) {
        val isPartyListVisible = screen == PartyScreen.List
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START && isPartyListVisible) {
                partyViewModel.onPartyListVisible()
            }
        }

        // addObserver 시점에 이미 STARTED 이상이면 ON_START가 동기적으로 한 번 재생되므로,
        // 탭 재진입·파티 내부 화면에서 목록으로 복귀하는 경우 모두 observer 한 경로로만 처리한다
        // (직접 호출을 남겨두면 addObserver의 동기 재생과 중복 호출된다).
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DisposableEffect(lifecycleOwner, screen) {
        val shouldRefreshPoint = screen == PartyScreen.Create || screen == PartyScreen.WaitingRoom
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && shouldRefreshPoint) {
                partyViewModel.refreshAvailablePoint()
            }
        }

        // addObserver 시점에 이미 RESUMED 상태면 LifecycleRegistry가 ON_RESUME을
        // 동기적으로 한 번 재생해준다. 그래서 생성·대기방 진입 시점과 포인트 충전 화면에서
        // 돌아온 시점 모두 observer의 ON_RESUME 처리 한 경로로만 갱신하고, 직접 호출은
        // 따로 남기지 않는다(남기면 addObserver의 동기 재생과 중복 요청이 발생함).
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DisposableEffect(lifecycleOwner, screen, waitingPartyId, waitingRoom != null) {
        val partyId = waitingPartyId
        // shouldLeave: 대기방 화면에 partyId만 있으면 성립한다. waitingRoom 조회가 아직
        // 끝나지 않았거나 에러로 로딩 화면이 떠 있는 동안에도 서버에는 이미 파티가 생성·참여된
        // 상태이므로, 이 구간에서 백그라운드로 나가도 이탈 요청은 보내야 한다.
        val shouldLeave = screen == PartyScreen.WaitingRoom && partyId != null
        // shouldPoll: 실제로 대기방 데이터를 받아온 뒤에만 폴링을 시작한다.
        val shouldPoll = shouldLeave && waitingRoom != null
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> if (shouldPoll) {
                    partyId?.let(partyViewModel::startWaitingRoomPolling)
                }
                Lifecycle.Event.ON_STOP -> {
                    partyViewModel.stopWaitingRoomPolling()
                    // onDestroy는 프로세스 강제 종료 시 호출이 보장되지 않으므로, 대기방이 백그라운드로
                    // 내려가는 시점(ON_STOP)에 이탈 요청을 대신 보낸다. 뒤로가기 확인 모달을 거치지 않고도
                    // 앱을 벗어나면 대기방에서 자동으로 나가지는 것이 의도된 동작이다.
                    // 단, 화면 회전 등 구성 변경으로 인한 재생성에서도 ON_STOP이 발생하므로
                    // isChangingConfigurations일 때는 자동 이탈을 건너뛴다.
                    val activity = context.findActivity()
                    val isChangingConfigurations = activity?.isChangingConfigurations == true
                    // 포인트 충전 화면처럼 같은 액티비티 안의 다른 화면으로 이동해도, 이 화면
                    // (NavBackStackEntry)의 lifecycleOwner에는 똑같이 ON_STOP이 전달된다.
                    // 액티비티 자체는 아직 포그라운드(STARTED 이상)인 경우까지 백그라운드
                    // 전환으로 취급해 자동 이탈시키면, 충전하고 돌아왔을 때 이미 파티에서
                    // 나가진 상태가 되어버린다. 그래서 액티비티 자체가 STARTED 밑으로 내려간
                    // 경우(진짜 백그라운드·강제종료 등)에만 자동 이탈한다.
                    val isActivityStillForeground =
                        (activity as? LifecycleOwner)?.lifecycle?.currentState
                            ?.isAtLeast(Lifecycle.State.STARTED) == true
                    if (shouldLeave && !isChangingConfigurations && !isActivityStillForeground) {
                        // 화면 전환은 API 결과와 무관하게 이 시점에 바로 처리한다(그렇지 않으면
                        // 다른 요청과 겹쳐 leaveParty가 지연·실패할 때 화면이 WaitingRoom에 고정되고
                        // 하단 탭바도 계속 숨겨진 채로 남아 하단 네비게이션 자체를 못 쓰게 된다).
                        // 실제 서버 이탈은 autoLeaveOnBackground()가 맡는다 — 실패해도 의도를 남겨두고
                        // 목록이 다시 보이는 시점(onPartyListVisible)에 스스로 재시도한다.
                        screen = PartyScreen.List
                        partyViewModel.autoLeaveOnBackground()
                    }
                }
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

    LaunchedEffect(screen, waitingRoom != null, waitingPartyId, partyState.action, partyState.errorMessage) {
        // 프로세스가 죽었다 복구된 경우처럼, WaitingRoom 화면인데 대기방 데이터도 없고
        // 진행 중인 요청·이전 오류도 없는 상태로 남아있으면 재시도 버튼을 누르지 않아도
        // 자동으로 한 번 조회한다. (실패하면 errorMessage가 채워지므로 무한 재시도는 안 됨 —
        // 이후엔 PartyLoadingScreen의 수동 재시도로만 다시 시도된다.)
        val partyId = waitingPartyId
        if (screen == PartyScreen.WaitingRoom &&
            waitingRoom == null &&
            partyId != null &&
            partyState.action == PartyAction.Idle &&
            partyState.errorMessage == null
        ) {
            partyViewModel.loadWaitingRoom(partyId)
        }
    }

    LaunchedEffect(screen, waitingRoom?.status) {
        if (screen == PartyScreen.WaitingRoom && waitingRoom?.status == PartyStatus.InProgress) {
            // 다른 사용자(방장)가 파티를 시작하면 폴링을 멈추고 대기방 캐시를 비운 뒤 홈으로 이동한다.
            // startParty()의 방장 경로와 동일하게 비워두지 않으면, 파티 탭으로 돌아왔을 때
            // 이미 시작된 파티의 낡은 대기방 화면이 되살아난다.
            // screen도 List로 같이 되돌려야 한다 — screen이 rememberSaveable이라 저장된
            // 마지막 값이 그대로 복원되므로, 여기서 안 돌려두면 다음에 파티 탭에 돌아왔을 때
            // WaitingRoom이 그대로 복원되어버린다.
            partyViewModel.clearWaitingRoomAfterStart()
            screen = PartyScreen.List
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
            isRefreshing = partyState.isRefreshing,
            onRefresh = partyViewModel::refreshParties,
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
                // 기존 확정 선택은 유지하고 새로 상세를 확인하던 선택만 초기화
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
            checkPointBeforeRequest = !PartyApiConfig.USE_REAL_CREATE,
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
            // 상세 진입 시 현재 확인 중인 선택을 우선 사용하고, 없으면 기존 확정 선택 사용
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
                        // 상세 CTA 선택 시에만 임시 챌린지를 최종 선택으로 확정하고, 생성 요청에는 상세 API의 id/title을 사용한다.
                        selectedChallenge = challenge.copy(
                            id = data.challengeId,
                            title = data.title
                        )
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
                    checkPointBeforeRequest = !PartyApiConfig.USE_REAL_READY,
                    onBack = {
                        // 뒤로가기도 파티 탈퇴 요청으로 처리하고 성공 시에만 목록으로 이동
                        partyViewModel.leaveParty { screen = PartyScreen.List }
                    },
                    onStartClick = {
                        // 시작 API 성공 후 명세에 따라 홈 화면으로 이동
                        partyViewModel.startParty {
                            // screen도 List로 같이 되돌려야 한다 — screen이 rememberSaveable이라
                            // 저장된 마지막 값이 그대로 복원되므로, 여기서 안 돌려두면 다음에
                            // 파티 탭에 돌아왔을 때 WaitingRoom이 그대로 복원되어버린다.
                            screen = PartyScreen.List
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
            onDismiss = {
                showCameraPermissionDialog = false
                clearPendingVerification()
            },
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

/** Compose Context가 감싸고 있는 실제 Activity를 찾는다(화면 회전 등 구성 변경 판별용). */
private tailrec fun android.content.Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
