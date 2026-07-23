package com.example.onuldo_fe.ui.component.party

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.onuldo_fe.viewmodel.party.PartyChallengeUi
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.SourCream

@Composable
fun PartyChallengeSelector(
    challenge: PartyChallengeUi?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (challenge == null) {
        Box(
            modifier
                .fillMaxWidth()
                .height(64.dp)
                .partyDashedBorder(Persimmon)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text("＋ 챌린지 선택하기", color = Persimmon, fontFamily = Pretendard, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        return
    }

    Row(
        modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(White, RoundedCornerShape(14.dp))
            .border(1.5.dp, Persimmon20, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(56.dp).height(24.dp).background(Persimmon10, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Text(challenge.category, color = Persimmon, fontFamily = Pretendard, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Text(challenge.title, Modifier.weight(1f).padding(start = 8.dp), color = Persimmon, fontFamily = Pretendard, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {
            Text("변경", color = DarkBrown50, fontFamily = Pretendard, fontSize = 13.sp)
            Text("›", Modifier.padding(start = 7.dp), color = DarkBrown50, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private fun Modifier.partyDashedBorder(color: Color): Modifier = drawBehind {
    drawRoundRect(
        color = color.copy(alpha = 0.7f),
        style = Stroke(
            width = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx()))
        ),
        cornerRadius = CornerRadius(14.dp.toPx())
    )
}

@Preview(name = "파티 챌린지 선택 - 미선택", showBackground = true, widthDp = 390)
@Composable
private fun PartyChallengeSelectorEmptyPreview() {
    OnulDo_FETheme {
        Box(Modifier.background(SourCream).padding(20.dp)) {
            PartyChallengeSelector(challenge = null, onClick = {})
        }
    }
}

@Preview(name = "파티 챌린지 선택 - 선택", showBackground = true, widthDp = 390)
@Composable
private fun PartyChallengeSelectorSelectedPreview() {
    OnulDo_FETheme {
        Box(Modifier.background(SourCream).padding(20.dp)) {
            PartyChallengeSelector(
                challenge = PartyChallengeUi("preview", "30일 헬스 챌린지", "피트니스"),
                onClick = {}
            )
        }
    }
}
