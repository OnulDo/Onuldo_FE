package com.example.onuldo_fe.ui.screen.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.screen.home.model.HomePartyChallenge
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown30
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

@Composable
fun HomePartyCard(
    partyChallenge: HomePartyChallenge,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(White, RoundedCornerShape(10.dp))
            .border(
                border = BorderStroke(1.dp, DarkBrown30),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = partyChallenge.title,
                    color = BlackBrown,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = partyChallenge.subtitle,
                    color = DarkBrown50,
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            Text(
                text = partyChallenge.dDay,
                color = DarkBrown,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = partyChallenge.deadline,
                color = Persimmon,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Bold
            )
            if (partyChallenge.timeLeft.isNotBlank()) {
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .background(Persimmon10, RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = partyChallenge.timeLeft,
                        color = Persimmon,
                        fontSize = 12.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PartyMemberIcons(
                completedCount = partyChallenge.completedMemberCount,
                totalCount = partyChallenge.totalMemberCount
            )
            CertificationButton()
        }
    }
}

@Composable
private fun PartyMemberIcons(
    completedCount: Int,
    totalCount: Int
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(totalCount) { index ->
            val icon = if (index < completedCount) {
                R.drawable.home_run_light_icon
            } else {
                R.drawable.home_run_dark_icon
            }
            val border = if (index < completedCount) {
                BorderStroke(width = 1.2.dp, color = Persimmon)
            } else {
                BorderStroke(width = 0.dp, color = Persimmon10)
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Persimmon10)
                    .border(border = border, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
private fun CertificationButton() {
    Row(
        modifier = Modifier
            .border(BorderStroke(1.dp, Persimmon), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.home_camera_icon),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "인증하기",
            color = Persimmon,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 360)
@Composable
private fun HomePartyCardPreview() {
    OnulDo_FETheme {
        HomePartyCard(
            partyChallenge = HomePartyChallenge(
                title = "새벽 러닝 파티",
                subtitle = "30분 러닝",
                dDay = "D-12",
                deadline = "7:00 마감",
                timeLeft = "1시간 30분 남음",
                completedMemberCount = 2,
                totalMemberCount = 5
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(SourCream)
                .padding(22.dp)
        )
    }
}
