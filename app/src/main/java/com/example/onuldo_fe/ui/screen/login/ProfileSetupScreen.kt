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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.ui.component.OnboardingBackHeader
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.component.OnuldoTextField
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.viewmodel.ProfileSetupViewModel

/**
 * 프로필 설정 — 온보딩 4/4 (Ready for Dev node `5154:4417`).
 * 점선 빈 원 + "+"(미선택) → 탭 시 캐릭터 선택 바텀시트 → 닉네임 입력 → "계속".
 */
@Composable
fun ProfileSetupScreen(
    onBack: () -> Unit,
    onDone: () -> Unit,
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
            fontFamily = Pretendard,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "프로필과 닉네임을 정해주세요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = DarkBrown70,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(24.dp))
        Text(
            text = "프로필",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
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
                    Text(
                        text = "+",
                        color = Persimmon,
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Normal,
                        fontSize = 44.sp,
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
                    Text(
                        text = "+",
                        color = White,
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
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

        OnulDoButton(
            text = "계속",
            onClick = { viewModel.submit(onDone) },
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
