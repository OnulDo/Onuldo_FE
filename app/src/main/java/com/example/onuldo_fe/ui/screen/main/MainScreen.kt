package com.example.onuldo_fe.ui.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import androidx.navigation.compose.currentBackStackEntryAsState
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

/**
 * 로그인 이후 진입하는 메인 화면. 하단 5탭 내비 + 탭별 NavHost.
 * 홈·챌린지·파티·기록은 타 팀원 구현분 연결, 마이는 내 담당.
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
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    /**
     * 탭 **안에서** 내비바를 잠시 감춰야 하는 상태용(파티 탭의 하위 화면 등).
     * 그 화면을 벗어날 때 반드시 다시 true로 되돌려야 한다.
     *
     * ⚠️ 아래 [isFullScreenRoute]에 해당하는 목적지에서는 이 값을 건드리지 말 것.
     * 그쪽은 라우트만 보고 감추므로 여기까지 끄면 화면을 벗어난 뒤에도 false로 남아
     * **모든 탭에서 내비바가 사라진다.**
     */
    var showBottomBar by remember { mutableStateOf(true) }
    var homeRefreshKey by rememberSaveable { mutableIntStateOf(0) }
    var partyTabClickKey by rememberSaveable { mutableIntStateOf(0) }

    /**
     * 이 NavHost에 등록됐지만 내비바 위에 풀스크린으로 떠야 하는 목적지.
     * 현재 라우트만 보고 판단하므로 목적지를 벗어나면 자동으로 원래대로 돌아온다.
     */
    val isFullScreenRoute = currentRoute == Routes.PARTY_SETTLEMENT

    Scaffold(
        bottomBar = {
            if (showBottomBar && !isFullScreenRoute) {
                OnuldoBottomBar(
                    navController = navController,
                    onTabClick = { tab ->
                        if (tab == BottomTab.Party) partyTabClickKey++
                    }
                )
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
                    onCameraNavigate = { challengeId, category, title, deadline ->
                        onNavigate(Routes.camera(challengeId, category, title, deadline))
                    },
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
                    },
                    // 알림 탭 → 챌린지 상세(루트 nav)
                    onChallengeClick = { challengeId ->
                        onNavigate(Routes.challengeDetail(challengeId))
                    }
                )
            }
            composable(
                route = Routes.PARTY_SETTLEMENT,
                arguments = listOf(navArgument("partyId") { type = NavType.LongType })
            ) { backStackEntry ->
                val partyId = backStackEntry.arguments?.getLong("partyId") ?: return@composable

                // 내비바는 isFullScreenRoute가 감춘다(showBottomBar를 건드리지 않는다).
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
                    tabClickKey = partyTabClickKey,
                    onBottomBarVisibilityChange = { showBottomBar = it },
                    onCameraNavigate = { challengeId, title, deadline ->
                        onNavigate(Routes.camera(challengeId, "", title, deadline))
                    },
                    // 충전 화면은 루트 내비에만 등록돼 있다(마이·지갑·챌린지 참여와 동일 경로).
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
                    onNotificationClick = { onNavigate(Routes.MYPAGE_NOTIFICATION) },
                    onTermClick = { termType -> onNavigate(Routes.mypageTerm(termType.name)) },
                    onLoggedOut = onLoggedOut,
                )
            }
        }
    }
}
