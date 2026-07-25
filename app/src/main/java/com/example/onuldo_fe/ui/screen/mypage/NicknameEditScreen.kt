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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
 * 닉네임 변경 — Figma node `4019:4540`.
 * 설계서 규칙(2~8자 한글/영문/숫자, 특수문자 불가). 유효하고 기존과 다르면 '변경하기' 활성.
 *
 * TODO: 서버 닉네임 중복확인("이미 사용 중인 닉네임이에요") 연동.
 */
@Composable
fun NicknameEditScreen(
    onBack: () -> Unit,
    currentNickname: String = "오늘두",
) {
    var nickname by remember { mutableStateOf(currentNickname) }
    val isValid = Validators.isValidNickname(nickname)
    val isError = nickname.isNotEmpty() && !isValid

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
            color = MySubText,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(28.dp))
        OnuldoTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = "닉네임",
            placeholder = "2~8자 한글/영문/숫자",
            isError = isError,
            supportingText = if (isError) Validators.nicknameErrorMessage(nickname) else "2~8자 한글 · 영문 · 숫자",
        )

        Spacer(Modifier.weight(1f))

        OnulDoButton(
            text = "변경하기",
            onClick = { /* TODO: 닉네임 변경 API 연동 후 popBack */ onBack() },
            enabled = isValid && nickname != currentNickname,
        )
        Spacer(Modifier.height(18.dp))
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun NicknameEditScreenPreview() {
    OnulDo_FETheme {
        NicknameEditScreen(onBack = {})
    }
}
