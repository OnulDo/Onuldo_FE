package com.example.onuldo_fe.ui.screen.challenge.gallery

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.repository.challenge.ChallengeRepositoryProvider
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
import com.example.onuldo_fe.model.challenge.ChallengeCategory

//챌린지 탐색 화면
data class Challenge(
    val id: Int,
    val title: String,
    val participantCount: Int,
    val category: ChallengeCategory = ChallengeCategory.DAILY_ROUTINE,
    @DrawableRes val imageRes: Int = R.drawable.challenge_sample_1
)

// 카테고리 더미 — API 연동 시 교체
private val sampleCategories = listOf("피트니스", "취미", "자기계발", "생활루틴", "식습관")

@Composable
fun GalleryScreen(
    // 챌린지 목록은 레포지토리에서 제공 (더미 → API 명세 확정 후 교체)
    challenges: List<Challenge> = ChallengeRepositoryProvider.provide().getChallenges(),
    onChallengeClick: (Challenge) -> Unit = {},
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
        // 헤더(제목·검색창)와 필터칩을 그리드 안 전체폭 아이템으로 넣어
        // 스크롤 시 카드와 함께 위로 올라가도록 함(고정 X).
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 제목 + 검색창 (스크롤됨)
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
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
                    // 필터가 닫혀 있으면 검색창과 카드 사이 여백만 보정(21 - 20(그리드 간격) = 1)
                    if (!filterSelected) Spacer(Modifier.height(1.dp))
                }
            }

            // 필터 카테고리 칩 (필터 버튼 눌렀을 때만 노출, 함께 스크롤)
            if (filterSelected) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    // 그리드가 이미 좌우 20 inset을 주므로 칩 자체 패딩은 0
                    GalleryFilterChips(
                        categories = sampleCategories,
                        selectedCategory = selectedCategory,
                        onCategoryClick = { category ->
                            // 같은 칩 다시 누르면 해제, 다른 칩 누르면 그 카테고리 선택
                            selectedCategory = if (selectedCategory == category) null else category
                            // TODO: 선택된 카테고리로 목록 필터링 (API 연동 시)
                        },
                        contentPadding = PaddingValues(horizontal = 0.dp)
                    )
                }
            }

            items(visibleChallenges, key = { it.id }) { challenge ->
                ChallengeCard(
                    challenge = challenge,
                    onClick = { onChallengeClick(challenge) }
                )
            }
        }
    }
}

@Composable
private fun ChallengeCard(
    challenge: Challenge,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(1.dp, DarkBrown20, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(challenge.imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(169f / 130f)
                .background(DarkBrown10)   // 로딩/여백 대비 회색 배경 유지
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
