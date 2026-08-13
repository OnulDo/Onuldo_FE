package com.example.onuldo_fe.ui.screen.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.BlackBrown70
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.ui.screen.party.components.PartyNetworkImage
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeHeader(
    userName: String,
    profileImageUrl: String? = null,
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {}
) {
    val spacing = LocalSpacing.current
    val notificationInteractionSource = remember { MutableInteractionSource() }
    val isNotificationPressed by notificationInteractionSource.collectIsPressedAsState()
    var isClickFeedbackActive by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Persimmon20, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 기본 캐릭터는 지정 크기로 표시하고 프로필 이미지 URL은 원형 영역을 채운다.
            PartyNetworkImage(
                imageUrl = profileImageUrl,
                fallbackImageRes = R.drawable.home_run_light_icon,
                contentDescription = "$userName 프로필",
                modifier = Modifier.size(width = 28.dp, height = 33.dp),
                contentScale = ContentScale.Fit,
                networkModifier = Modifier.fillMaxSize().clip(CircleShape),
                networkContentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(spacing.spacing10))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "오늘두 함께 도전!",
                color = BlackBrown70,
                style = OnulDoTypography.caption4Bold
            )
            Text(
                text = userName,
                color = BlackBrown,
                style = OnulDoTypography.body1ExtraBold
            )
        }

        Box(
            modifier = Modifier
                .size(30.dp)
                .background(White, CircleShape)
                .border(BorderStroke(0.5.dp, DarkBrown20), CircleShape)
                .clickable(
                    interactionSource = notificationInteractionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = {
                        if (!isClickFeedbackActive) {
                            // 짧게 눌러도 눌림 색상이 보이도록 100ms 유지
                            isClickFeedbackActive = true
                            coroutineScope.launch {
                                delay(100L)
                                isClickFeedbackActive = false
                                onNotificationClick()
                            }
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.home_bell_icon),
                contentDescription = "알림",
                modifier = Modifier.size(17.dp),
                tint = if (isNotificationPressed || isClickFeedbackActive) Persimmon else BlackBrown
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 390)
@Composable
private fun HomeHeaderPreview() {
    OnulDo_FETheme {
        val spacing = LocalSpacing.current
        HomeHeader(
            userName = "김민지",
            modifier = Modifier
                .fillMaxWidth()
                .background(SourCream)
                .padding(horizontal = spacing.spacing20)
        )
    }
}
