package com.example.onuldo_fe.ui.screen.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard

@Composable
fun EmptyChallengeContent(
    modifier: Modifier = Modifier,
    onBrowseChallengesClick: () -> Unit = {}
) {
    val spacing = LocalSpacing.current
    // TODO: Persimmon15 색상 토큰 추가 후 교체
    // TODO: 디자인 시스템에 없는 9·42dp 여백 토큰 추가 후 교체

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Persimmon.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_question_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(width = 82.dp, height = 95.dp)
                    .offset(x = (-5).dp, y = 2.5.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.height(spacing.spacing16))
        Text(
            text = "아직 시작한 챌린지가 없어요",
            modifier = Modifier.fillMaxWidth(),
            color = BlackBrown,
            style = MaterialTheme.typography.titleLarge,
            lineHeight = 26.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Spacer(Modifier.height(9.dp))
        Text(
            text = "도전금을 걸고 작은 습관부터\n갓생을 시작해보세요!",
            color = DarkBrown,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelLarge,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            maxLines = 2
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
            // 제목·설명은 350dp를 사용하고 CTA만 Figma 기준 278dp로 제한
            modifier = Modifier.width(278.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 390, heightDp = 600)
@Composable
private fun EmptyChallengeContentPreview() {
    OnulDo_FETheme { EmptyChallengeContent(modifier = Modifier.fillMaxWidth()) }
}
