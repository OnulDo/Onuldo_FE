package com.example.onuldo_fe.ui.screen.party.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.onuldo_fe.viewmodel.party.InviteCodeError
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown10
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.Red
import com.example.onuldo_fe.ui.theme.Red2
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

@Composable
fun InviteCodeDialog(
    error: InviteCodeError?,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    onSubmit: (String) -> Unit,
    initialCode: String = "",
    isSubmitting: Boolean = false,
    networkErrorMessage: String? = null
) {
    var code by remember { mutableStateOf(initialCode) }
    val isError = error != null

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .width(350.dp)
                .height(320.dp)
                .background(SourCream, RoundedCornerShape(20.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO 디자인 시스템에 52dp 토큰이 추가되면 LocalSpacing으로 교체
            Spacer(Modifier.height(52.dp))
            Text(
                text = "초대코드 입력",
                color = BlackBrown,
                fontFamily = Pretendard,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.36).sp
            )
            Spacer(Modifier.height(LocalSpacing.current.spacing10))
            Text(
                text = error?.message ?: networkErrorMessage ?: "파티장에게 받은 6자리 코드를 입력하세요",
                color = if (isError || networkErrorMessage != null) Red else DarkBrown,
                fontFamily = Pretendard,
                fontSize = 12.sp,
                fontWeight = if (isError) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(LocalSpacing.current.spacing30))
            BasicTextField(
                value = code,
                onValueChange = { value ->
                    if (!isError) {
                        code = value
                            .filter { it in 'A'..'Z' || it in 'a'..'z' || it in '0'..'9' }
                            .uppercase()
                            .take(6)
                    }
                },
                singleLine = true,
                textStyle = TextStyle(color = Color.Transparent),
                decorationBox = { innerTextField ->
                    Box {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            repeat(6) { index ->
                                val character = code.getOrNull(index)?.toString().orEmpty()
                                val hasValue = character.isNotEmpty()
                                Box(
                                    modifier = Modifier
                                        .width(36.dp)
                                        .height(44.dp)
                                        .background(if (isError) Red2 else White, RoundedCornerShape(10.dp))
                                        .border(
                                            width = if (isError || hasValue) 1.5.dp else 1.dp,
                                            color = when {
                                                isError -> Red
                                                hasValue -> Persimmon
                                                else -> DarkBrown40
                                            },
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = character,
                                        color = if (isError) Red else BlackBrown,
                                        fontFamily = Pretendard,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        Box(Modifier.width(266.dp).height(44.dp)) { innerTextField() }
                    }
                }
            )
            Spacer(Modifier.height(LocalSpacing.current.spacing28))
            Button(
                onClick = {
                    if (isError) {
                        code = ""
                        onRetry()
                    } else {
                        onSubmit(code)
                    }
                },
                enabled = !isSubmitting && (isError || networkErrorMessage != null || code.length == 6),
                modifier = Modifier.width(278.dp).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Persimmon,
                    contentColor = SourCream,
                    disabledContainerColor = DarkBrown10,
                    disabledContentColor = DarkBrown
                )
            ) {
                Text(
                    text = when {
                        isSubmitting -> "확인 중..."
                        isError || networkErrorMessage != null -> "다시 입력"
                        else -> "참가하기"
                    },
                    fontFamily = Pretendard,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("취소", color = DarkBrown, fontFamily = Pretendard, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(name = "초대코드 입력", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun InviteCodeDialogPreview() {
    OnulDo_FETheme { InviteCodeDialog(null, {}, {}, {}, initialCode = "G7B2") }
}

@Preview(name = "초대코드 오류", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun InviteCodeErrorDialogPreview() {
    OnulDo_FETheme { InviteCodeDialog(InviteCodeError.Invalid, {}, {}, {}, initialCode = "9X2K04") }
}
