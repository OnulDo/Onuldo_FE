package com.example.onuldo_fe.ui.screen.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.White

@Composable
fun SettlementCompleteCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(64.dp)
            .background(White, RoundedCornerShape(14.dp))
            .border(BorderStroke(1.dp, DarkBrown40), RoundedCornerShape(14.dp))
            .padding(horizontal = 15.dp, vertical = 12.dp)
    ) {
        Text("정산이 완료됐어요", color = BlackBrown, fontSize = 15.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("새벽 러너 파티", color = DarkBrown50, fontSize = 12.sp, lineHeight = 14.sp)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 390)
@Composable
private fun SettlementCompleteCardPreview() {
    OnulDo_FETheme { SettlementCompleteCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) }
}
