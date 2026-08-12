package com.example.onuldo_fe.ui.screen.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.EmptyStateContent
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

@Composable
fun EmptyChallengeContent(
    modifier: Modifier = Modifier,
    onBrowseChallengesClick: () -> Unit = {}
) {
    // TODO: 디자인 시스템에 없는 42dp 여백 토큰 추가 후 교체
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        EmptyStateContent(
            iconRes = R.drawable.home_question_icon,
            title = "아직 시작한 챌린지가 없어요",
            description = "도전금을 걸고 작심삼일부터\n갓생까지 시작해보세요!",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(42.dp))
        OnulDoButton(
            text = "챌린지 둘러보기",
            onClick = onBrowseChallengesClick,
            height = 52.dp,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            horizontalPadding = 0.dp,
            pressedContainerColor = DarkBrown,
            // 제목·설명은 전체 너비를 사용하고 CTA만 Figma 기준 278dp로 제한
            modifier = Modifier.width(278.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 390, heightDp = 600)
@Composable
private fun EmptyChallengeContentPreview() {
    OnulDo_FETheme { EmptyChallengeContent(modifier = Modifier.fillMaxWidth()) }
}
