package com.example.onuldo_fe.ui.component.party

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.theme.*

@Composable
fun PartyInviteCodeCard(inviteCode: String, onCopyClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(White, RoundedCornerShape(16.dp))
            .border(1.dp, DarkBrown40, RoundedCornerShape(16.dp))
            .padding(horizontal = 19.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                "초대코드",
                color = DarkBrown70,
                fontFamily = Pretendard,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                inviteCode,
                Modifier.padding(top = 3.dp),
                color = BlackBrown,
                fontFamily = Pretendard,
                fontSize = 26.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            Modifier.width(64.dp).height(36.dp).background(DarkBrown10, RoundedCornerShape(18.dp)).clickable(onClick = onCopyClick),
            contentAlignment = Alignment.Center
        ) {
            Text("복사", color = DarkBrown, fontFamily = Pretendard, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun PartyInviteCodeCardPreview() {
    OnulDo_FETheme {
        Box(
            Modifier
                .background(SourCream)
                .padding(20.dp)
        ) {
            PartyInviteCodeCard(
                inviteCode = "82K3H9",
                onCopyClick = {}
            )
        }
    }
}
