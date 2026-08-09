package com.example.onuldo_fe.ui.screen.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.model.home.ChallengeStatus
import com.example.onuldo_fe.model.home.HomePartyChallenge
import com.example.onuldo_fe.model.home.HomePartyMember
import com.example.onuldo_fe.ui.screen.party.components.PartyMemberProfileImage
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown10
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown80
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.Green2
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon80
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.Red
import com.example.onuldo_fe.ui.theme.Red2
import com.example.onuldo_fe.ui.theme.White
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HomePartyCard(
    partyChallenge: HomePartyChallenge,
    modifier: Modifier = Modifier,
    onVerifyClick: () -> Unit = {}
) {
    val spacing = LocalSpacing.current
    // TODO: 13sp Medium·11sp Bold 글자 스타일과 2·5·6·7·14·17dp 여백 토큰 추가 후 교체

    Column(
        modifier = modifier
            .height(140.dp)
            .background(White, RoundedCornerShape(14.dp))
            .border(BorderStroke(1.dp, DarkBrown40), RoundedCornerShape(14.dp))
            .padding(start = 14.dp, top = 14.dp, end = spacing.spacing10, bottom = 17.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = partyChallenge.title,
                    color = BlackBrown,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(7.dp))
                Text(
                    text = partyChallenge.subtitle,
                    color = DarkBrown50,
                    fontFamily = Pretendard,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
            Text(
                text = stringResource(R.string.home_challenge_d_day, partyChallenge.remainingDays),
                color = DarkBrown80,
                modifier = Modifier
                    .padding(end = 6.dp)
                    .offset(y = (-3).dp),
                fontFamily = Pretendard,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(spacing.spacing10))
        Row(verticalAlignment = Alignment.CenterVertically) {
            val deadlineText = partyChallenge.verifiedAt?.let {
                stringResource(R.string.home_challenge_verified_at, it.toDisplayText())
            } ?: partyChallenge.deadlineAt?.let {
                stringResource(R.string.home_challenge_deadline, it.toDisplayText())
            } ?: stringResource(R.string.home_challenge_deadline_unknown)
            Text(
                text = deadlineText,
                color = partyChallenge.status.statusColor(),
                fontFamily = Pretendard,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )
            partyChallenge.remainingMinutes
                ?.takeIf { partyChallenge.canVerify && it >= 0 }
                ?.let { remainingMinutes ->
                Spacer(Modifier.width(spacing.spacing10))
                HomeRemainingTimeChip(remainingMinutes = remainingMinutes)
                }
        }

        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                val members = partyChallenge.members.ifEmpty {
                    // 실제 API 연결 전에도 현재 인원 수와 인증 상태를 확인할 수 있도록 임시 멤버 구성
                    List(partyChallenge.totalMemberCount) { index ->
                        HomePartyMember(
                            memberId = "preview-member-$index",
                            profileImageUrl = null,
                            defaultCharacterId = (index % 9) + 1,
                            isVerifiedToday = index < partyChallenge.completedMemberCount
                        )
                    }
                }
                members.forEach { member ->
                    PartyMemberProfileImage(
                        profileImageUrl = member.profileImageUrl,
                        defaultCharacterId = member.defaultCharacterId,
                        contentDescription = null,
                        containerSize = 33.dp,
                        characterWidth = 28.dp,
                        characterHeight = 33.dp,
                        showBorder = member.isVerifiedToday,
                        dimmed = !member.isVerifiedToday
                    )
                }
            }
            PartyAction(partyChallenge, onVerifyClick)
        }
    }
}

@Composable
private fun PartyAction(
    party: HomePartyChallenge,
    onVerifyClick: () -> Unit
) {
    if (party.status == ChallengeStatus.NeedCertification) {
        HomeVerifyButton(
            onClick = onVerifyClick,
            enabled = party.canVerify,
            width = 96.dp,
            height = 32.dp,
            iconSize = 14.dp,
            fontSize = 12.sp,
            lineHeight = 22.sp
        )
        return
    }

    if (party.status != ChallengeStatus.NeedCertification) {
        val (background, textColor) = when (party.status) {
            ChallengeStatus.Success -> Green2 to Green
            ChallengeStatus.WaitingReview -> DarkBrown10 to DarkBrown
            ChallengeStatus.Failed -> Red2 to Red
            ChallengeStatus.NeedCertification -> Color.Transparent to Persimmon
        }
        Box(
            Modifier.size(width = 78.dp, height = 26.dp).background(background, RoundedCornerShape(13.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(party.status.actionTextRes()),
                color = textColor,
                fontFamily = Pretendard,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun ChallengeStatus.statusColor() = when (this) {
    ChallengeStatus.Success -> Green
    ChallengeStatus.WaitingReview, ChallengeStatus.Failed -> Red
    ChallengeStatus.NeedCertification -> Persimmon80
}

private fun ChallengeStatus.actionTextRes(): Int = when (this) {
    ChallengeStatus.NeedCertification -> R.string.home_challenge_action_verify
    ChallengeStatus.WaitingReview -> R.string.home_challenge_action_waiting_review
    ChallengeStatus.Failed -> R.string.home_challenge_action_failed
    ChallengeStatus.Success -> R.string.home_challenge_action_success
}

private val homeTimeFormatter = DateTimeFormatter.ofPattern("H:mm")

private fun LocalTime.toDisplayText(): String = format(homeTimeFormatter)

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 390)
@Composable
private fun HomePartyCardPreview() {
    OnulDo_FETheme {
        val spacing = LocalSpacing.current
        HomePartyCard(
            HomePartyChallenge("새벽 러너 파티", "30분 러닝", 12, LocalTime.of(7, 0), 2, 5, remainingMinutes = 45),
            Modifier.fillMaxWidth().padding(horizontal = spacing.spacing20)
        )
    }
}
