package com.example.onuldo_fe.ui.component.party

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.Green2
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.Red
import com.example.onuldo_fe.ui.theme.Red2
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import kotlinx.coroutines.launch

private val partySuccessConditions = listOf(
    "침대와 개어진 이불이 사진에 함께 보여요",
    "인증 가능 시간(05:00 ~ 07:00) 내에 촬영",
    "카메라로 직접 촬영한 사진만 인정돼요",
    "본인 침대가 명확히 식별돼요"
)

private val partyFailureConditions = listOf(
    "갤러리에서 업로드한 사진",
    "침대 또는 이불이 보이지 않음",
    "이불이 정돈되지 않은 상태",
    "미션 조건(개어진 이불) 미충족",
    "동일/유사 사진 재사용"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyVerificationNoticeBottomSheet(
    challengeTitle: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    fun dismiss() {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SourCream,
        scrimColor = BlackBrown.copy(alpha = 0.4f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(Modifier.size(width = 36.dp, height = 4.dp).clip(RoundedCornerShape(2.dp)).background(DarkBrown20))
            }
        },
        modifier = modifier
    ) {
        Text(
            "인증 유의사항",
            modifier = Modifier.padding(start = 24.dp),
            color = BlackBrown,
            fontFamily = Pretendard,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(17.dp))
        HorizontalDivider(thickness = 1.dp, color = DarkBrown20)
        Text(
            challengeTitle,
            modifier = Modifier.padding(start = 24.dp, top = 10.dp, bottom = 7.dp),
            color = DarkBrown,
            fontFamily = Pretendard,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PartyNoticeConditionCard(
                title = "인증 성공 조건",
                conditions = partySuccessConditions,
                accentColor = Green,
                backgroundColor = Green2,
                iconRes = R.drawable.party_challenge_notice_success
            )
            PartyNoticeConditionCard(
                title = "인증 실패 조건",
                conditions = partyFailureConditions,
                accentColor = Red,
                backgroundColor = Red2,
                iconRes = R.drawable.party_challenge_notice_failure
            )
            Button(
                onClick = ::dismiss,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Persimmon, contentColor = SourCream)
            ) {
                Text("확인", fontFamily = Pretendard, fontSize = 14.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(20.dp))
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
private fun PartyNoticeConditionCard(
    title: String,
    conditions: List<String>,
    accentColor: Color,
    backgroundColor: Color,
    iconRes: Int
) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(196.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(iconRes), null, Modifier.size(19.dp))
            Spacer(Modifier.width(6.dp))
            Text(title, color = accentColor, fontFamily = Pretendard, fontSize = 14.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
        }
        Column(Modifier.padding(top = 13.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            conditions.forEach { condition ->
                Text(condition, color = BlackBrown, fontFamily = Pretendard, fontSize = 12.sp, lineHeight = 20.sp)
            }
        }
    }
}

@Preview(name = "파티 인증 유의사항 바텀시트", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartyVerificationNoticeBottomSheetPreview() {
    OnulDo_FETheme {
        Box(Modifier.fillMaxWidth().height(844.dp).background(SourCream)) {
            PartyVerificationNoticeBottomSheet(
                challengeTitle = "새벽 6시 기상",
                onDismiss = {}
            )
        }
    }
}
