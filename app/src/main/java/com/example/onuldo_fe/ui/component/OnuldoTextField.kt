package com.example.onuldo_fe.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.Red
import com.example.onuldo_fe.ui.theme.White

/**
 * 라벨 + 입력 필드 + 하단 헬퍼/에러 텍스트를 묶은 공통 텍스트필드.
 * 좌우 20dp 여백은 [OnulDoButton]과 동일하게 컴포넌트 내부에서 적용한다.
 *
 * 상태 우선순위: [isError](빨강) > [isSuccess](초록) > 기본. 헬퍼 텍스트 색도 동일하게 따라간다.
 */
@Composable
fun OnuldoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isError: Boolean = false,
    isSuccess: Boolean = false,
    supportingText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
) {
    val supportingColor = when {
        isError -> Red
        isSuccess -> Green
        else -> DarkBrown50
    }
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = label,
           style = OnulDoTypography.body6Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            textStyle = OnulDoTypography.body5Regular,
            placeholder = {
                Text(
                    placeholder,
                    color = DarkBrown40,
                    style = OnulDoTypography.body5Regular,
                )
            },
            singleLine = true,
            isError = isError,
            visualTransformation =
                if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isSuccess) Green else Persimmon,
                unfocusedBorderColor = if (isSuccess) Green else DarkBrown40,
                errorBorderColor = Red,
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                errorContainerColor = White,
            ),
        )
        if (supportingText != null) {
            Text(
                text = supportingText,
                style = OnulDoTypography.caption3Regular,
                color = supportingColor,
                modifier = Modifier.padding(PaddingValues(start = 4.dp, top = 4.dp)),
            )
        }
    }
}
