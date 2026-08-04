package com.example.onuldo_fe.ui.screen.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.data.auth.dto.TermType
import com.example.onuldo_fe.viewmodel.mypage.MyMainViewModel
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageMenuRow
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White

/**
 * 마이 - 메인 (Figma node `4310:2206`). 하단 5탭 스캐폴드의 '마이' 탭 콘텐츠.
 * 프로필 카드 · 포인트 지갑 요약 · 설정/약관/정보 메뉴 리스트.
 *
 * 닉네임·이메일·보유 포인트는 `GET /api/users/me`로 채운다.
 * 서비스 탈퇴는 서버 API가 없어 아직 동작하지 않는다.
 */
@Composable
fun MyMainScreen(
    onProfileClick: () -> Unit,
    onWalletClick: () -> Unit,
    onChargeClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onTermClick: (TermType) -> Unit = {},
    onLoggedOut: () -> Unit = {},
    viewModel: MyMainViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val nickname = state.nickname
    val email = state.email
    val point = state.pointText

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(bottom = 24.dp),
    ) {
        Spacer(Modifier.height(20.dp))

        // 프로필 카드 → 프로필 설정
        ProfileCard(nickname = nickname, email = email, onClick = onProfileClick)

        Spacer(Modifier.height(16.dp))

        // 포인트 지갑 요약
        WalletSummary(
            point = point,
            onWalletClick = onWalletClick,
            onChargeClick = onChargeClick,
            onWithdrawClick = onWithdrawClick,
        )

        Spacer(Modifier.height(16.dp))

        SectionLabel("설정")
        //알림 이동 추가 (시온)
        MenuCard(title = "알림 설정", onClick = onNotificationClick)

        Spacer(Modifier.height(20.dp))

        SectionLabel("약관 및 정책")
        MenuCard(title = "서비스 이용약관", onClick = { onTermClick(TermType.SERVICE) })
        Spacer(Modifier.height(10.dp))
        MenuCard(title = "개인정보 처리방침", onClick = { onTermClick(TermType.PRIVACY) })
        Spacer(Modifier.height(10.dp))
        MenuCard(title = "환급 정책", onClick = { onTermClick(TermType.REFUND) })

        Spacer(Modifier.height(20.dp))

        SectionLabel("정보")
        MenuCard(title = "앱 버전", value = "1.0.0 (Beta)", showChevron = false)

        Spacer(Modifier.height(40.dp))

        Text(
            text = "로그아웃",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Persimmon,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.logout(onLoggedOut) },
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "서비스 탈퇴",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            color = DarkBrown50,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .fillMaxWidth()
                // 서버에 회원 탈퇴 API가 없어 아직 연결하지 못했다. API 추가 시 확인 다이얼로그와 함께 연결한다.
                .clickable { /* TODO: 회원 탈퇴 API 추가 후 연결 */ },
        )
    }
}

@Composable
private fun ProfileCard(nickname: String, email: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .border(1.dp, DarkBrown40, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Persimmon20),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.img_avatar_running),
                contentDescription = null,
                modifier = Modifier.size(57.dp),
            )
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = nickname,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = BlackBrown,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = email,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = DarkBrown70,
            )
        }
        Text(
            text = "›",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = DarkBrown50,
        )
    }
}

@Composable
private fun WalletSummary(
    point: String,
    onWalletClick: () -> Unit,
    onChargeClick: () -> Unit,
    onWithdrawClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Persimmon10)
            .border(1.dp, Persimmon20, RoundedCornerShape(14.dp))
            .padding(20.dp),
    ) {
        Column(modifier = Modifier.clickable(onClick = onWalletClick)) {
            Text(
                text = "내 포인트 지갑",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.44.sp,
                color = BlackBrown.copy(alpha = 0.7f),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = point,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                letterSpacing = (-0.56).sp,
                color = Persimmon,
            )
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            WalletActionButton(
                text = "충전",
                filled = true,
                onClick = onChargeClick,
                modifier = Modifier.weight(1f),
            )
            WalletActionButton(
                text = "출금",
                filled = false,
                onClick = onWithdrawClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun WalletActionButton(
    text: String,
    filled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (filled) Persimmon else White)
            .then(if (filled) Modifier else Modifier.border(1.5.dp, Persimmon20, RoundedCornerShape(14.dp)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (filled) White else Persimmon,
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = DarkBrown50,
        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
    )
}

@Composable
private fun MenuCard(
    title: String,
    value: String? = null,
    showChevron: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .border(1.dp, DarkBrown40, RoundedCornerShape(14.dp)),
    ) {
        MyPageMenuRow(
            title = title,
            value = value,
            showChevron = showChevron,
            onClick = onClick,
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun MyMainScreenPreview() {
    OnulDo_FETheme {
        MyMainScreen(
            onProfileClick = {}, onWalletClick = {}, onChargeClick = {},
            onWithdrawClick = {}, onNotificationClick = {},
        )
    }
}
