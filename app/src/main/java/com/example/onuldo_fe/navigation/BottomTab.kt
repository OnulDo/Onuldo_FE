package com.example.onuldo_fe.navigation

/**
 * 하단 내비게이션 5탭 (Ready for Dev 확정 디자인: 홈·챌린지·파티·기록·마이).
 * 아이콘은 추후 디자인 에셋(drawable)로 교체 예정 — 현재는 라벨 + 임시 dot.
 */
sealed class BottomTab(
    val route: String,
    val label: String,
) {
    data object Home : BottomTab("home", "홈")
    data object Challenge : BottomTab("challenge", "챌린지")
    data object Party : BottomTab("party", "파티")
    data object Record : BottomTab("record", "기록")
    data object My : BottomTab("my", "마이")

    companion object {
        val items = listOf(Home, Challenge, Party, Record, My)
    }
}
