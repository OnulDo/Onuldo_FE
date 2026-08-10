package com.example.onuldo_fe.ui.screen.party.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.font.FontWeight
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.SourCream

@Composable
fun PartyNameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    BasicTextField(
        value = value,
        // 문자 규칙은 [파티 만들기] 클릭 시 검증하고 여기서는 최대 길이만 제한
        onValueChange = { input -> onValueChange(input.take(10)) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = TextStyle(
            color = BlackBrown,
            fontFamily = Pretendard,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal
        ),
        cursorBrush = SolidColor(Persimmon),
        decorationBox = { innerTextField ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(White, RoundedCornerShape(14.dp))
                    .border(1.dp, if (isError) Persimmon else DarkBrown40, RoundedCornerShape(14.dp))
                    // TODO 디자인 시스템에 19dp 토큰이 추가되면 LocalSpacing으로 교체
                    .padding(horizontal = 19.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text("예: 갓생팟", color = DarkBrown40, fontFamily = Pretendard, fontSize = 13.sp)
                }
                innerTextField()
            }
        }
    )
}

@Preview(name = "파티 이름 입력", showBackground = true, widthDp = 390)
@Composable
private fun PartyNameTextFieldPreview() {
    var value by remember { mutableStateOf("") }
    OnulDo_FETheme {
        Box(Modifier.background(SourCream).padding(20.dp)) {
            PartyNameTextField(value = value, onValueChange = { value = it })
        }
    }
}
