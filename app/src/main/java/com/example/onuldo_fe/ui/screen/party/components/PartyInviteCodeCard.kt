package com.example.onuldo_fe.ui.screen.party.components

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
            .padding(horizontal = LocalSpacing.current.spacing20),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                "초대코드",
                color = DarkBrown70,
                style = OnulDoTypography.caption2Medium
            )
            Text(
                inviteCode,
                // TODO 디자인 시스템에 3dp 토큰이 추가되면 LocalSpacing으로 교체
                Modifier.padding(top = 3.dp),
                color = BlackBrown,
                style = OnulDoTypography.headline2Bold
            )
        }
        Box(
            Modifier.width(64.dp).height(36.dp).background(DarkBrown10, RoundedCornerShape(18.dp)).clickable(onClick = onCopyClick),
            contentAlignment = Alignment.Center
        ) {
            Text("복사", color = DarkBrown, style = OnulDoTypography.caption2Medium)
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
