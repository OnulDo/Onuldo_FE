package com.example.onuldo_fe.ui.screen.login

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnboardingBackHeader
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown10
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

/** 아이콘 박스 배경 — Figma는 Persimmon 15%로, 공용 팔레트에 없는 값이라 이 화면에서만 정의한다. */
private val PermissionIconBackground = Persimmon.copy(alpha = 0.15f)

/**
 * 화면에 안내할 권한 항목.
 *
 * [required]는 표시용 칩(필수/선택)일 뿐 진행을 막지 않는다. 설계서(A109) 기준으로
 * 필수 권한을 거부해도 온보딩은 계속되고, 해당 기능에 처음 진입할 때 다시 요청한다.
 */
private enum class PermissionItem(
    val label: String,
    val description: String,
    @param:DrawableRes val icon: Int,
    val required: Boolean,
) {
    CAMERA("카메라", "챌린지 인증 사진 촬영에 필요해요", R.drawable.ic_perm_camera, required = true),
    NOTIFICATION("알림", "인증 시간 리마인더와 결과 안내", R.drawable.ic_perm_notification, required = true),
    PHOTO("사진", "인증 사진 저장에 사용해요", R.drawable.ic_perm_photo, required = false),
}

/**
 * 요청할 시스템 권한 목록.
 *
 * `POST_NOTIFICATIONS`는 API 33(TIRAMISU)부터 생긴 런타임 권한이라 그 아래에서는 요청 대상이 아니다
 * (33 미만은 설치 시 허용된 것으로 본다). 사진도 33부터 세분화된 `READ_MEDIA_IMAGES`로 바뀌었다.
 * 목록에 없는 권한을 요청하면 시스템이 조용히 거부로 처리하므로 버전에 맞는 것만 담는다.
 */
private fun runtimePermissions(): Array<String> = buildList {
    add(Manifest.permission.CAMERA)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        add(Manifest.permission.POST_NOTIFICATIONS)
        add(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        @Suppress("DEPRECATION")
        add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}.toTypedArray()

/**
 * 온보딩 권한 요청 — Figma Ready for Dev node `4771:468`, 설계서 `A109`.
 *
 * 약관 동의와 프로필 설정 사이 단계다. 이메일 가입과 소셜 신규 가입이 공통으로 거친다.
 *
 * **권한 결과와 무관하게 [onContinue]로 진행한다.** 설계서상 필수 권한(카메라·알림)을 거부해도
 * 가입 자체를 막지 않고, 카메라·알림이 실제로 필요한 화면에서 다시 요청하는 정책이다.
 * 여기서 막으면 권한을 영구 거부한 사용자가 온보딩을 끝내지 못한다.
 *
 * 상단 진행바(3/4)는 이 Figma 프레임에 남아 있지만 **구현하지 않는다.** 다른 온보딩 화면
 * (회원가입·약관·프로필)은 모두 진행바가 제거된 상태이고, 팀에서 진행바 없이 가기로 확정했다
 * (CLAUDE.md 2026-07-21). 이 프레임만 갱신이 안 된 것으로 보인다.
 */
@Composable
fun PermissionRequestScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    // 권한 다이얼로그는 카메라 → 알림 → 사진 순으로 차례로 뜬다.
    // 결과는 시스템이 보관하므로 앱에서 따로 저장하지 않는다.
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { onContinue() }

    val gutter = Modifier.padding(horizontal = 20.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SourCream)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        OnboardingBackHeader(onBack = onBack)

        Spacer(Modifier.height(37.dp))

        Text(
            text = "오늘DO 시작 전,\n권한을 허용해 주세요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 30.sp,
            color = BlackBrown,
            modifier = gutter,
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "아래 권한은 챌린지 인증·알림에 사용돼요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = DarkBrown70,
            modifier = gutter,
        )

        Spacer(Modifier.height(46.dp))

        PermissionItem.entries.forEachIndexed { index, item ->
            if (index > 0) Spacer(Modifier.height(12.dp))
            PermissionCard(item = item, modifier = gutter)
        }

        Spacer(Modifier.height(11.dp))

        Text(
            text = "ⓘ  권한은 언제든지 설정에서 변경할 수 있어요",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            color = DarkBrown50,
            modifier = gutter,
        )

        Spacer(Modifier.weight(1f))

        OnulDoButton(
            text = "계속",
            onClick = { permissionLauncher.launch(runtimePermissions()) },
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun PermissionCard(
    item: PermissionItem,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .border(1.dp, DarkBrown40, RoundedCornerShape(14.dp))
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(PermissionIconBackground),
        ) {
            // drawable이 48×48 좌표계 안에 글리프 위치까지 담고 있어 박스를 그대로 채운다.
            Image(
                painter = painterResource(item.icon),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.label,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = BlackBrown,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.description,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = DarkBrown50,
            )
        }

        Spacer(Modifier.width(12.dp))

        RequirementChip(required = item.required)
    }
}

/** 필수/선택 칩. 필수는 Persimmon 계열, 선택은 DarkBrown 계열로 톤을 낮춘다. */
@Composable
private fun RequirementChip(required: Boolean) {
    val containerColor: Color = if (required) Persimmon10 else DarkBrown10
    val contentColor: Color = if (required) Persimmon else DarkBrown

    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 22.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(containerColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (required) "필수" else "선택",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = contentColor,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 390, heightDp = 844)
@Composable
private fun PermissionRequestScreenPreview() {
    OnulDo_FETheme {
        PermissionRequestScreen(onBack = {}, onContinue = {})
    }
}
