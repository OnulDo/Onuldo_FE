package com.example.onuldo_fe.ui.screen.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.component.OnuldoTextField
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageTopBar
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.utils.Validators
import com.example.onuldo_fe.viewmodel.mypage.NicknameEditViewModel

/**
 * 닉네임 변경 — Figma node `4019:4540`.
 * 설계서 규칙(2~8자 한글/영문/숫자, 특수문자 불가). 유효하고 기존과 다르면 '변경하기' 활성.
 *
 * [currentNickname]은 프로필 설정 화면이 이미 조회한 값을 라우트 인자로 넘겨받는다.
 * '변경하기'는 `PATCH /api/users/me/profile`로 nickname을 저장하고, **성공 응답에서만** [onBack]을 호출한다.
 * 중복 등 실패 사유("이미 사용 중인 닉네임이에요")는 서버 문구 그대로 입력칸 아래에 노출한다.
 */
@Composable
fun NicknameEditScreen(
    onBack: () -> Unit,
    currentNickname: String = "",
    viewModel: NicknameEditViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var nickname by remember { mutableStateOf(currentNickname) }
    val isValid = Validators.isValidNickname(nickname)
    // 형식 오류(로컬 검증)와 서버 실패 사유를 같은 입력칸 밑에 함께 다룬다.
    val isFormatError = nickname.isNotEmpty() && !isValid
    val serverError = state.errorMessage
    val isError = isFormatError || serverError != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        MyPageTopBar(title = "닉네임 변경", onBack = onBack)

        Spacer(Modifier.height(28.dp))
        Text(
            text = "새 닉네임을 입력해주세요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = BlackBrown,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "다른 사람에게 보여지는 이름이에요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = DarkBrown70,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(28.dp))
        OnuldoTextField(
            value = nickname,
            onValueChange = {
                nickname = it
                // 수정시) 이전 서버 실패 안내는 지움
                viewModel.clearError()
            },
            label = "닉네임",
            placeholder = "2~8자 한글/영문/숫자",
            isError = isError,
            supportingText = when {
                isFormatError -> Validators.nicknameErrorMessage(nickname)
                serverError != null -> serverError
                else -> "2~8자 한글 · 영문 · 숫자"
            },
        )

        Spacer(Modifier.weight(1f))

        OnulDoButton(
            text = "변경하기",
            onClick = { viewModel.updateNickname(nickname, onSuccess = onBack) },
            // 유효하고 기존과 다르며 제출 중이 아닐 때만 활성.
            enabled = isValid && nickname != currentNickname && !state.isSubmitting,
        )
        // 버튼은 바닥에서 42dp 띄운다.
        Spacer(Modifier.height(42.dp))
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun NicknameEditScreenPreview() {
    OnulDo_FETheme {
        NicknameEditScreen(onBack = {})
    }
}
