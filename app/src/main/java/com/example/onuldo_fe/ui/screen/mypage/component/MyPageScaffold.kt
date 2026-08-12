package com.example.onuldo_fe.ui.screen.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.Pretendard

/**
 * 마이페이지 하위 화면 공통 상단바 — 뒤로가기 + 중앙 타이틀 (Figma node `4837:2418`).
 *
 * 뒤로가기는 다른 화면과 동일한 [OnulDoBackButton]을 쓴다(직접 그린 '‹' 문자를 쓰면
 * 화면마다 크기·정렬이 어긋난다). 헤더 아래 구분선은 최신 디자인에서 빠졌다.
 */
@Composable
fun MyPageTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            lineHeight = 20.sp,
            color = BlackBrown,
        )
        OnulDoBackButton(
            // IconButton이 자체 여백을 가져 별도 padding 없이 가로 20dp에 맞는다.
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = onBack,
        )
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
    // 값 좌우 여백. 기본값은 기존 동작(라벨과 12dp, 셰브론/끝과 8/0dp). 특정 화면에서 간격을 지정할 때 넘긴다.
    valueStartPadding: Dp = 12.dp,
    valueEndPadding: Dp? = null,
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
            style = OnulDoTypography.body4Bold,
            color = BlackBrown,
        )
        Text(
            text = value.orEmpty(),
            style = OnulDoTypography.caption1Medium,
            color = DarkBrown50,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = valueStartPadding,
                    end = valueEndPadding ?: if (showChevron) 8.dp else 0.dp,
                ),
        )
        if (showChevron) {
            // Figma에서 추출한 셰브론(5×8). 문자 '›'를 쓰면 화면마다 크기가 달라진다.
            Image(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                modifier = Modifier.size(width = 5.dp, height = 8.dp),
            )
        }
    }
}
