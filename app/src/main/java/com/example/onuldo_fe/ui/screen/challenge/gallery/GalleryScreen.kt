package com.example.onuldo_fe.ui.screen.challenge.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.screen.challenge.gallery.component.GalleryFilterButton
import com.example.onuldo_fe.ui.screen.challenge.gallery.component.GalleryFilterChips
import com.example.onuldo_fe.ui.screen.challenge.gallery.component.GallerySearchBar
import com.example.onuldo_fe.ui.theme.DarkBrown10
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

//챌린지 탐색 화면
data class Challenge(
    val id: Int,
    val title: String,
    val participantCount: Int
)

// 더미 데이터 — API 연동 시 교체
private val sampleCategories = listOf("피트니스", "취미", "자기계발", "생활루틴", "식습관")

private val sampleChallenges = listOf(
    Challenge(id = 0, title = "새벽 6시 기상", participantCount = 1234),
    Challenge(id = 1, title = "30분 러닝", participantCount = 682),
    Challenge(id = 2, title = "하루 독서 30분", participantCount = 920),
    Challenge(id = 3, title = "영양제 챙기기", participantCount = 1532),
    Challenge(id = 4, title = "영단어 30개", participantCount = 1149),
    Challenge(id = 5, title = "명상 10분", participantCount = 425)
)

@Composable
fun GalleryScreen(
    challenges: List<Challenge> = sampleChallenges,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var filterSelected by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // 검색어로 필터 (제목 매칭). 빈 검색어면 전체.
    val visibleChallenges = challenges.filter {
        it.title.contains(query.trim(), ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SourCream)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(23.dp))
            Text(
                text = "챌린지",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "나에게 맞는 챌린지를 찾아보세요",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkBrown50
            )
            Spacer(Modifier.height(17.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                GallerySearchBar(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(9.dp))
                GalleryFilterButton(
                    onClick = { filterSelected = !filterSelected }, // TODO: 필터 열기
                    selected = filterSelected
                )
            }
        }

        // 필터 카테고리 칩 (필터 버튼 눌렀을 때만 노출)
        if (filterSelected) {
            Spacer(Modifier.height(13.dp))
            GalleryFilterChips(
                categories = sampleCategories,
                selectedCategory = selectedCategory,
                onCategoryClick = { category ->
                    // 같은 칩 다시 누르면 해제, 다른 칩 누르면 그 카테고리 선택
                    selectedCategory = if (selectedCategory == category) null else category
                    // TODO: 선택된 카테고리로 목록 필터링 (API 연동 시)
                }
            )
            Spacer(Modifier.height(14.dp))
        } else {
            Spacer(Modifier.height(21.dp))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(visibleChallenges, key = { it.id }) { challenge ->
                ChallengeCard(challenge = challenge)
            }
        }
    }
}

@Composable
private fun ChallengeCard(
    challenge: Challenge,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(1.dp, DarkBrown20, RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(169f / 130f)
                .background(DarkBrown10)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 10.dp)
        ) {
            Text(
                text = challenge.title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    lineHeight = 13.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.challenge_person),
                    contentDescription = null,
                    tint = Persimmon,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "%,d명".format(challenge.participantCount),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        lineHeight = 10.sp
                    ),
                    color = DarkBrown50,
                    maxLines = 1
                )
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun GalleryScreenPreview() {
    OnulDo_FETheme {
        GalleryScreen()
    }
}
