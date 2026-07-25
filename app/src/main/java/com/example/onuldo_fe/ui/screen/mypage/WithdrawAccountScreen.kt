package com.example.onuldo_fe.ui.screen.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageTopBar
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White

/** 출금 계좌 관리 강조 오렌지 (#FA8729). */
private val AccentOrange = Color(0xFFFA8729)

/**
 * 출금 계좌 관리 — Figma node `4019:4580`.
 * 등록된 계좌 카드 + 다른 계좌로 변경 + 계좌 삭제.
 *
 * 값은 더미. TODO: 계좌 등록/변경/삭제 및 본인 인증 연동.
 */
@Composable
fun WithdrawAccountScreen(
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        MyPageTopBar(title = "출금 계좌 관리", onBack = onBack)

        Spacer(Modifier.height(28.dp))
        Text(
            text = "환급받을 계좌를 관리해요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = BlackBrown,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "본인 명의 계좌만 등록할 수 있어요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MyValueText,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(28.dp))
        Text(
            text = "등록된 계좌",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = MySubText,
            modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
        )

        LinkedAccountCard()

        Spacer(Modifier.height(16.dp))
        OutlineActionButton(
            text = "다른 계좌로 변경",
            textColor = AccentOrange,
            borderColor = AccentOrange,
            onClick = { /* TODO: 계좌 변경 플로우 */ },
        )
        Spacer(Modifier.height(16.dp))
        OutlineActionButton(
            text = "계좌 삭제",
            textColor = MyValueText,
            borderColor = MyCardBorder,
            onClick = { /* TODO: 계좌 삭제 확인 다이얼로그 */ },
        )

        Spacer(Modifier.height(28.dp))
        Text(
            text = "ⓘ 계좌 변경 시 본인 인증이 필요해요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = MySubText,
            modifier = Modifier.padding(start = 24.dp),
        )
    }
}

@Composable
private fun LinkedAccountCard() {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(96.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(1.dp, MyCardBorder, RoundedCornerShape(16.dp))
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AccentOrange),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "국민",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = White,
            )
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "국민은행",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = BlackBrown,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "1234-**-****-5678",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = Color(0xFF595959),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "홍길동 · 기본 계좌",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = MySubText,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFFFF6EC))
                .padding(horizontal = 8.dp, vertical = 3.dp),
        ) {
            Text(
                text = "기본",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = AccentOrange,
            )
        }
    }
}

@Composable
private fun OutlineActionButton(
    text: String,
    textColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = textColor,
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun WithdrawAccountScreenPreview() {
    OnulDo_FETheme {
        WithdrawAccountScreen(onBack = {})
    }
}
