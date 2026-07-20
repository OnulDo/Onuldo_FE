package com.example.onuldo_fe.ui.screen.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.screen.home.model.CompletedChallengeType
import com.example.onuldo_fe.ui.screen.home.model.HomeCompletedChallenge
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.Green2
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.White

@Composable
fun HomeCompletedChallengeCard(
    completedChallenge: HomeCompletedChallenge,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .background(White, RoundedCornerShape(14.dp))
            .border(BorderStroke(1.dp, DarkBrown40), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = completedChallenge.time,
                color = DarkBrown50,
                fontSize = 10.sp,
                lineHeight = 12.sp
            )
            Text(
                text = completedChallenge.title,
                color = BlackBrown,
                fontSize = 14.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (completedChallenge.type == CompletedChallengeType.Party) {
            Box(
                modifier = Modifier
                    .height(24.dp)
                    .background(Green2, RoundedCornerShape(12.dp))
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(completedChallenge.resultText, color = Green, fontSize = 11.sp, lineHeight = 13.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Text(completedChallenge.resultText, color = Green, fontSize = 12.sp, lineHeight = 14.sp)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 390)
@Composable
private fun HomeCompletedChallengeCardPreview() {
    OnulDo_FETheme {
        HomeCompletedChallengeCard(
            HomeCompletedChallenge("06:30", "새벽 러너 파티", "3/3 인증", CompletedChallengeType.Party),
            Modifier.fillMaxWidth().padding(horizontal = 20.dp)
        )
    }
}
