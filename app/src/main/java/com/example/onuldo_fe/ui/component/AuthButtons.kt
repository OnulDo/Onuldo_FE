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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard

// [OnulDoButton]과 동일한 규격(높이 56dp, 라운드 14dp, 좌우 20dp 여백).
private val AuthButtonShape = RoundedCornerShape(14.dp)
private val SocialButtonShape = RoundedCornerShape(8.dp)
private const val AuthButtonHeight = 56

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
        // Figma 랜딩(4771:375) btn/primary 외곽선은 2px다.
        border = BorderStroke(2.dp, Persimmon),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Persimmon),
    ) {
        Text(
            text = text,
            style = OnulDoTypography.body2Bold
        )
    }
}

@Composable
fun SocialLoginButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes leadingIcon: Int? = null,
    iconSize: Dp = 24.dp,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(AuthButtonHeight.dp),
        // 소셜 버튼만 radius 8이다(Figma `5446:7969`의 --radius-s). 일반 버튼은 14.
        shape = SocialButtonShape,
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
                        .size(iconSize)
                        .align(Alignment.CenterStart),
                )
            }
            Text(
                text = text,
                // Figma는 카카오 전용 폰트(Kakao Small Sans) 18px을 쓴다. 앱에는 그 폰트가 없어
                // 크기만 맞춘 Pretendard 18sp로 대체한다.
                style = OnulDoTypography.body1Bold,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}
