package com.example.onuldo_fe.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.screen.home.data.repository.FakeHomeRepository
import com.example.onuldo_fe.ui.screen.home.model.ChallengeStatus
import com.example.onuldo_fe.ui.screen.home.model.HomeChallenge
import com.example.onuldo_fe.ui.screen.home.model.TodayChallenge
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown30
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.Green2
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Red
import com.example.onuldo_fe.ui.theme.Red2
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

@Composable
fun HomeChallengeScreen(
    todayChallenge: TodayChallenge,
    challenges: List<HomeChallenge>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SourCream)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        HomeHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        )

        TodayChallengeCard(
            todayChallenge = todayChallenge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        SectionTitle(
            text = "나의 챌린지",
            modifier = Modifier.padding(horizontal = 22.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier.padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            challenges.forEach { challenge ->
                ChallengeCard(
                    challenge = challenge,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun TodayChallengeCard(
    todayChallenge: TodayChallenge,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = Persimmon10,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                border = BorderStroke(1.dp, Persimmon.copy(alpha = 0.55f)),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 16.dp)
    ) {
        Text(
            text = todayChallenge.date,
            color = Persimmon,
            fontSize = 17.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(34.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = todayChallenge.title,
                color = Persimmon,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = todayChallenge.progressText,
                color = Persimmon,
                fontSize = 9.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(White, RoundedCornerShape(50))
        ) {
            Box(
                modifier = Modifier
                    .weight(todayChallenge.progress.coerceIn(0.001f, 1f))
                    .height(5.dp)
                    .background(Persimmon, RoundedCornerShape(50))
            )
            Spacer(
                modifier = Modifier.weight((1f - todayChallenge.progress).coerceIn(0.001f, 1f))
            )
        }
    }
}

@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(17.dp)
                .background(Persimmon, RoundedCornerShape(50))
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            color = BlackBrown,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 15.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun ChallengeCard(
    challenge: HomeChallenge,
    modifier: Modifier = Modifier
) {
    val actionColors = challenge.actionColors()

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
                    text = challenge.title,
                    color = BlackBrown,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = challenge.subtitle,
                    color = DarkBrown50,
                    fontSize = 9.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = challenge.dDay,
                color = DarkBrown,
                fontSize = 9.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = challenge.deadline,
                color = actionColors.text,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .background(actionColors.background, RoundedCornerShape(50))
                    .padding(horizontal = 18.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = challenge.actionText,
                    color = actionColors.text,
                    fontSize = 9.sp,
                    lineHeight = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private data class ChallengeActionColors(
    val background: Color,
    val text: Color
)

private fun HomeChallenge.actionColors(): ChallengeActionColors {
    return when (status) {
        ChallengeStatus.NeedCertification -> ChallengeActionColors(
            background = Persimmon10,
            text = Persimmon
        )

        ChallengeStatus.WaitingReview,
        ChallengeStatus.Failed -> ChallengeActionColors(
            background = Red2,
            text = Red
        )

        ChallengeStatus.Success -> ChallengeActionColors(
            background = Green2,
            text = Green
        )
    }
}

@Preview(
    name = "Home With Challenges",
    showBackground = true,
    showSystemUi = true,
    widthDp = 360,
    heightDp = 740
)
@Composable
private fun HomeChallengeScreenPreview() {
    val repository = FakeHomeRepository()
    val todayChallenge = repository.getTodayChallenge()

    OnulDo_FETheme {
        if (todayChallenge != null) {
            HomeChallengeScreen(
                todayChallenge = todayChallenge,
                challenges = repository.getChallenges()
            )
        }
    }
}
