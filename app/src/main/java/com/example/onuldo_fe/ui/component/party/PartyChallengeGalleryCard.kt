package com.example.onuldo_fe.ui.component.party

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import com.example.onuldo_fe.viewmodel.party.PartyChallengeCardUi
import com.example.onuldo_fe.viewmodel.party.PartyChallengeUi
import com.example.onuldo_fe.ui.theme.*

@Composable
fun PartyChallengeGalleryCard(
    item: PartyChallengeCardUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(White, RoundedCornerShape(16.dp))
            .border(1.dp, DarkBrown20, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        PartyNetworkImage(
            imageUrl = item.imageUrl,
            fallbackImageRes = item.fallbackImageRes,
            contentDescription = item.challenge.title,
            modifier = Modifier.fillMaxWidth().height(130.dp),
            contentScale = ContentScale.Crop
        )
        Text(item.challenge.title, Modifier.padding(start = 11.dp, top = 7.dp), color = BlackBrown, fontFamily = Pretendard, fontSize = 12.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.padding(start = 12.dp, top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.party_challenge_person), null, Modifier.size(12.dp), tint = Persimmon)
            Text("${"%,d".format(item.participantCount)}명", Modifier.padding(start = 4.dp), color = BlackBrown.copy(alpha = .6f), fontFamily = Pretendard, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PartyChallengeGalleryCardPreview() {
    OnulDo_FETheme {
        PartyChallengeGalleryCard(
            item = PartyChallengeCardUi(
                challenge = PartyChallengeUi("preview", "새벽 6시 기상", "생활루틴"),
                participantCount = 1_234,
                fallbackImageRes = R.drawable.party_challenge_morning
            ),
            onClick = {}
        )
    }
}
