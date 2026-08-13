package com.example.onuldo_fe.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.onuldo_fe.navigation.BottomTab
import com.example.onuldo_fe.ui.theme.BlackBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.SourCream

/**
 * 하단 내비게이션 바 — Figma BottomNav(`9196:67545`), 아이콘(`4353:73393`).
 * 5탭(홈·챌린지·파티·기록·마이). 선택 = Persimmon, 미선택 = BlackBrown 50%.
 * 라벨 = Caption2/Bold. 선택 표시는 pill 없이 아이콘·라벨 색상만 변경한다.
 *
 * 배경(Sour Cream)·상단 보더(DarkBrown 20%)·미선택 색은 모두 디자인시스템 토큰과 같은 값이라
 * 로컬 상수를 두지 않고 토큰을 그대로 쓴다.
 */
@Composable
fun OnuldoBottomBar(
    navController: NavController,
    onTabClick: (BottomTab) -> Unit = {}
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Column {
        HorizontalDivider(thickness = 1.dp, color = DarkBrown20)
        NavigationBar(
            // Figma BottomNav는 상단 보더 포함 84. 기본값(80)을 쓰면 3dp 모자란다.
            modifier = Modifier.height(83.dp),
            containerColor = SourCream,
            tonalElevation = 0.dp,
        ) {
            BottomTab.items.forEach { tab ->
                val selected = currentRoute == tab.route
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        onTabClick(tab)
                        if (selected) {
                            return@NavigationBarItem
                        } else {
                            navController.navigate(tab.route) {
                                // 탭 전환 시 백스택이 쌓이지 않도록 시작 목적지까지 pop + 상태 저장/복원
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(tab.icon),
                            contentDescription = tab.label,
                        )
                    },
                    label = {
                        Text(
                            text = tab.label,
                            style = OnulDoTypography.caption2Bold,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Persimmon,
                        selectedTextColor = Persimmon,
                        unselectedIconColor = BlackBrown50,
                        unselectedTextColor = BlackBrown50,
                        indicatorColor = Color.Transparent,
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun OnuldoBottomBarPreview() {
    OnulDo_FETheme {
        // 프리뷰용 임시 navController — '홈' 탭이 선택된 초기 상태로 표시된다.
        OnuldoBottomBar(navController = rememberNavController())
    }
}
