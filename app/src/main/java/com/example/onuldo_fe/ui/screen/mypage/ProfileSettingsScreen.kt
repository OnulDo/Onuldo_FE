package com.example.onuldo_fe.ui.screen.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.R
import com.example.onuldo_fe.data.user.CurrentProfileImageStore
import com.example.onuldo_fe.utils.ProfileAsset
import com.example.onuldo_fe.ui.component.AuthErrorBanner
import com.example.onuldo_fe.ui.component.RefreshOnResume
import com.example.onuldo_fe.ui.screen.login.CharacterPickerSheet
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
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.viewmodel.mypage.ProfileSettingsViewModel

/** 아바타 원형 배경. Figma: Persimmon 15%. */
private val AvatarBackground = Persimmon.copy(alpha = 0.15f)

/**
 * 프로필 설정 (마이 진입) — Figma node `4837:2412`.
 * 아바타 + 닉네임/이메일 + 기본 정보(닉네임·이메일) 행.
 *
 * 표시값은 `GET /api/users/me/profile`로 채운다.
 * 아바타 편집 배지를 누르면 캐릭터 선택 시트가 열리고, 고른 캐릭터는
 * `PATCH /api/users/me/profile`(`profileImageUrl`)로 바로 저장된다.
 */
@Composable
fun ProfileSettingsScreen(
    onBack: () -> Unit,
    /** 현재 닉네임을 함께 넘겨, 변경 화면이 재조회 없이 초기값을 채울 수 있게 한다. */
    onNicknameClick: (String) -> Unit,
    viewModel: ProfileSettingsViewModel = viewModel(),
) {
    val spacing = LocalSpacing.current
    val state by viewModel.uiState.collectAsState()
    val nickname = state.nickname
    val email = state.email
    var showCharacterPicker by remember { mutableStateOf(false) }
    // 닉네임 변경 후 이 화면으로 돌아오면 최신 값이 반영되도록 - 새로
    RefreshOnResume { viewModel.load() }
    // 공유 프로필 이미지(편집 직후·조회로 데워진 캐시)를 우선 사용하고, 없으면 조회값으로 폴백
    val sharedProfileUrl by CurrentProfileImageStore.profileImageUrl.collectAsState()
    val characterIndex = ProfileAsset.toCharacterIndex(sharedProfileUrl) ?: state.characterIndex
    val avatarRes = characterIndex
        ?.let { ProfileCharacters.getOrNull(it) }
        ?: R.drawable.img_avatar_running

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        MyPageTopBar(title = "프로필 설정", onBack = onBack)

        Spacer(Modifier.height(spacing.spacing28))

        // 아바타 + 편집 배지
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // 실기 실측(420dpi) 기준 보정. 20이면 닉네임이 Figma(8672:32176)보다 9dp 내려간다.
                .padding(bottom = 11.dp),
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
                // 누르면 캐릭터 선택 시트가 열리고, 고른 캐릭터가 프로필 사진으로 저장된다.
                Image(
                    painter = painterResource(R.drawable.ic_profile_edit_badge),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable(onClick = { showCharacterPicker = true })
                        .semantics {
                            role = Role.Button
                            contentDescription = "프로필 사진 변경"
                        },
                )
            }
        }

        if (state.avatarErrorMessage != null) {
            AuthErrorBanner(
                text = state.avatarErrorMessage.orEmpty(),
                modifier = Modifier.padding(horizontal = spacing.spacing20, vertical = spacing.spacing8),
            )
        }

        Text(
            text = nickname,
            style = OnulDoTypography.title1Bold,
            color = BlackBrown,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        // 최신 노드 `8672:32176` 기준(구 `4837:2412`는 폐기). 그쪽 렌더 잉크는 닉네임 242~261 · 이메일 275~287이다.
        // 이메일 박스(17)와 body4Medium.lineHeight(22)가 달라 박스 대 박스로는 못 맞추므로 실기 실측으로 잡았다.
        // 위에서 닉네임을 9dp 올렸기 때문에, 이메일을 제자리(275)에 두려면 같은 9dp를 여기서 돌려준다.
        Spacer(Modifier.height(9.dp))
        Text(
            text = email,
            style = OnulDoTypography.body4Medium,
            color = DarkBrown70,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        // 실측 보정: 32이면 "기본 정보"·카드 첫 행이 Figma보다 12.6dp 올라온다.
        Spacer(Modifier.height(39.dp))

        Text(
            text = "기본 정보",
            style = OnulDoTypography.caption2Medium,
            color = DarkBrown50,
            // Figma(8672:32176): "기본 정보" 박스 328~342 → 첫 행 352이므로 아래 간격은 10이다.
            modifier = Modifier.padding(start = spacing.spacing24, bottom = 10.dp),
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
                // 값 오른쪽 17dp (8.8 피드백 반영)
                valueStartPadding = 71.dp,
                valueEndPadding = 17.dp,
            )
        }
        Spacer(Modifier.height(spacing.spacing10))
        RowCard { MyPageMenuRow(title = "이메일", value = email, showChevron = false) }
    }

    if (showCharacterPicker) {
        CharacterPickerSheet(
            selectedIndex = state.characterIndex,
            onSelect = { index ->
                    showCharacterPicker = false
                    viewModel.updateAvatar(index) {}
            },
            onDismiss = { showCharacterPicker = false },
        )
    }
}

@Composable
private fun RowCard(content: @Composable () -> Unit) {
    val spacing = LocalSpacing.current
    Box(
        modifier = Modifier
            .padding(horizontal = spacing.spacing20)
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
