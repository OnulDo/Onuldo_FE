package com.example.onuldo_fe.ui.screen.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.component.OnuldoTextField
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageTopBar
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.utils.Validators

/**
 * 비밀번호 변경 — Figma node `4019:4557`.
 * 현재/새/새 확인 3필드. 새 비밀번호는 설계서 규칙(영문·숫자·특수문자 8~20자), 확인은 일치 검증.
 *
 * TODO: 현재 비밀번호 서버 검증 및 변경 API 연동.
 */
@Composable
fun PasswordChangeScreen(
    onBack: () -> Unit,
) {
    var current by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    val isNewValid = Validators.isValidPassword(newPassword)
    val newError = newPassword.isNotEmpty() && !isNewValid
    val isMatch = confirm.isNotEmpty() && confirm == newPassword
    val confirmError = confirm.isNotEmpty() && confirm != newPassword

    val canSubmit = current.isNotEmpty() && isNewValid && isMatch

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        MyPageTopBar(title = "비밀번호 변경", onBack = onBack)

        Spacer(Modifier.height(28.dp))
        Text(
            text = "안전한 비밀번호로 변경해주세요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = BlackBrown,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "영문 · 숫자 · 특수문자 조합 8~20자",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MySubText,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(24.dp))
        OnuldoTextField(
            value = current,
            onValueChange = { current = it },
            label = "현재 비밀번호",
            placeholder = "현재 비밀번호 입력",
            isPassword = true,
            keyboardType = KeyboardType.Password,
        )
        Spacer(Modifier.height(20.dp))
        OnuldoTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = "새 비밀번호",
            placeholder = "새 비밀번호 입력",
            isPassword = true,
            isError = newError,
            supportingText = if (newError) Validators.passwordErrorMessage(newPassword) else null,
            keyboardType = KeyboardType.Password,
        )
        Spacer(Modifier.height(20.dp))
        OnuldoTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = "새 비밀번호 확인",
            placeholder = "새 비밀번호 다시 입력",
            isPassword = true,
            isError = confirmError,
            isSuccess = isMatch,
            supportingText = when {
                confirmError -> "비밀번호가 일치하지 않습니다"
                isMatch -> "비밀번호가 일치합니다"
                else -> "비밀번호 규칙: 영문·숫자·특수문자 8~20자"
            },
            keyboardType = KeyboardType.Password,
        )

        Spacer(Modifier.weight(1f))

        OnulDoButton(
            text = "변경하기",
            onClick = { /* TODO: 비밀번호 변경 API 연동 후 popBack */ onBack() },
            enabled = canSubmit,
        )
        Spacer(Modifier.height(18.dp))
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PasswordChangeScreenPreview() {
    OnulDo_FETheme {
        PasswordChangeScreen(onBack = {})
    }
}
