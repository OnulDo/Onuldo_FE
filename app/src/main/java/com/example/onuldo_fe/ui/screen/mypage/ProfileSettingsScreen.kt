package com.example.onuldo_fe.ui.screen.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.screen.login.ProfileCharacters
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageMenuRow
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageTopBar
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.viewmodel.mypage.ProfileSettingsViewModel

/** 아바타 원형 배경. Figma: Persimmon 15%. */
private val AvatarBackground = Persimmon.copy(alpha = 0.15f)

/**
 * 프로필 설정 (마이 진입) — Figma node `4837:2412`.
 * 아바타 + 닉네임/이메일 + 기본 정보(닉네임·이메일) 행.
 *
 * 표시값은 `GET /api/users/me/profile`로 채운다.
 * 아바타 편집 배지는 서버에 프로필 수정 API가 없어 아직 동작하지 않는다.
 */
@Composable
fun ProfileSettingsScreen(
    onBack: () -> Unit,
    /** 현재 닉네임을 함께 넘겨, 변경 화면이 재조회 없이 초기값을 채울 수 있게 한다. */
    onNicknameClick: (String) -> Unit,
    viewModel: ProfileSettingsViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val nickname = state.nickname
    val email = state.email
    // 서버가 배정한 캐릭터가 앱 목록에 있으면 그 캐릭터를, 없으면 기본 아바타를 쓴다.
    val avatarRes = state.characterIndex
        ?.let { ProfileCharacters.getOrNull(it) }
        ?: R.drawable.img_avatar_running

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
                        painter = painterResource(avatarRes),
                        contentDescription = null,
                        modifier = Modifier.size(84.dp),
                    )
                }
                // 편집 배지는 Figma 에셋(연필). 이모지를 쓰면 기기마다 모양이 달라진다.
                Image(
                    painter = painterResource(R.drawable.ic_profile_edit_badge),
                    contentDescription = "프로필 사진 변경",
                    modifier = Modifier.size(32.dp),
                )
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
            color = DarkBrown70,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "기본 정보",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = DarkBrown50,
            modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
        )

        RowCard {
            // 조회 전에는 nickname이 비어 있다. 그대로 넘기면 변경 화면이 초기값 없이 열려
            // 기존과 같은 닉네임을 입력해도 '변경하기'가 활성화되므로, 값이 올 때까지 막는다.
            MyPageMenuRow(
                title = "닉네임",
                value = nickname,
                onClick = if (nickname.isNotBlank()) {
                    { onNicknameClick(nickname) }
                } else {
                    null
                },
            )
        }
        Spacer(Modifier.height(10.dp))
        RowCard { MyPageMenuRow(title = "이메일", value = email, showChevron = false) }
    }
}

@Composable
private fun RowCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            // Figma: 카드에 1dp 테두리(brand/dark-brown/40)가 들어간다.
            .border(1.dp, DarkBrown40, RoundedCornerShape(14.dp)),
    ) {
        content()
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ProfileSettingsScreenPreview() {
    OnulDo_FETheme {
        ProfileSettingsScreen(onBack = {}, onNicknameClick = {})
    }
}
