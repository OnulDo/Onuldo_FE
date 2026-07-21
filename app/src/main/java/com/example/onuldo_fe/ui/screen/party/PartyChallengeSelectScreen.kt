package com.example.onuldo_fe.ui.screen.party

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.*

private data class PartyChallengeGalleryItem(
    val challenge: PartyChallengeUi,
    val participantCount: String,
    val imageRes: Int
)

private val partyChallengeGalleryItems = listOf(
    PartyChallengeGalleryItem(PartyChallengeUi("challenge-1", "새벽 6시 기상", "4주", 10_000), "1,234명", R.drawable.party_challenge_morning),
    PartyChallengeGalleryItem(PartyChallengeUi("challenge-2", "30분 러닝", "4주", 10_000), "682명", R.drawable.party_challenge_running),
    PartyChallengeGalleryItem(PartyChallengeUi("challenge-3", "하루 독서 30분", "4주", 10_000), "920명", R.drawable.party_challenge_reading),
    PartyChallengeGalleryItem(PartyChallengeUi("challenge-4", "영양제 챙기기", "4주", 10_000), "1,532명", R.drawable.party_challenge_supplement),
    PartyChallengeGalleryItem(PartyChallengeUi("challenge-5", "영단어 30개", "4주", 10_000), "1,149명", R.drawable.party_challenge_words),
    PartyChallengeGalleryItem(PartyChallengeUi("challenge-6", "명상 10분", "4주", 10_000), "425명", R.drawable.party_challenge_meditation)
)

@Composable
fun PartyChallengeSelectScreen(
    challenges: List<PartyChallengeUi>,
    selectedId: String?,
    onSelect: (PartyChallengeUi) -> Unit,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val items = partyChallengeGalleryItems.filter { searchText.isBlank() || it.challenge.title.contains(searchText, ignoreCase = true) }

    Column(Modifier.fillMaxSize().background(SourCream).systemBarsPadding()) {
        Text("챌린지", Modifier.padding(start = 20.dp, top = 17.dp), color = BlackBrown, fontFamily = Pretendard, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("나에게 맞는 챌린지를 찾아보세요", Modifier.padding(start = 20.dp, top = 3.dp), color = DarkBrown50, fontFamily = Pretendard, fontSize = 12.sp)

        Row(Modifier.padding(start = 20.dp, end = 26.dp, top = 17.dp), verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = searchText,
                onValueChange = { searchText = it },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = BlackBrown, fontFamily = Pretendard, fontSize = 12.sp),
                cursorBrush = SolidColor(Persimmon),
                modifier = Modifier.weight(1f).height(27.dp),
                decorationBox = { inner ->
                    Row(Modifier.fillMaxSize().background(DarkBrown10, RoundedCornerShape(14.dp)).padding(horizontal = 11.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("⌕", color = DarkBrown50, fontSize = 15.sp)
                        Box(Modifier.weight(1f).padding(start = 8.dp)) {
                            if (searchText.isEmpty()) Text("생활루틴", color = DarkBrown70, fontFamily = Pretendard, fontSize = 12.sp)
                            inner()
                        }
                    }
                }
            )
            Box(Modifier.padding(start = 9.dp).size(27.dp).background(DarkBrown, CircleShape), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Box(Modifier.width(11.dp).height(1.dp).background(SourCream))
                    Box(Modifier.width(7.dp).height(1.dp).background(SourCream))
                    Box(Modifier.width(4.dp).height(1.dp).background(SourCream))
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f).padding(top = 21.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.challenge.id }) { item ->
                PartyChallengeGalleryCard(item, selected = item.challenge.id == selectedId) {
                    onSelect(item.challenge)
                    onConfirm()
                }
            }
        }
    }
}

@Composable
private fun PartyChallengeGalleryCard(item: PartyChallengeGalleryItem, selected: Boolean, onClick: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().height(180.dp).background(White, RoundedCornerShape(16.dp)).border(if (selected) 1.5.dp else 1.dp, if (selected) Persimmon else DarkBrown20, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick)
    ) {
        Image(painterResource(item.imageRes), null, Modifier.fillMaxWidth().height(130.dp), contentScale = ContentScale.Crop)
        Text(item.challenge.title, Modifier.padding(start = 12.dp, top = 7.dp), color = BlackBrown, fontFamily = Pretendard, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.padding(start = 12.dp, top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("♟", color = BlackBrown.copy(alpha = .6f), fontSize = 10.sp)
            Text(item.participantCount, Modifier.padding(start = 5.dp), color = BlackBrown.copy(alpha = .6f), fontFamily = Pretendard, fontSize = 10.sp)
        }
    }
}

@Preview(name = "파티 챌린지 탐색", showBackground = true, widthDp = 390, heightDp = 884)
@Composable private fun PartyChallengeSelectScreenPreview() { OnulDo_FETheme { PartyChallengeSelectScreen(samplePartyChallenges, null, {}, {}, {}) } }
