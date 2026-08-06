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
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.utils.Validators

/** 프로필 수정 API 연동 여부. 서버에 엔드포인트가 생기면 true로 바꾼다. */
private const val PROFILE_UPDATE_API_READY = false

/**
 * 닉네임 변경 — Figma node `4019:4540`.
 * 설계서 규칙(2~8자 한글/영문/숫자, 특수문자 불가). 유효하고 기존과 다르면 '변경하기' 활성.
 *
 * [currentNickname]은 프로필 설정 화면이 이미 조회한 값을 라우트 인자로 넘겨받는다.
 *
 * TODO: 서버에 프로필 수정 API(`PATCH /api/users/me/profile`)가 없어 **입력값을 저장할 수 없다.**
 *       API가 생기면 [PROFILE_UPDATE_API_READY]를 true로 바꾸고 '변경하기'에 연동한 뒤,
 *       성공 응답에서만 [onBack]을 호출한다. 닉네임 중복확인("이미 사용 중인 닉네임이에요")도 함께 붙인다.
 */
@Composable
fun NicknameEditScreen(
    onBack: () -> Unit,
    currentNickname: String = "",
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
            color = DarkBrown70,
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

        // 저장할 수단이 없는데 화면만 닫으면 사용자는 변경이 끝난 줄 안다.
        // API가 붙기 전까지는 눌리지 않게 두고 이유를 밝힌다.
        if (!PROFILE_UPDATE_API_READY) {
            Text(
                text = "서버 연동 준비 중이라 아직 저장할 수 없어요",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = DarkBrown70,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
        }

        OnulDoButton(
            text = "변경하기",
            onClick = { /* TODO: 닉네임 변경 API 연동 시 성공 응답에서만 onBack() */ },
            enabled = PROFILE_UPDATE_API_READY && isValid && nickname != currentNickname,
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
