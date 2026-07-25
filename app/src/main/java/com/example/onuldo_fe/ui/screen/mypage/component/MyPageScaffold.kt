package com.example.onuldo_fe.ui.screen.mypage.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.screen.mypage.MyLine
import com.example.onuldo_fe.ui.screen.mypage.MySubText
import com.example.onuldo_fe.ui.screen.mypage.MyValueText
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.Pretendard

/**
 * 마이페이지 하위 화면 공통 상단바 — 뒤로가기(‹) + 중앙 타이틀 + 하단 구분선.
 * (온보딩의 진행 헤더와 달리 마이페이지는 이 형태를 쓴다.)
 */
@Composable
fun MyPageTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = title,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = BlackBrown,
            )
            Text(
                text = "‹",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = BlackBrown,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
                    .size(width = 24.dp, height = 40.dp)
                    .clickable(onClick = onBack),
            )
        }
        HorizontalDivider(thickness = 1.dp, color = MyLine)
    }
}

/**
 * 마이페이지 리스트/메뉴 행 — 좌측 타이틀 + 우측 값/셰브론. (배경 카드는 호출부에서 지정)
 * [value]가 있으면 값을 표시, [showChevron]이면 오른쪽 화살표(›)를 표시한다.
 */
@Composable
fun MyPageMenuRow(
    title: String,
    modifier: Modifier = Modifier,
    value: String? = null,
    showChevron: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = BlackBrown,
        )
        Text(
            text = value.orEmpty(),
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MyValueText,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, end = if (showChevron) 8.dp else 0.dp),
        )
        if (showChevron) {
            Text(
                text = "›",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MySubText,
            )
        }
    }
}
