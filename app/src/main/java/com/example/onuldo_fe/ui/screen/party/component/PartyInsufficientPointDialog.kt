package com.example.onuldo_fe.ui.screen.party.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.onuldo_fe.ui.theme.*

@Composable
fun PartyInsufficientPointDialog(
    ownedPoint: Int,
    requiredPoint: Int,
    onDismiss: () -> Unit,
    onChargeClick: () -> Unit
) {
    val shortage = (requiredPoint - ownedPoint).coerceAtLeast(0)

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            Modifier.width(350.dp).height(380.dp).clip(RoundedCornerShape(20.dp)).background(SourCream),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(34.dp))
            Box(Modifier.size(100.dp).clip(CircleShape).background(Red3), contentAlignment = Alignment.Center) {
                Image(painterResource(R.drawable.challenge_sad_icon), null, Modifier.size(width = 58.dp, height = 76.dp))
            }
            Spacer(Modifier.height(14.dp))
            Text("포인트가 부족해요", Modifier.fillMaxWidth(), color = BlackBrown, fontFamily = Pretendard, fontSize = 20.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text("챌린지 시작을 위해 도전금이 필요해요", Modifier.fillMaxWidth(), color = DarkBrown, fontFamily = Pretendard, fontSize = 13.sp, lineHeight = 13.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))
            Row(
                Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(78.dp).background(Persimmon10, RoundedCornerShape(14.dp)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PartyPointColumn("보유 포인트", "%,dP".format(ownedPoint), BlackBrown)
                PartyPointOperator("−", 28.dp)
                PartyPointColumn("필요 포인트", "%,dP".format(requiredPoint), BlackBrown)
                PartyPointOperator("=", 35.dp)
                PartyPointColumn("부족분", "%,dP".format(shortage), Persimmon)
            }
            Spacer(Modifier.height(19.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PartyDialogButton("취소", onDismiss, Modifier.width(144.dp), filled = false)
                PartyDialogButton("포인트 충전", onChargeClick, Modifier.width(158.dp), filled = true)
            }
        }
    }
}

@Composable
private fun PartyPointColumn(label: String, value: String, valueColor: Color) {
    Column(Modifier.width(80.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = DarkBrown70, fontFamily = Pretendard, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        Text(value, color = valueColor, fontFamily = Pretendard, fontSize = 14.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PartyPointOperator(symbol: String, width: Dp) {
    Box(Modifier.width(width).padding(top = 20.dp), contentAlignment = Alignment.Center) {
        Text(symbol, color = DarkBrown70, fontFamily = Pretendard, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PartyDialogButton(text: String, onClick: () -> Unit, modifier: Modifier, filled: Boolean) {
    Box(
        modifier.height(48.dp)
            .background(if (filled) Persimmon else White, RoundedCornerShape(14.dp))
            .then(if (filled) Modifier else Modifier.border(1.5.dp, DarkBrown40, RoundedCornerShape(14.dp)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (filled) SourCream else DarkBrown, fontFamily = Pretendard, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartyInsufficientPointDialogPreview() {
    OnulDo_FETheme { PartyInsufficientPointDialog(5_000, 10_000, {}, {}) }
}
