package com.example.onuldo_fe.navigation

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onuldo_fe.camera.CameraScreen
import com.example.onuldo_fe.camera.CameraViewModel
import com.example.onuldo_fe.ui.screen.challenge.detail.DetailRoute
import com.example.onuldo_fe.ui.screen.challenge.participate.ParticipateRoute
import com.example.onuldo_fe.ui.screen.login.LandingScreen
import com.example.onuldo_fe.ui.screen.login.LoginScreen
import com.example.onuldo_fe.ui.screen.login.ProfileSetupScreen
import com.example.onuldo_fe.ui.screen.login.SignupScreen
import com.example.onuldo_fe.ui.screen.login.TermsAgreementScreen
import com.example.onuldo_fe.ui.screen.login.WelcomeScreen
import com.example.onuldo_fe.ui.screen.main.MainScreen
import com.example.onuldo_fe.ui.screen.mypage.NicknameEditScreen
import com.example.onuldo_fe.ui.screen.mypage.PointChargeScreen
import com.example.onuldo_fe.ui.screen.mypage.PointWalletScreen
import com.example.onuldo_fe.ui.screen.mypage.PointWithdrawScreen
import com.example.onuldo_fe.ui.screen.mypage.ProfileSettingsScreen
import com.example.onuldo_fe.ui.screen.mypage.SettingScreen
import com.example.onuldo_fe.ui.screen.mypage.TermScreen
import com.example.onuldo_fe.data.auth.dto.TermType
import com.example.onuldo_fe.data.network.SessionEvents
import com.example.onuldo_fe.viewmodel.OnboardingDraft
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.onuldo_fe.camera.PhotoPreviewScreen
import com.example.onuldo_fe.ui.screen.verification.ChallengeVerificationScreen
import com.example.onuldo_fe.ui.screen.verification.VerificationStatus
import kotlinx.coroutines.delay

private const val MINIMUM_REVIEWING_DURATION_MILLIS = 2_000L
private const val FINAL_REVIEW_STEP_DISPLAY_MILLIS = 300L

/** 마이 메뉴 이름. 서버가 약관 제목을 주기 전까지 상단바에 쓴다. */
private fun termTitleOf(termType: TermType): String = when (termType) {
    TermType.SERVICE -> "서비스 이용약관"
    TermType.PRIVACY -> "개인정보 처리방침"
    TermType.REFUND -> "환급 정책"
    TermType.AGE_14 -> "만 14세 이상"
    TermType.MARKETING -> "마케팅 수신 동의"
}

/** 앱 전체 내비게이션 그래프. 랜딩 → 로그인/회원가입 → 프로필 설정 → 환영 → 메인(탭).
 *  (스플래시는 별도 화면이 아니라 시스템 스플래시로 처리 — [MainActivity])
 *
 *  [startDestination]은 자동 로그인 판정 결과다. 저장된 세션이 살아 있으면 [Routes.MAIN],
 *  아니면 [Routes.LANDING]. 판정은 [MainActivity]가 스플래시를 붙잡은 채로 끝낸다. */
@Composable
fun OnuldoApp(startDestination: String = Routes.LANDING) {
    val navController = rememberNavController()

    //카메라 -> previewScreen
    val cameraViewModel: CameraViewModel = viewModel()

    // 리프레시 토큰까지 만료돼 자동 재발급이 실패하면 랜딩으로 되돌린다.
    LaunchedEffect(Unit) {
        SessionEvents.sessionExpired.collect {
            SessionEvents.consume()
            navController.navigate(Routes.LANDING) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LANDING) {
            // 랜딩 도착 = 온보딩을 시작 전이거나 중도 이탈했다는 뜻.
            // 뒤로가기 이탈·로그아웃·세션 만료가 모두 이곳으로 모이므로, 메모리에 남은
            // 이메일·비밀번호를 여기서 지운다. (OnboardingDraft는 프로세스 전역 object다.)
            LaunchedEffect(Unit) { OnboardingDraft.clear() }

            LandingScreen(
                onLoginClick = { navController.navigate(Routes.LOGIN) },
                onSignupClick = { navController.navigate(Routes.SIGNUP) },
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        // 인증 완료 후에는 랜딩/로그인으로 되돌아가지 않도록 백스택 제거
                        popUpTo(Routes.LANDING) { inclusive = true }
                    }
                },
                onSignupClick = { navController.navigate(Routes.SIGNUP) },
                // 소셜 로그인 결과 신규 회원 → 약관 동의 → 프로필 설정 → oauth/signup
                onSocialSignupNeeded = { navController.navigate(Routes.TERMS_AGREEMENT) },
            )
        }
        // 약관 동의. 이메일 가입과 소셜 신규 가입이 공통으로 거친다.
        composable(Routes.TERMS_AGREEMENT) {
            TermsAgreementScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Routes.PROFILE_SETUP) },
                onTermClick = { termType -> navController.navigate(Routes.mypageTerm(termType.name)) },
            )
        }
        composable(Routes.SIGNUP) {
            SignupScreen(
                onBack = { navController.popBackStack() },
                // 회원가입 입력 → 약관 동의 → 프로필 설정 순으로 진행한다.
                onNext = { navController.navigate(Routes.TERMS_AGREEMENT) },
            )
        }
        composable(Routes.PROFILE_SETUP) {
            ProfileSetupScreen(
                onBack = { navController.popBackStack() },
                // 프로필 완료 → 가입 완료(환영) 화면.
                onDone = { navController.navigate(Routes.WELCOME) },
            )
        }
        composable(Routes.WELCOME) {
            WelcomeScreen(
                // "오늘두 시작하기" → 메인 진입(온보딩 백스택 전체 제거).
                onStart = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LANDING) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.MAIN) {
            // 메인 도착 = 온보딩 종료. 가입 경로는 ProfileSetupViewModel이 이미 비웠지만,
            // 회원가입을 입력하다 로그인으로 되돌아가 로그인한 경우가 남는다.
            LaunchedEffect(Unit) { OnboardingDraft.clear() }

            MainScreen(
                onNavigate = { route -> navController.navigate(route) },
                // 로그아웃 시 온보딩 백스택을 모두 비우고 랜딩으로 되돌린다.
                onLoggedOut = {
                    navController.navigate(Routes.LANDING) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        // --- 마이페이지 하위 화면 (메인 탭 위 풀스크린) ---
        composable(Routes.MYPAGE_PROFILE) {
            ProfileSettingsScreen(
                onBack = { navController.popBackStack() },
                onNicknameClick = { nickname ->
                    navController.navigate(Routes.mypageNickname(nickname))
                },
            )
        }
        composable(
            route = Routes.MYPAGE_NICKNAME,
            arguments = listOf(
                navArgument(Routes.MYPAGE_NICKNAME_ARG) {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) { backStackEntry ->
            NicknameEditScreen(
                currentNickname = backStackEntry.arguments
                    ?.getString(Routes.MYPAGE_NICKNAME_ARG)
                    .orEmpty(),
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.MYPAGE_WALLET) {
            PointWalletScreen(
                onBack = { navController.popBackStack() },
                onChargeClick = { navController.navigate(Routes.MYPAGE_CHARGE) },
                onWithdrawClick = { navController.navigate(Routes.MYPAGE_WITHDRAW) },
            )
        }
        composable(Routes.MYPAGE_CHARGE) {
            PointChargeScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.MYPAGE_WITHDRAW) {
            PointWithdrawScreen(onBack = { navController.popBackStack() })
        }
        // 알림 설정(시온)  //화면이 상태 자체 보유 → 등록만. 뒤로가기 → 마이로 복귀
        composable(Routes.MYPAGE_NOTIFICATION) {
            SettingScreen(onBackClick = { navController.popBackStack() })
        }
        // 약관 상세. termType(SERVICE/PRIVACY/REFUND)에 따라 본문을 서버에서 받아 표시한다.
        composable(
            route = Routes.MYPAGE_TERM,
            arguments = listOf(navArgument("termType") { type = NavType.StringType }),
        ) { backStackEntry ->
            val raw = backStackEntry.arguments?.getString("termType").orEmpty()
            val termType = TermType.entries.firstOrNull { it.name == raw } ?: TermType.SERVICE
            TermScreen(
                termType = termType,
                fallbackTitle = termTitleOf(termType),
                onBack = { navController.popBackStack() },
            )
        }

        // 카메라 촬영
        composable(
            route = Routes.CAMERA,
            arguments = listOf(
                navArgument(Routes.CAMERA_CHALLENGE_ID_ARG) { type = NavType.LongType },
                navArgument(Routes.CAMERA_CATEGORY_ARG) {
                    type = NavType.StringType
                    defaultValue = "시간 챌린지"
                },
                navArgument(Routes.CAMERA_TITLE_ARG) {
                    type = NavType.StringType
                    defaultValue = "오늘의 챌린지 인증"
                },
                navArgument(Routes.CAMERA_DEADLINE_ARG) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val challengeId = backStackEntry.arguments
                ?.getLong(Routes.CAMERA_CHALLENGE_ID_ARG)
                ?: return@composable
            val category = backStackEntry.arguments
                ?.getString(Routes.CAMERA_CATEGORY_ARG)
                .orEmpty()
            val title = backStackEntry.arguments
                ?.getString(Routes.CAMERA_TITLE_ARG)
                .orEmpty()
            val deadline = backStackEntry.arguments
                ?.getString(Routes.CAMERA_DEADLINE_ARG)
                .orEmpty()
            CameraScreen(
                category = category,
                title = title,
                onPhotoCaptured = { uri ->
                    cameraViewModel.setImageUri(uri)
                    navController.navigate(
                        Routes.photoPreview(challengeId, category, title, deadline)
                    )
                },
                onCloseClick = {
                    cameraViewModel.discardPhoto()
                    navController.popBackStack()
                }
            )
        }

        // 촬영 사진 확인 및 제출
        composable(
            route = Routes.PHOTO_PREVIEW,
            arguments = listOf(
                navArgument(Routes.CAMERA_CHALLENGE_ID_ARG) { type = NavType.LongType },
                navArgument(Routes.CAMERA_CATEGORY_ARG) {
                    type = NavType.StringType
                    defaultValue = "시간 챌린지"
                },
                navArgument(Routes.CAMERA_TITLE_ARG) {
                    type = NavType.StringType
                    defaultValue = "오늘의 챌린지 인증"
                },
                navArgument(Routes.CAMERA_DEADLINE_ARG) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val challengeId = backStackEntry.arguments
                ?.getLong(Routes.CAMERA_CHALLENGE_ID_ARG)
                ?: return@composable
            val category = backStackEntry.arguments
                ?.getString(Routes.CAMERA_CATEGORY_ARG)
                .orEmpty()
            val title = backStackEntry.arguments
                ?.getString(Routes.CAMERA_TITLE_ARG)
                .orEmpty()
            val deadline = backStackEntry.arguments
                ?.getString(Routes.CAMERA_DEADLINE_ARG)
                .orEmpty()
            val imageUri by cameraViewModel.imageUri.collectAsState()
            val submitState by cameraViewModel.submitState.collectAsState()
            var hasStartedReviewNavigation by remember { mutableStateOf(false) }

            LaunchedEffect(submitState) {
                val shouldShowReviewing = when (submitState) {
                    com.example.onuldo_fe.camera.VerificationSubmitState.Reviewing,
                    is com.example.onuldo_fe.camera.VerificationSubmitState.Success,
                    is com.example.onuldo_fe.camera.VerificationSubmitState.Failure,
                    is com.example.onuldo_fe.camera.VerificationSubmitState.Waiting -> true
                    else -> false
                }

                if (shouldShowReviewing && !hasStartedReviewNavigation) {
                    hasStartedReviewNavigation = true
                    navController.navigate(Routes.VERIFICATION_REVIEWING) {
                        launchSingleTop = true
                    }
                } else if (submitState == com.example.onuldo_fe.camera.VerificationSubmitState.Idle ||
                    submitState is com.example.onuldo_fe.camera.VerificationSubmitState.Error
                ) {
                    hasStartedReviewNavigation = false
                }
            }

            PhotoPreviewScreen(
                category = category,
                title = title,
                imageUri = imageUri,
                onCloseClick = {
                    cameraViewModel.discardPhoto()
                    navController.popBackStack(
                        route = Routes.CAMERA,
                        inclusive = true
                    )
                },
                onRetakeClick = {
                    cameraViewModel.discardPhoto()
                    navController.popBackStack()
                },
                onSubmitClick = {
                    cameraViewModel.submitVerification(challengeId, category, title, deadline)
                },
                submitState = submitState,
                onErrorConfirm = cameraViewModel::clearSubmitState
            )
        }

        composable(Routes.VERIFICATION_REVIEWING) {
            val submitState by cameraViewModel.submitState.collectAsState()
            val reviewingStartedAt = remember { SystemClock.elapsedRealtime() }
            val isReviewResultReady =
                submitState is com.example.onuldo_fe.camera.VerificationSubmitState.Success ||
                    submitState is com.example.onuldo_fe.camera.VerificationSubmitState.Failure ||
                    submitState is com.example.onuldo_fe.camera.VerificationSubmitState.Waiting

            LaunchedEffect(submitState) {
                if (isReviewResultReady) {
                    val elapsedTime = SystemClock.elapsedRealtime() - reviewingStartedAt
                    val remainingMinimumDuration =
                        (MINIMUM_REVIEWING_DURATION_MILLIS - elapsedTime).coerceAtLeast(0L)
                    delay(maxOf(remainingMinimumDuration, FINAL_REVIEW_STEP_DISPLAY_MILLIS))
                }

                when (submitState) {
                    is com.example.onuldo_fe.camera.VerificationSubmitState.Success ->
                        navController.navigate(Routes.VERIFICATION_SUCCESS) {
                            popUpTo(Routes.VERIFICATION_REVIEWING) { inclusive = true }
                        }
                    is com.example.onuldo_fe.camera.VerificationSubmitState.Failure ->
                        navController.navigate(Routes.VERIFICATION_FAIL) {
                            popUpTo(Routes.VERIFICATION_REVIEWING) { inclusive = true }
                        }
                    is com.example.onuldo_fe.camera.VerificationSubmitState.Waiting ->
                        navController.navigate(Routes.VERIFICATION_WAITING) {
                            popUpTo(Routes.VERIFICATION_REVIEWING) { inclusive = true }
                        }
                    is com.example.onuldo_fe.camera.VerificationSubmitState.Error ->
                        navController.popBackStack()
                    else -> Unit
                }
            }

            ChallengeVerificationScreen(
                status = VerificationStatus.REVIEWING,
                isReviewResultReady = isReviewResultReady
            )
        }

        composable(Routes.VERIFICATION_SUCCESS) {
            ChallengeVerificationScreen(
                status = VerificationStatus.SUCCESS,
                onConfirmClick = {
                    cameraViewModel.clearSubmitState()
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.VERIFICATION_FAIL) {
            val state by cameraViewModel.submitState.collectAsState()
            ChallengeVerificationScreen(
                status = VerificationStatus.FAILURE,
                failureReason = (state as? com.example.onuldo_fe.camera.VerificationSubmitState.Failure)?.message.orEmpty(),
                verificationDeadline = cameraViewModel.activeDeadline,
                onManualReviewClick = {
                    navController.navigate(Routes.VERIFICATION_WAITING)
                },
                onRetryClick = {
                    cameraViewModel.activeChallengeId?.let { challengeId ->
                        val category = cameraViewModel.activeCategory
                            .ifBlank { "시간 챌린지" }
                        val title = cameraViewModel.activeTitle
                            .ifBlank { "오늘의 챌린지 인증" }
                        cameraViewModel.clearSubmitState()
                        navController.navigate(
                            Routes.camera(challengeId, category, title, cameraViewModel.activeDeadline)
                        ) {
                            popUpTo(Routes.CAMERA) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.VERIFICATION_WAITING) {
            val state by cameraViewModel.submitState.collectAsState()
            val submittedAt =
                (state as? com.example.onuldo_fe.camera.VerificationSubmitState.Waiting)
                    ?.result
                    ?.verifiedAt
                    ?: cameraViewModel.activeVerifiedAt
            ChallengeVerificationScreen(
                status = VerificationStatus.WAITING,
                submittedAt = submittedAt,
                onConfirmClick = {
                    cameraViewModel.clearSubmitState()
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }

        // --- 챌린지 상세 흐름: 상세 → 참여 ---
        // challengeId 기반으로 상세 API를 조회한 뒤, CTA 동작은 호출부에서 처리한다.
        // 챌린지 탭(참여 화면 이동)/파티 생성 흐름(선택 확정)에서 공통 재사용한다.
        composable(
            route = Routes.CHALLENGE_DETAIL,
            arguments = listOf(navArgument(Routes.CHALLENGE_DETAIL_ARG) { type = NavType.LongType })
        ) { backStackEntry ->
            val challengeId = backStackEntry.arguments?.getLong(Routes.CHALLENGE_DETAIL_ARG) ?: 0L
            DetailRoute(
                challengeId = challengeId,
                onBackClick = { navController.popBackStack() },
                onActionClick = { data ->
                    navController.navigate(
                        Routes.challengeParticipate(
                            data.challengeId,
                            data.title,
                            data.description,
                            data.category,
                            data.timeStart,
                            data.timeEnd
                        )
                    )
                },
            )
        }
        composable(
            route = Routes.CHALLENGE_PARTICIPATE,
            arguments = listOf(
                navArgument(Routes.CHALLENGE_PARTICIPATE_ARG) { type = NavType.LongType },
                navArgument(Routes.CHALLENGE_PARTICIPATE_ARG_TITLE) { type = NavType.StringType; defaultValue = "" },
                navArgument(Routes.CHALLENGE_PARTICIPATE_ARG_DESC) { type = NavType.StringType; defaultValue = "" },
                navArgument(Routes.CHALLENGE_PARTICIPATE_ARG_CATEGORY) { type = NavType.StringType; defaultValue = "" },
                navArgument(Routes.CHALLENGE_PARTICIPATE_ARG_TIME_START) { type = NavType.StringType; defaultValue = "" },
                navArgument(Routes.CHALLENGE_PARTICIPATE_ARG_TIME_END) { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val challengeId = backStackEntry.arguments?.getLong(Routes.CHALLENGE_PARTICIPATE_ARG) ?: 0L
            val args = backStackEntry.arguments
            ParticipateRoute(
                challengeId = challengeId,
                title = args?.getString(Routes.CHALLENGE_PARTICIPATE_ARG_TITLE).orEmpty(),
                description = args?.getString(Routes.CHALLENGE_PARTICIPATE_ARG_DESC).orEmpty(),
                category = args?.getString(Routes.CHALLENGE_PARTICIPATE_ARG_CATEGORY).orEmpty(),
                timeStart = args?.getString(Routes.CHALLENGE_PARTICIPATE_ARG_TIME_START).orEmpty(),
                timeEnd = args?.getString(Routes.CHALLENGE_PARTICIPATE_ARG_TIME_END).orEmpty(),
                onBackClick = { navController.popBackStack() },
                // 참여 성공 완료 화면(StartDone)의 "홈으로 가기" → 상세 흐름 백스택 정리하고 메인(홈)으로
                onHome = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                },
                // 잔액 부족 팝업의 "충전하기" → 포인트 충전 화면
                onChargePoint = { navController.navigate(Routes.MYPAGE_CHARGE) },
            )
        }
    }
}
