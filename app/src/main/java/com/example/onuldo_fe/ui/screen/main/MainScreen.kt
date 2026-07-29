package com.example.onuldo_fe.ui.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onuldo_fe.navigation.BottomTab
import com.example.onuldo_fe.navigation.Routes
import com.example.onuldo_fe.ui.component.OnuldoBottomBar
import com.example.onuldo_fe.ui.screen.challenge.gallery.GalleryScreen
import com.example.onuldo_fe.ui.screen.home.HomeRoute
import com.example.onuldo_fe.ui.screen.mypage.MyMainScreen
import com.example.onuldo_fe.ui.screen.party.PartyRoute
import com.example.onuldo_fe.ui.screen.record.RecordScreen
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
) {
    val navController = rememberNavController()
    var showBottomBar by remember { mutableStateOf(true) }

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
                    onCameraNavigate = { onNavigate(Routes.CAMERA) }
                )
            }
            composable(BottomTab.Challenge.route) {
                GalleryScreen(
                    onChallengeClick = { onNavigate(Routes.CHALLENGE_DETAIL) },
                )
            }
            composable(BottomTab.Party.route) {
                PartyRoute(
                    onBottomBarVisibilityChange = { showBottomBar = it },
                    onCameraNavigate = { onNavigate(Routes.CAMERA) }
                )
            }
            composable(BottomTab.Record.route) {
                RecordScreen(
                    progressList = emptyList(),
                    completeList = listOf(
                        CompleteRecord(
                            isSuccess = true,
                            title = "운동 30분 하기",
                            progress = 100,
                            completeDate = "2026.07.22",
                            point = 5000
                        ),
                        CompleteRecord(
                            isSuccess = false,
                            title = "책 50페이지 읽기",
                            progress = 70,
                            completeDate = "2026.07.18",
                            point = -1000
                        )
                    )
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
                )
            }
        }
    }
}
