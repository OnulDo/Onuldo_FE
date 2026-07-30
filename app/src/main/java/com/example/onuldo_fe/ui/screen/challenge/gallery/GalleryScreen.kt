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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.repository.challenge.ChallengeRepositoryProvider
import com.example.onuldo_fe.ui.screen.challenge.gallery.component.GalleryFilterButton
import com.example.onuldo_fe.ui.screen.challenge.gallery.component.GalleryFilterChips
import com.example.onuldo_fe.ui.screen.challenge.gallery.component.GallerySearchBar
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.BlackBrown70
import com.example.onuldo_fe.ui.theme.DarkBrown10
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.LocalSpacing
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
    val focusManager = LocalFocusManager.current
    val spacing = LocalSpacing.current
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
            // 빈 곳 터치 시 검색창 포커스·키보드 해제
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
    ) {
        // 고정 헤더: 제목·서브카피·검색창·필터칩 (스크롤 X)
        // 좌우 20 inset은 아래 그리드 contentPadding과 맞춰 카드와 정렬시킴
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "챌린지",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "나에게 맞는 챌린지를 찾아보세요",
                style = MaterialTheme.typography.labelLarge.copy(   // 13sp
                    fontWeight = FontWeight.Medium
                ),
                color = BlackBrown70
            )
            // 서브카피 → 검색: 간격 12
            Spacer(Modifier.height(spacing.spacing12))
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
            // 필터 카테고리 칩 (필터 버튼 눌렀을 때만 노출)
            if (filterSelected) {
                Spacer(Modifier.height(spacing.spacing12))
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

        // 헤더 → 그리드 간격 12 (보정 없이 자연스럽게)
        Spacer(Modifier.height(spacing.spacing12))

        // 카드 목록만 스크롤 (무한 스크롤 대상). 카드끼리 세로 간격 12로 수정
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(spacing.spacing12)
        ) {
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
    val spacing = LocalSpacing.current
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
                .height(130.dp)            // 디자인 스펙: 이미지 높이 130 (폭은 유동, Crop)
                .background(DarkBrown10)   // 로딩/여백 대비 회색 배경 유지
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)   // 디자인 스펙: 글씨 영역 50 (이미지 130 + 50 = 카드 180)
                .padding(start = spacing.spacing12, end = spacing.spacing10),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = challenge.title,
                style = MaterialTheme.typography.bodySmall,  // Body4 (12sp Bold)
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis  // 카드 폭 넘치면 … 처리
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.challenge_person),
                    contentDescription = null,
                    tint = Persimmon,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "%,d명".format(challenge.participantCount),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        lineHeight = 10.sp
                    ),
                    color = BlackBrown70,
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
