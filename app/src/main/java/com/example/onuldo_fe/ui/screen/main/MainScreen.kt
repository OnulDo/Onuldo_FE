package com.example.onuldo_fe.ui.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.onuldo_fe.model.home.notification.NotificationLanding
import com.example.onuldo_fe.model.home.notification.NotificationLandingBus
import com.example.onuldo_fe.navigation.BottomTab
import com.example.onuldo_fe.navigation.Routes
import com.example.onuldo_fe.ui.component.OnuldoBottomBar
import com.example.onuldo_fe.ui.screen.challenge.gallery.GalleryRoute
import com.example.onuldo_fe.ui.screen.home.HomeRoute
import com.example.onuldo_fe.ui.screen.mypage.MyMainScreen
import com.example.onuldo_fe.ui.screen.mypage.PointChargeScreen
import com.example.onuldo_fe.ui.screen.party.PartyRoute
import com.example.onuldo_fe.ui.screen.party.PartySettlementRoute
import com.example.onuldo_fe.ui.screen.record.RecordRoute
import com.example.onuldo_fe.ui.screen.record.RecordTab

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
    var showBottomBar by remember { mutableStateOf(true) }
    var homeRefreshKey by rememberSaveable { mutableIntStateOf(0) }
    var partyTabClickKey by rememberSaveable { mutableIntStateOf(0) }
    val isFullScreenRoute = currentRoute == Routes.MYPAGE_CHARGE ||
        currentRoute == Routes.PARTY_SETTLEMENT

    // 기기 푸시 탭 랜딩(NOTI-04): 기록 탭 초기 탭과 파티 피드 진입 partyId를 소비-1회 상태로 둔다.
    var recordInitialTab by remember { mutableStateOf(RecordTab.PROGRESS) }
    var pendingFeedPartyId by remember { mutableStateOf<Long?>(null) }

    // 하단 탭으로 이동(기존 탭 전환과 동일한 백스택 정책 재사용).
    fun switchTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    // 푸시 탭으로 결정된 목적지를 소비해 실제 화면으로 이동한다.
    // 로그인 상태에서만 존재하는 MainScreen이 처리하므로 미로그인 시 잘못된 이동을 막는다.
    val pendingLanding by NotificationLandingBus.pending.collectAsState()
    LaunchedEffect(pendingLanding) {
        when (val landing = pendingLanding) {
            null -> Unit
            NotificationLanding.Home -> switchTab(BottomTab.Home.route)
            is NotificationLanding.ChallengeDetail ->
                onNavigate(Routes.challengeDetail(landing.challengeId))
            is NotificationLanding.PartySettlement ->
                navController.navigate(Routes.partySettlement(landing.partyId))
            NotificationLanding.RecordOngoing -> {
                recordInitialTab = RecordTab.PROGRESS
                switchTab(BottomTab.Record.route)
            }
            NotificationLanding.RecordCompleted, NotificationLanding.SoloRecord -> {
                recordInitialTab = RecordTab.COMPLETE
                switchTab(BottomTab.Record.route)
            }
            is NotificationLanding.PartyFeed -> {
                pendingFeedPartyId = landing.partyId
                switchTab(BottomTab.Party.route)
            }
        }
        if (pendingLanding != null) NotificationLandingBus.consume()
    }

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

                LaunchedEffect(Unit) { showBottomBar = false }
                DisposableEffect(Unit) {
                    onDispose { showBottomBar = true }
                }

                PartySettlementRoute(
                    partyId = partyId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.MYPAGE_CHARGE) {
                LaunchedEffect(Unit) { showBottomBar = false }

                PointChargeScreen(onBack = { navController.popBackStack() })
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
                    openFeedPartyId = pendingFeedPartyId,
                    onFeedOpened = { pendingFeedPartyId = null },
                    onCameraNavigate = { challengeId, title, deadline ->
                        onNavigate(Routes.camera(challengeId, "", title, deadline))
                    },
                    onChargePoint = { navController.navigate(Routes.MYPAGE_CHARGE) },
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
                    initialTab = recordInitialTab,
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
