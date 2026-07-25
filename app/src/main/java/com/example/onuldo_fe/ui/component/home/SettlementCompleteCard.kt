package com.example.onuldo_fe.ui.component.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown80
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.White

@Composable
fun SettlementCompleteCard(
    partyName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(64.dp)
            .background(White, RoundedCornerShape(14.dp))
            .border(BorderStroke(1.5.dp, Persimmon), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.home_congratulation_icon),
            contentDescription = null,
            modifier = Modifier.size(width = 22.dp, height = 22.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(stringResource(R.string.home_settlement_complete_title), color = BlackBrown, fontSize = 15.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        Text(partyName, color = DarkBrown80, fontSize = 12.sp, lineHeight = 14.sp)
        Spacer(Modifier.width(12.dp))
        Image(
            painter = painterResource(R.drawable.home_arrow_right),
            contentDescription = null,
            modifier = Modifier.size(width = 6.dp, height = 11.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 390)
@Composable
private fun SettlementCompleteCardPreview() {
    OnulDo_FETheme {
        SettlementCompleteCard("새벽 러너 파티", {}, Modifier.fillMaxWidth().padding(horizontal = 20.dp))
    }
}
