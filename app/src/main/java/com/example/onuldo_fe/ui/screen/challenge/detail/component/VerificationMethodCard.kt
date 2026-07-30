package com.example.onuldo_fe.ui.screen.challenge.detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White

// 인증 방법 카드 — 챌린지 상세 하단 (파티 상세 디자인 반영)
@Composable
fun VerificationMethodCard(
    title: String,
    description: String,
    onShowNotice: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Persimmon10)
            .border(1.dp, Persimmon20, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "인증 방법",
            style = MaterialTheme.typography.bodyMedium,  // Body3
            color = BlackBrown
        )

        Text(
            text = title,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyMedium,  // Body3
            color = Persimmon
        )

        Text(
            text = description,
            modifier = Modifier.padding(top = 2.dp),
            style = MaterialTheme.typography.labelLarge,  // Caption1
            color = BlackBrown
        )

        // 인증 예시 사진
        Image(
            painter = painterResource(R.drawable.challenge_detail_verification),
            contentDescription = "인증 예시 사진",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
                .size(width = 195.dp, height = 315.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.5.dp, Persimmon20, RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        // 인증 유의사항 보기 (기존 코드 그대로)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
                .height(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(White)
                .border(1.5.dp, Persimmon20, RoundedCornerShape(14.dp))
                .clickable(onClick = onShowNotice)
        ) {
            Spacer(Modifier.height(15.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // 정보 아이콘 + 텍스트 (가운데)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.challenge_info),
                        contentDescription = null,
                        tint = Persimmon,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(7.dp))
                    Text(
                        text = "인증 유의사항 보기",
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        lineHeight = 13.sp,
                        color = BlackBrown
                    )
                }
                // 화살표 — 오른쪽 끝에서 22
                Image(
                    painter = painterResource(R.drawable.challenge_arrow_right),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 22.dp)
                        .size(width = 5.dp, height = 11.dp)
                )
            }
        }
    }
}
