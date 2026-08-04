package com.example.onuldo_fe.model.challenge

import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge

// 서버의 페이지 정보를 함께 보관한다.
// 현재는 첫 페이지만 사용
// hasNext와 page는 첫번째 QA 이후 페이지네이션 구현 시 사용 (지금은 권한땜에 안보여서 미리x)
data class ChallengePage(
    val challenges: List<Challenge>,
    val page: Int,
    val hasNext: Boolean
)
