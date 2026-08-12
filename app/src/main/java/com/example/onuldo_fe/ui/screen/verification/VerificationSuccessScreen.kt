package com.example.onuldo_fe.ui.screen.verification

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme


@Composable
fun VerificationSuccessScreen(onConfirmClick: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 27.dp)
                    .height(48.dp)
            ) {
                Text(
                    text = "인증 결과",
                    color = BlackBrown,
                    style = OnulDoTypography.body2Bold,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }

            Image(
                painter = painterResource(
                    id = R.drawable.verification_success_icon
                ),
                contentDescription = "인증 성공 아이콘",
                modifier = Modifier
                    .padding(top = 243.dp)
                    .size(135.dp)
            )

            Text(
                text = "인증 성공!",
                color = BlackBrown,
                style = OnulDoTypography.title1Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "AI 검증을 모두 통과했어요",
                color = DarkBrown70,
                style = OnulDoTypography.body4Regular,
                modifier = Modifier.padding(top = 11.dp),
                textAlign = TextAlign.Center
            )
        }

        OnulDoButton(
            text = "확인",
            onClick = onConfirmClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 42.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VerificationSuccessScreenPreview() {
    OnulDo_FETheme {
        VerificationSuccessScreen()
    }
}