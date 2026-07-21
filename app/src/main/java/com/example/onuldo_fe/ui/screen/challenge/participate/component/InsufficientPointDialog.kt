package com.example.onuldo_fe.ui.screen.challenge.participate.component
import com.example.onuldo_fe.ui.screen.challenge.component.ChallengeNoticeBox

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.Red3
import com.example.onuldo_fe.ui.theme.SourCream

// 더미 데이터 — 나중에 DB(서버 API) 연결하면 실제 보유/필요 포인트로 교체 예정
private const val DUMMY_OWNED_POINT = 5_000
private const val DUMMY_REQUIRED_POINT = 10_000

/**
 * 잔액 부족 - 충전 유도 다이얼로그 — 참여 화면의 "도전 시작하기"에서 포인트가 모자랄 때 뜸
 * 부족분은 (필요 - 보유)로 계산
 */
@Composable
fun InsufficientPointDialog(
    onDismiss: () -> Unit,
    onCharge: () -> Unit,
    modifier: Modifier = Modifier,
    ownedPoint: Int = DUMMY_OWNED_POINT,
    requiredPoint: Int = DUMMY_REQUIRED_POINT
) {
    val shortage = (requiredPoint - ownedPoint).coerceAtLeast(0)

    Dialog(
        onDismissRequest = onDismiss,
        // !! 기본 폭 제한을 꺼야 350 지정이 먹음 !!(클로드)
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = modifier
                .width(350.dp)
                .height(380.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(SourCream),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(34.dp))

            // 캐릭터 + 원
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Red3),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.challenge_sad_icon),
                    contentDescription = null,
                    modifier = Modifier.size(width = 58.dp, height = 76.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "포인트가 부족해요",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 20.sp,
                color = BlackBrown,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "챌린지 시작을 위해 도전금이 필요해요",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 13.sp,
                color = DarkBrown,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            ChallengeNoticeBox(
                height = 78.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(top = 19.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        PointColumn(
                            label = "보유 포인트",
                            value = "%,dP".format(ownedPoint),
                            valueColor = BlackBrown
                        )
                        OperatorColumn(symbol = "−", width = 28.dp)
                        PointColumn(
                            label = "필요 포인트",
                            value = "%,dP".format(requiredPoint),
                            valueColor = BlackBrown
                        )
                        OperatorColumn(symbol = "=", width = 35.dp)
                        PointColumn(
                            label = "부족분",
                            value = "%,dP".format(shortage),
                            valueColor = Persimmon
                        )
                    }
                }
            }

            Spacer(Modifier.height(19.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DialogButton(
                    text = "취소",
                    onClick = onDismiss,
                    modifier = Modifier.width(144.dp)
                )
                DialogButton(
                    text = "포인트 충전",
                    onClick = onCharge,
                    filled = true,
                    modifier = Modifier.width(158.dp)
                )
            }
        }
    }
}

//포인트 한 칸 (라벨 + 값)
@Composable
private fun PointColumn(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            color = DarkBrown70,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = value,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            color = valueColor,
            textAlign = TextAlign.Center
        )
    }
}

//연산자 (− / =) — 값 줄에 맞춰 내려서 배치
@Composable
private fun OperatorColumn(
    symbol: String,
    width: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(width),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Text(
            text = symbol,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 18.sp,
            color = DarkBrown70,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun InsufficientPointDialogPreview() {
    OnulDo_FETheme {
        InsufficientPointDialog(onDismiss = {}, onCharge = {})
    }
}
