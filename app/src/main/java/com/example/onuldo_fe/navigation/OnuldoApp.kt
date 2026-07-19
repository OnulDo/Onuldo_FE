package com.example.onuldo_fe.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.onuldo_fe.ui.screen.login.EmailVerifyScreen
import com.example.onuldo_fe.ui.screen.login.LandingScreen
import com.example.onuldo_fe.ui.screen.login.LoginScreen
import com.example.onuldo_fe.ui.screen.login.PermissionScreen
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

/** 앱 전체 내비게이션 그래프. 랜딩 → 로그인/회원가입 → 이메일 인증 → 권한 → 프로필 → 메인(탭). */
@Composable
fun OnuldoApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LANDING) {
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
                onNext = { email -> navController.navigate(Routes.emailVerify(email)) },
            )
        }
        composable(
            route = "${Routes.EMAIL_VERIFY}/{${Routes.EMAIL_VERIFY_ARG}}",
            arguments = listOf(navArgument(Routes.EMAIL_VERIFY_ARG) { type = NavType.StringType }),
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString(Routes.EMAIL_VERIFY_ARG).orEmpty()
            EmailVerifyScreen(
                email = email,
                onBack = { navController.popBackStack() },
                // "확인" → 권한 요청(3/4)로 진행.
                onConfirm = { navController.navigate(Routes.PERMISSION) },
            )
        }
        composable(Routes.PERMISSION) {
            PermissionScreen(
                onBack = { navController.popBackStack() },
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
    }
}
