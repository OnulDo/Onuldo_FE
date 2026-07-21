package com.example.onuldo_fe.navigation

import androidx.annotation.DrawableRes
import com.example.onuldo_fe.R

/**
 * 하단 내비게이션 5탭 (Ready for Dev 확정 디자인: 홈·챌린지·파티·기록·마이).
 * 아이콘은 Figma 아이콘 세트(`4353:73393`)에서 추출한 라인 벡터. 선택/미선택은 런타임 틴트로 처리.
 */
sealed class BottomTab(
    val route: String,
    val label: String,
    @param:DrawableRes val icon: Int,
) {
    data object Home : BottomTab("home", "홈", R.drawable.ic_nav_home)
    data object Challenge : BottomTab("challenge", "챌린지", R.drawable.ic_nav_challenge)
    data object Party : BottomTab("party", "파티", R.drawable.ic_nav_party)
    data object Record : BottomTab("record", "기록", R.drawable.ic_nav_record)
    data object My : BottomTab("my", "마이", R.drawable.ic_nav_my)

    companion object {
        val items = listOf(Home, Challenge, Party, Record, My)
    }
}
