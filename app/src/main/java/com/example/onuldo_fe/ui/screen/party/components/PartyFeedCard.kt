package com.example.onuldo_fe.ui.screen.party.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.home.HomeVerifyButton
import com.example.onuldo_fe.viewmodel.party.PartyFeedItemUi
import com.example.onuldo_fe.ui.theme.*

@Composable
fun PartyFeedCard(
    item: PartyFeedItemUi,
    modifier: Modifier = Modifier,
    isCurrentUser: Boolean = false,
    onVerifyClick: () -> Unit = {}
) {
    val shouldShowVerifyButton = isCurrentUser && item.verificationImageUrl == null

    Column(
        modifier
            .fillMaxWidth()
            .height(275.dp)
            .background(White, RoundedCornerShape(16.dp))
            .border(1.dp, DarkBrown10, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
    ) {
        Box(Modifier.fillMaxWidth().height(225.dp).background(Persimmon.copy(alpha = .15f)), contentAlignment = Alignment.Center) {
            if (item.verificationImageUrl != null || item.imageRes != null) {
                PartyNetworkImage(
                    imageUrl = item.verificationImageUrl,
                    fallbackImageRes = item.imageRes ?: R.drawable.party_feed_unverified_icon,
                    contentDescription = "${item.name} 인증 사진",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painterResource(R.drawable.party_feed_unverified_icon),
                    contentDescription = null,
                    modifier = Modifier.width(94.dp).height(108.dp),
                    contentScale = ContentScale.Fit,
                    alpha = .6f
                )
            }
        }
        Row(Modifier.fillMaxWidth().height(50.dp).padding(start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            PartyMemberProfileImage(
                profileImageUrl = item.profileImageUrl,
                defaultCharacterId = item.defaultCharacterId,
                contentDescription = "${item.name} 프로필",
                containerSize = 28.dp,
                characterWidth = 22.dp,
                characterHeight = 25.dp
            )
            Column(Modifier.padding(start = 8.dp)) {
                Text(item.name, color = BlackBrown, fontFamily = Pretendard, fontSize = 12.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
                Text(item.time, color = DarkBrown50, fontFamily = Pretendard, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }
            if (shouldShowVerifyButton) {
                Spacer(Modifier.weight(1f))
                HomeVerifyButton(
                    onClick = onVerifyClick,
                    width = 78.dp,
                    height = 26.dp,
                    iconSize = 12.dp,
                    fontSize = 10.sp,
                    lineHeight = 12.sp
                )
            }
        }
    }
}

@Preview(name = "파티 인증 피드 카드", showBackground = true)
@Composable
private fun PartyFeedCardPreview() {
    OnulDo_FETheme {
        Box(Modifier.width(169.dp).background(SourCream)) {
            PartyFeedCard(PartyFeedItemUi("민지", "2시간 전", imageRes = R.drawable.party_feed_minji))
        }
    }
}

@Preview(name = "파티 미인증 피드 카드", showBackground = true)
@Composable
private fun PartyFeedUnverifiedCardPreview() {
    OnulDo_FETheme {
        Box(Modifier.width(169.dp).background(SourCream)) {
            PartyFeedCard(PartyFeedItemUi("하늘", "미인증", memberId = "current-user"), isCurrentUser = true)
        }
    }
}
