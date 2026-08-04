package com.example.onuldo_fe.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.onuldo_fe.camera.CameraScreen
import com.example.onuldo_fe.camera.CameraViewModel
import com.example.onuldo_fe.ui.screen.challenge.detail.DetailRoute
import com.example.onuldo_fe.ui.screen.challenge.participate.ParticipateRoute
import com.example.onuldo_fe.ui.screen.challenge.participate.StartDoneScreen
import com.example.onuldo_fe.ui.screen.login.LandingScreen
import com.example.onuldo_fe.ui.screen.login.LoginScreen
import com.example.onuldo_fe.ui.screen.login.ProfileSetupScreen
import com.example.onuldo_fe.ui.screen.login.SignupScreen
import com.example.onuldo_fe.ui.screen.login.WelcomeScreen
import com.example.onuldo_fe.ui.screen.main.MainScreen
import com.example.onuldo_fe.ui.screen.mypage.NicknameEditScreen
import com.example.onuldo_fe.ui.screen.mypage.PasswordChangeScreen
import com.example.onuldo_fe.ui.screen.mypage.PointChargeScreen
import com.example.onuldo_fe.ui.screen.mypage.PointWalletScreen
import com.example.onuldo_fe.ui.screen.mypage.PointWithdrawScreen
import com.example.onuldo_fe.ui.screen.mypage.ProfileSettingsScreen
import com.example.onuldo_fe.ui.screen.mypage.WithdrawAccountScreen
import com.example.onuldo_fe.ui.screen.mypage.SettingScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.onuldo_fe.camera.PhotoPreviewScreen
import com.example.onuldo_fe.ui.screen.verification.ChallengeVerificationScreen
import com.example.onuldo_fe.ui.screen.verification.VerificationStatus

/** 앱 전체 내비게이션 그래프. 랜딩 → 로그인/회원가입 → 프로필 설정 → 환영 → 메인(탭).
 *  (스플래시는 별도 화면이 아니라 시스템 스플래시로 처리 — [MainActivity]) */
@Composable
fun OnuldoApp() {
    val navController = rememberNavController()
    // 앱 진입점은 랜딩. 특정 화면만 확인하고 싶을 땐 이 값을 잠시 바꿔 쓰되,
    // 커밋에는 반드시 LANDING 상태로 되돌린다.
    val debugStartDestination = Routes.LANDING

    //카메라 -> previewScreen
    val cameraViewModel: CameraViewModel = viewModel()

    NavHost(navController = navController, startDestination = debugStartDestination) {
        composable(Routes.LANDING) {
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
            )
        }
        composable(Routes.SIGNUP) {
            SignupScreen(
                onBack = { navController.popBackStack() },
                // 회원가입 완료 → 프로필 설정으로 바로 진행(이메일 인증·권한 화면 제거됨).
                onNext = { navController.navigate(Routes.PROFILE_SETUP) },
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
            MainScreen(onNavigate = { route -> navController.navigate(route) })
        }

        // --- 마이페이지 하위 화면 (메인 탭 위 풀스크린) ---
        composable(Routes.MYPAGE_PROFILE) {
            ProfileSettingsScreen(
                onBack = { navController.popBackStack() },
                onNicknameClick = { navController.navigate(Routes.MYPAGE_NICKNAME) },
                onPasswordClick = { navController.navigate(Routes.MYPAGE_PASSWORD) },
            )
        }
        composable(Routes.MYPAGE_NICKNAME) {
            NicknameEditScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.MYPAGE_PASSWORD) {
            PasswordChangeScreen(onBack = { navController.popBackStack() })
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
        composable(Routes.MYPAGE_ACCOUNT) {
            WithdrawAccountScreen(onBack = { navController.popBackStack() })
        }
        // 알림 설정(시온)  //화면이 상태 자체 보유 → 등록만. 뒤로가기 → 마이로 복귀
        composable(Routes.MYPAGE_NOTIFICATION) {
            SettingScreen(onBackClick = { navController.popBackStack() })
        }

        // 카메라 화면
        composable(Routes.CAMERA) {
            CameraScreen(
                category = "",
                title = "",
                onPhotoCaptured = { uri ->
                    cameraViewModel.setImageUri(uri)
                    navController.navigate(Routes.PHOTO_PREVIEW)
                },
                onCloseClick = {
                    navController.popBackStack()
                }
            )
        }

        //프리뷰
        composable(Routes.PHOTO_PREVIEW) {

            val imageUri by cameraViewModel.imageUri.collectAsState()

            PhotoPreviewScreen(
                category = "",
                title = "",
                imageUri = imageUri,

                onCloseClick = {
                    navController.popBackStack()
                },

                onRetakeClick = {
                    navController.popBackStack()
                },

                onSubmitClick = {
                    navController.navigate(Routes.VERIFICATION_WAITING)
                }
            )
        }

        //검증 심사중
        composable(Routes.VERIFICATION_REVIEWING) {
            ChallengeVerificationScreen(
                status = VerificationStatus.REVIEWING
            )
        }

        composable(Routes.VERIFICATION_SUCCESS) {
            ChallengeVerificationScreen(
                status = VerificationStatus.SUCCESS
            )
        }

        composable(Routes.VERIFICATION_FAIL) {
            ChallengeVerificationScreen(
                status = VerificationStatus.FAILURE
            )
        }

        composable(Routes.VERIFICATION_WAITING) {
            ChallengeVerificationScreen(
                status = VerificationStatus.WAITING,
                onConfirmClick = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }



        // --- 챌린지 상세 흐름 (챌린지 탭 위 풀스크린): 상세 → 참여 → 시작 완료 ---
        // 상세는 challengeId를 받아 API로 조회(DetailRoute). 참여/완료는 콜백으로 연동.
        composable(
            route = Routes.CHALLENGE_DETAIL,
            arguments = listOf(navArgument(Routes.CHALLENGE_DETAIL_ARG) { type = NavType.LongType })
        ) { backStackEntry ->
            val challengeId = backStackEntry.arguments?.getLong(Routes.CHALLENGE_DETAIL_ARG) ?: 0L
            DetailRoute(
                challengeId = challengeId,
                onBackClick = { navController.popBackStack() },
                onJoinClick = { title, description, category, timeStart, timeEnd ->
                    navController.navigate(
                        Routes.challengeParticipate(
                            challengeId, title, description, category, timeStart, timeEnd
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
                onSuccess = { navController.navigate(Routes.CHALLENGE_START_DONE) },
                // 잔액 부족 팝업의 "충전하기" → 포인트 충전 화면
                onChargePoint = { navController.navigate(Routes.MYPAGE_CHARGE) },
            )
        }
        composable(Routes.CHALLENGE_START_DONE) {
            StartDoneScreen(
                // "홈으로 가기" → 상세 흐름 백스택 정리하고 메인(홈)으로
                onHomeClick = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                },
            )
        }
    }
}
