package com.example.onuldo_fe.ui.screen.challenge.detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.screen.challenge.component.ChallengeNoticeBox
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White

//인증방법 창

@Composable
fun VerificationMethodCard(
    title: String,
    description: String,
    onShowNotice: () -> Unit,
    modifier: Modifier = Modifier
) {
    ChallengeNoticeBox(height = 348.dp, modifier = modifier) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(20.dp))

            Text(
                text = "인증 방법",
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 13.sp,
                color = BlackBrown
            )

            Spacer(Modifier.height(23.dp))

            Text(
                text = title,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                lineHeight = 15.sp,
                color = Persimmon
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = description,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                color = BlackBrown
            )

            Spacer(Modifier.height(9.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(White)
                    .border(1.5.dp, Persimmon20, RoundedCornerShape(14.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(15.dp))

                Image(
                    painter = painterResource(R.drawable.challenge_camera_icon),
                    contentDescription = null,
                    modifier = Modifier.size(width = 104.dp, height = 116.dp)
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = "예시 사진을 추가해주세요",
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    lineHeight = 11.sp,
                    color = DarkBrown50,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(White)
                    .border(1.5.dp, Persimmon20, RoundedCornerShape(14.dp))
                    .clickable(onClick = onShowNotice)
            ) {
                Spacer(Modifier.height(15.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_challenge_info),
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
            }
        }
    }
}
