package com.example.onuldo_fe.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.onuldo_fe.navigation.BottomTab
import com.example.onuldo_fe.ui.theme.DarkBrown50

@Composable
fun OnuldoBottomBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        BottomTab.items.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
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
                    // 임시 아이콘 placeholder — 디자인 아이콘 에셋 확보 시 Icon(painterResource(...))로 교체
                    Box(
                        Modifier
                            .size(6.dp)
                            .background(
                                color = if (selected) MaterialTheme.colorScheme.primary
                                else DarkBrown50,
                                shape = CircleShape,
                            )
                    )
                },
                label = { Text(tab.label) },
            )
        }
    }
}
