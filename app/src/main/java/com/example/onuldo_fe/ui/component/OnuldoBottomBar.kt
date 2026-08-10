package com.example.onuldo_fe.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.onuldo_fe.navigation.BottomTab
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard

/** BottomNav 배경 (#FFFDF7) · 상단 보더 (DarkBrown 20%). Figma BottomNav `5042:1511` 기준. */
private val BottomBarBackground = Color(0xFFFFFDF7)
private val BottomBarBorder = Color(0x335C2C03)
private val UnselectedTint = BlackBrown.copy(alpha = 0.5f)

/**
 * 하단 내비게이션 바 — Figma BottomNav(`5042:1511`), 아이콘(`4353:73393`).
 * 5탭(홈·챌린지·파티·기록·마이). 선택 = Persimmon, 미선택 = BlackBrown 50%.
 * 라벨 = Pretendard Bold 12px. 선택 표시는 pill 없이 아이콘·라벨 색상만 변경한다.
 */
@Composable
fun OnuldoBottomBar(
    navController: NavController,
    onTabClick: (BottomTab) -> Unit = {}
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Column {
        HorizontalDivider(thickness = 1.dp, color = BottomBarBorder)
        NavigationBar(
            containerColor = BottomBarBackground,
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
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Persimmon,
                        selectedTextColor = Persimmon,
                        unselectedIconColor = UnselectedTint,
                        unselectedTextColor = UnselectedTint,
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
