package com.example.onuldo_fe.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard

// [OnulDoButton]과 동일한 규격(높이 56dp, 라운드 14dp, 좌우 20dp 여백).
private val AuthButtonShape = RoundedCornerShape(14.dp)
private const val AuthButtonHeight = 56

/**
 * 외곽선 보조 버튼 (회원가입 등). primary 채움 버튼은 [OnulDoButton] 사용.
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(AuthButtonHeight.dp),
        enabled = enabled,
        shape = AuthButtonShape,
        border = BorderStroke(1.dp, Persimmon),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Persimmon),
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = Pretendard,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

/**
 * 소셜 로그인 버튼 (카카오/네이버). 아이콘은 좌측 정렬, 텍스트는 중앙 정렬.
 */
@Composable
fun SocialLoginButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes leadingIcon: Int? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(AuthButtonHeight.dp),
        shape = AuthButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
    ) {
        Box(Modifier.fillMaxWidth()) {
            if (leadingIcon != null) {
                Image(
                    painter = painterResource(leadingIcon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.CenterStart),
                )
            }
            Text(
                text = text,
                fontSize = 16.sp,
                fontFamily = Pretendard,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}
