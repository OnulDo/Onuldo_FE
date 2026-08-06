package com.example.onuldo_fe.ui.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.onuldo_fe.navigation.BottomTab
import com.example.onuldo_fe.navigation.Routes
import com.example.onuldo_fe.ui.component.OnuldoBottomBar
import com.example.onuldo_fe.ui.screen.challenge.gallery.GalleryRoute
import com.example.onuldo_fe.ui.screen.home.HomeRoute
import com.example.onuldo_fe.ui.screen.mypage.MyMainScreen
import com.example.onuldo_fe.ui.screen.party.PartyRoute
import com.example.onuldo_fe.ui.screen.party.PartySettlementRoute
import com.example.onuldo_fe.ui.screen.record.RecordRoute
import com.example.onuldo_fe.ui.screen.record.data.CompleteRecord
import kotlin.collections.emptyList

/**
 * 로그인 이후 진입하는 메인 화면. 하단 5탭 내비 + 탭별 NavHost.
 * 홈·챌린지·기록은 타 팀원 구현분 연결, 파티는 아직 placeholder. 마이는 내 담당.
 *
 * [onNavigate]는 앱 최상위(루트) 내비게이션으로, 마이페이지 하위 화면을
 * 하단 탭 위에 풀스크린으로 띄우기 위해 사용한다.
 */
@Composable
fun MainScreen(
    onNavigate: (String) -> Unit = {},
    onLoggedOut: () -> Unit = {},
) {
    val navController = rememberNavController()
    var showBottomBar by remember { mutableStateOf(true) }
    var homeRefreshKey by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                OnuldoBottomBar(navController)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomTab.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(BottomTab.Home.route) {
                HomeRoute(
                    onCameraNavigate = { onNavigate(Routes.CAMERA) },
                    refreshKey = homeRefreshKey,
                    onBrowseChallengesClick = {
                        // 빈 홈 CTA에서 기존 챌린지 탭의 GalleryScreen으로 이동
                        navController.navigate(BottomTab.Challenge.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onSettlementResultClick = { partyId ->
                        navController.navigate(Routes.partySettlement(partyId))
                    }
                )
            }
            composable(
                route = Routes.PARTY_SETTLEMENT,
                arguments = listOf(navArgument("partyId") { type = NavType.LongType })
            ) { backStackEntry ->
                val partyId = backStackEntry.arguments?.getLong("partyId") ?: return@composable

                LaunchedEffect(Unit) { showBottomBar = false }
                DisposableEffect(Unit) {
                    onDispose { showBottomBar = true }
                }

                PartySettlementRoute(
                    partyId = partyId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(BottomTab.Challenge.route) {
                GalleryRoute(
                    onChallengeClick = { challenge ->
                        onNavigate(Routes.challengeDetail(challenge.id))
                    },
                )
            }
            composable(BottomTab.Party.route) {
                PartyRoute(
                    onBottomBarVisibilityChange = { showBottomBar = it },
                    onCameraNavigate = { onNavigate(Routes.CAMERA) },
                    onChargePoint = { onNavigate(Routes.MYPAGE_CHARGE) },
                    onHomeNavigate = {
                        // 새로 시작한 파티 재조회와 홈 상단 이동을 한 번에 요청
                        homeRefreshKey++
                        navController.navigate(BottomTab.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(BottomTab.Record.route) {
                RecordRoute(
                    onBrowseChallenges = {
                        navController.navigate(BottomTab.Challenge.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(BottomTab.My.route) {
                MyMainScreen(
                    onProfileClick = { onNavigate(Routes.MYPAGE_PROFILE) },
                    onWalletClick = { onNavigate(Routes.MYPAGE_WALLET) },
                    onChargeClick = { onNavigate(Routes.MYPAGE_CHARGE) },
                    onWithdrawClick = { onNavigate(Routes.MYPAGE_WITHDRAW) },
                    onAccountClick = { onNavigate(Routes.MYPAGE_ACCOUNT) },
                    onNotificationClick = { onNavigate(Routes.MYPAGE_NOTIFICATION) },
                    onTermClick = { termType -> onNavigate(Routes.mypageTerm(termType.name)) },
                    onLoggedOut = onLoggedOut,
                )
            }
        }
    }
}
