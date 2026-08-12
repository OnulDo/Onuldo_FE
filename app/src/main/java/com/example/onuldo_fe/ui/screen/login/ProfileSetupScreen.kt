package com.example.onuldo_fe.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.AuthErrorBanner
import com.example.onuldo_fe.ui.component.OnboardingBackHeader
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.component.OnuldoTextField
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.viewmodel.ProfileSetupViewModel


@Composable
fun ProfileSetupScreen(
    onBack: () -> Unit,
    onDone: () -> Unit,
    onEmailChangeRequired: (String) -> Unit = {},
    onExistingAccount: (String) -> Unit = {},
    viewModel: ProfileSetupViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var showCharacterPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        OnboardingBackHeader(onBack = onBack)

        Spacer(Modifier.height(24.dp))
        Text(
            text = "프로필을 설정해주세요",
            style = OnulDoTypography.header1ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "프로필과 닉네임을 정해주세요",
            style = OnulDoTypography.body5Regular,
            color = DarkBrown70,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(24.dp))
        Text(
            text = "프로필",
            style = OnulDoTypography.body6Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(16.dp))
        val selectedChar = state.selectedCharacterIndex
        // 캐릭터 아바타 — 탭하면 선택 시트를 연다.
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(120.dp)
                .clickable { showCharacterPicker = true },
        ) {
            if (selectedChar == null) {
                // 미선택(디자인): 점선 빈 원 + 중앙 "+".
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawCircle(
                                color = DarkBrown40,
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f)),
                                ),
                            )
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    // 텍스트 '+'는 baseline 때문에 원 중앙에서 위로 치우친다. 도형 아이콘으로 정렬을 맞춘다.
                    Image(
                        painter = painterResource(R.drawable.ic_plus),
                        contentDescription = "프로필 사진 선택",
                        colorFilter = ColorFilter.tint(Persimmon),
                        modifier = Modifier.size(40.dp),
                    )
                }
            } else {
                // 선택됨: 채운 원 + "+" 편집 배지.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Persimmon10),
                    contentAlignment = Alignment.Center,
                ) {
                    // 캐릭터는 배경 투명 PNG. Figma(node 5154:5045) 실측대로 원(120dp)의
                    // 약 90%(91×108)를 채우도록 108dp로 배치 → 중앙 정렬 시 오프셋도 Figma와 일치.
                    Image(
                        painter = painterResource(ProfileCharacters[selectedChar]),
                        contentDescription = "프로필 캐릭터",
                        modifier = Modifier.size(108.dp),
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Persimmon)
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_plus),
                        contentDescription = "프로필 사진 변경",
                        colorFilter = ColorFilter.tint(White),
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        OnuldoTextField(
            value = state.nickname,
            onValueChange = viewModel::onNicknameChange,
            label = "닉네임",
            placeholder = "2~8자 한글/영문/숫자",
            isError = state.nicknameError,
            supportingText = state.nicknameSupport ?: "닉네임은 가입 후 마이페이지에서 변경할 수 있어요",
            imeAction = ImeAction.Done,
        )

        Spacer(Modifier.weight(1f))

        // 회원가입 API가 이 화면에서 호출되므로 서버 실패 문구도 여기에 노출된다.
        // 이메일 중복처럼 이 화면에서 못 고치는 오류는 회원가입 화면으로 되돌아가도록 안내한다.
        state.errorMessage?.let { message ->
            AuthErrorBanner(
                text = if (state.requiresEmailChange) {
                    "$message\n회원가입 화면으로 돌아가 이메일을 변경해주세요."
                } else {
                    message
                },
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(12.dp))
        }

        OnulDoButton(
            text = if (state.isLoading) "가입 중..." else "계속",
            onClick = { viewModel.submit(onDone, onEmailChangeRequired, onExistingAccount) },
            enabled = state.isContinueEnabled,
        )
        Spacer(Modifier.height(24.dp))
    }

    if (showCharacterPicker) {
        CharacterPickerSheet(
            selectedIndex = state.selectedCharacterIndex,
            onSelect = viewModel::onCharacterSelect,
            onDismiss = { showCharacterPicker = false },
        )
    }
}
