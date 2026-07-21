package com.example.onuldo_fe.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnboardingBackHeader
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown10
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White

/**
 * 권한 요청 안내 — WF ver.2.2, 온보딩 3/4 (Figma node 4353:3229).
 * 카메라(필수)·알림(필수)·사진(선택) 권한 안내 카드 + "계속".
 *
 * ⚠️ 실제 런타임 권한 요청은 하지 않는다(안내 화면만). 권한은 각 기능 사용 시점에 인라인 요청 예정.
 */
@Composable
fun PermissionScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        OnboardingBackHeader(onBack = onBack)

        Spacer(Modifier.height(24.dp))
        Text(
            text = "오늘DO 시작 전,\n권한을 허용해 주세요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 30.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "아래 권한은 챌린지 인증·알림에 사용돼요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = DarkBrown70,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(28.dp))

        PermissionCard(
            icon = R.drawable.ic_perm_camera,
            title = "카메라",
            description = "챌린지 인증 사진 촬영에 필요해요",
            required = true,
        )
        Spacer(Modifier.height(12.dp))
        PermissionCard(
            icon = R.drawable.ic_perm_notification,
            title = "알림",
            description = "인증 시간 리마인더와 결과 안내",
            required = true,
        )
        Spacer(Modifier.height(12.dp))
        PermissionCard(
            icon = R.drawable.ic_perm_photo,
            title = "사진",
            description = "인증 사진 저장에 사용해요",
            required = false,
        )

        Spacer(Modifier.height(16.dp))
        Text(
            text = "ⓘ  이후 언제든 설정에서 변경할 수 있어요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            color = DarkBrown50,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.weight(1f))

        // TODO: 실제 런타임 권한 요청 연동. 지금은 안내만 하고 다음 단계로 진행.
        OnulDoButton(text = "계속", onClick = onNext)
        Spacer(Modifier.height(24.dp))
    }
}

/** 권한 항목 카드 — 아이콘 + 제목/설명 + 필수/선택 칩. */
@Composable
private fun PermissionCard(
    icon: Int,
    title: String,
    description: String,
    required: Boolean,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(80.dp)
            .background(White, RoundedCornerShape(14.dp))
            .border(1.dp, DarkBrown40, RoundedCornerShape(14.dp))
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = description,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = DarkBrown50,
            )
        }
        RequirementChip(required = required)
    }
}

/** 필수(오렌지) / 선택(브라운) 칩. */
@Composable
private fun RequirementChip(required: Boolean) {
    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 22.dp)
            .background(
                if (required) Persimmon10 else DarkBrown10,
                RoundedCornerShape(11.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (required) "필수" else "선택",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = if (required) Persimmon else DarkBrown,
        )
    }
}
