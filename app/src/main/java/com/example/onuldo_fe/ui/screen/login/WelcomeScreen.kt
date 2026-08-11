package com.example.onuldo_fe.ui.screen.login

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard


/**
 * 가입 완료(환영) 화면 — 온보딩 마지막, Figma node `4310:1375`.
 * 히어로 카드(파티 캐릭터 + 색종이) + "환영해요!" + 축하 문구 + "오늘두 시작하기".
 * 온보딩 종료 화면이라 진행 헤더·뒤로가기 없음.
 */
@Composable
fun WelcomeScreen(
    onStart: () -> Unit,
) {
    // 회원가입 완료 유저만 거치는 화면이므로, 여기서 딱 1회 시스템 푸시 권한(POST_NOTIFICATIONS)을 요청한다.
    // (로그인 유저는 이 화면을 안 거침 · 인앱 알림함 열람은 권한과 무관)
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* 허용/거부 결과는 시스템 푸시 수신 여부에만 영향 — 별도 처리 없음 */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 상태바(44) 아래 히어로 top 79 → 여백 35
        Spacer(Modifier.height(230.dp))

        HeroCard()

        Spacer(Modifier.height(20.dp))

        Text(
            text = "환영해요!",
            style = MaterialTheme.typography.headlineLarge,
            color = BlackBrown,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "오늘DO 가족이 되신 걸 축하드려요\n이제 첫 챌린지를 등록해볼까요?",
            style = MaterialTheme.typography.labelLarge,
            color = DarkBrown70,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = "가입 시 환영 보너스 100,000P가 함께 시작돼요",
            style = MaterialTheme.typography.bodyMedium,
            color = Persimmon
            )

        Spacer(Modifier.height(12.dp))

        OnulDoButton(
            text = "오늘DO 시작하기",
            onClick = onStart,
        )

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun HeroCard(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(R.drawable.auth_welcome_icon),
        contentDescription = null,
        modifier = modifier.size(135.dp),
    )
}
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun WelcomeScreenPreview() {
    OnulDo_FETheme {
        WelcomeScreen(onStart = {})
    }
}
