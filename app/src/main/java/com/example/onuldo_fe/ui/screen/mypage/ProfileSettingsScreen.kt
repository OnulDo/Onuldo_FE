package com.example.onuldo_fe.ui.screen.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageMenuRow
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageTopBar
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White

/** 프로필 아바타 원형 배경 (#FFEBDE). */
private val AvatarBackground = Color(0xFFFFEBDE)

/**
 * 프로필 설정 (마이 진입) — Figma node `4019:4672`.
 * 아바타 + 닉네임/이메일 + 기본 정보(닉네임 변경·이메일·비밀번호 변경) 행.
 *
 * 값은 더미. TODO: ViewModel/API 연동. 아바타 편집(📷)은 TODO 스텁.
 */
@Composable
fun ProfileSettingsScreen(
    onBack: () -> Unit,
    onNicknameClick: () -> Unit,
    onPasswordClick: () -> Unit,
    nickname: String = "오늘두",
    email: String = "user@example.com",
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        MyPageTopBar(title = "프로필 설정", onBack = onBack)

        Spacer(Modifier.height(28.dp))

        // 아바타 + 편집 배지
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(AvatarBackground),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.img_avatar_running),
                        contentDescription = null,
                        modifier = Modifier.size(84.dp),
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Persimmon),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "📷", fontSize = 14.sp)
                }
            }
        }

        Text(
            text = nickname,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = BlackBrown,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = email,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MySubText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "기본 정보",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = MySubText,
            modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
        )

        RowCard { MyPageMenuRow(title = "닉네임", value = nickname, onClick = onNicknameClick) }
        Spacer(Modifier.height(10.dp))
        RowCard { MyPageMenuRow(title = "이메일", value = email, showChevron = false) }
        Spacer(Modifier.height(10.dp))
        RowCard { MyPageMenuRow(title = "비밀번호 변경", onClick = onPasswordClick) }
    }
}

@Composable
private fun RowCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(White),
    ) {
        content()
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ProfileSettingsScreenPreview() {
    OnulDo_FETheme {
        ProfileSettingsScreen(onBack = {}, onNicknameClick = {}, onPasswordClick = {})
    }
}
