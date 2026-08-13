package com.example.onuldo_fe.ui.screen.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown80
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.White

@Composable
fun SettlementCompleteCard(
    partyName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .height(64.dp)
            .clip(shape)
            .background(White, shape)
            .border(BorderStroke(1.5.dp, Persimmon), shape)
            .clickable(onClick = onClick)
    ) {
        // Figma 프레임 좌표와 크기를 그대로 적용해 요소별 위치 고정
        Image(
            painter = painterResource(R.drawable.home_congratulation_icon),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .offset(x = 17.dp, y = 20.dp)
                .size(width = 22.dp, height = 21.17.dp)
        )
        Text(
            text = stringResource(R.string.home_settlement_complete_title),
            color = BlackBrown,
            style = OnulDoTypography.body4Bold,
            modifier = Modifier
                .offset(x = 47.dp, y = 23.dp)
                .width(112.dp)
        )
        // API에서 긴 챌린지명이 들어오면 최대 69dp 안에서 한 줄 말줄임 처리
        Text(
            text = partyName,
            color = DarkBrown80,
            style = OnulDoTypography.caption2Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-40).dp, y = 25.dp)
                .widthIn(max = 69.dp)
        )
        Image(
            painter = painterResource(R.drawable.home_arrow_right),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-24.5).dp, y = 26.dp)
                .size(width = 5.5.dp, height = 11.05.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 390)
@Composable
private fun SettlementCompleteCardPreview() {
    OnulDo_FETheme {
        SettlementCompleteCard(
            "새벽 러너 파티",
            {},
            Modifier.width(350.dp)
        )
    }
}
