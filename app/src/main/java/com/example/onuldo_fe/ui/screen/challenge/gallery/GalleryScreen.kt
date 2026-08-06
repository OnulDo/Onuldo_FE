package com.example.onuldo_fe.ui.screen.challenge.gallery

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.onuldo_fe.R
import com.example.onuldo_fe.model.challenge.ChallengeCategory
import com.example.onuldo_fe.viewmodel.challenge.ChallengeListUiState
import com.example.onuldo_fe.ui.screen.challenge.gallery.component.GalleryFilterButton
import com.example.onuldo_fe.ui.screen.challenge.gallery.component.GalleryFilterChips
import com.example.onuldo_fe.ui.screen.challenge.gallery.component.GallerySearchBar
import com.example.onuldo_fe.ui.theme.BlackBrown70
import com.example.onuldo_fe.ui.theme.DarkBrown10
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

//챌린지 탐색 화면
data class Challenge(
    val id: Long,
    val title: String,
    val participantCount: Int,
    val category: ChallengeCategory = ChallengeCategory.LIFESTYLE_ROUTINE,
    val imageUrl: String? = null,   // 서버 captionImgUrl (Coil 로딩) — null이면 imageRes 폴백
    @DrawableRes val imageRes: Int = R.drawable.challenge_sample_1
)

// 상태 없는 순수 UI — 데이터/이벤트는 GalleryRoute에서 주입한다. (파티 생성 흐름에서도 재사용)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    uiState: ChallengeListUiState,
    onQueryChange: (String) -> Unit,
    onCategorySelected: (ChallengeCategory?) -> Unit,
    onChallengeClick: (Challenge) -> Unit = {},
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val spacing = LocalSpacing.current
    val pullToRefreshState = rememberPullToRefreshState()
    // 화면 재생성 후에도 선택된 카테고리가 있으면 필터 칩을 펼쳐 활성 필터가 보이게(코드 래빗)
    var filterSelected by remember(uiState.selectedCategory) {
        mutableStateOf(uiState.selectedCategory != null)
    }

    // 카테고리 칩은 ChallengeCategory
    val categories = remember { ChallengeCategory.entries.map { it.displayName } }

    // 화면 전체를 감싸 당김 인디케이터가 헤더 위(화면 맨 위)에 뜨게 한다.
    // 위에서 당길 때만 노출(pull-to-refresh)되고, 화면 복귀 자동 갱신(silentRefresh)은 표시되지 않는다.
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        modifier = modifier
            .fillMaxSize()
            .background(SourCream)
            .statusBarsPadding(),
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = pullToRefreshState,
                isRefreshing = uiState.isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                color = Persimmon
            )
        }
    ) {
      Column(
        modifier = Modifier
            .fillMaxSize()
            // 빈 곳 터치 시 검색창 포커스·키보드 해제
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
    ) {
        // 고정 헤더: 제목·서브카피·검색창·필터칩
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "챌린지",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "나에게 맞는 챌린지를 찾아보세요",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                color = BlackBrown70
            )
            Spacer(Modifier.height(spacing.spacing12))
            Row(verticalAlignment = Alignment.CenterVertically) {
                GallerySearchBar(
                    value = uiState.query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(9.dp))
                GalleryFilterButton(
                    onClick = {
                        filterSelected = !filterSelected
                        // 칩을 닫으면 선택 카테고리도 해제 → 근거 없는 필터 유지 방지
                        if (!filterSelected) onCategorySelected(null)
                    },
                    selected = filterSelected
                )
            }
            // 필터 카테고리 칩 (필터 버튼 눌렀을 때만 노출)
            if (filterSelected) {
                Spacer(Modifier.height(spacing.spacing12))
                GalleryFilterChips(
                    categories = categories,
                    selectedCategory = uiState.selectedCategory?.displayName,
                    onCategoryClick = { name ->
                        //라벨 매칭 (enum필터 <-> 한글)
                        val category = ChallengeCategory.entries.first { it.displayName == name }
                        // 같은 칩 재선택이면 해제(null), 아니면 선택
                        val next = if (uiState.selectedCategory == category) null else category
                        onCategorySelected(next)
                    },
                    contentPadding = PaddingValues(horizontal = 0.dp)
                )
            }
        }

        Spacer(Modifier.height(spacing.spacing12))

        // 카드 목록 — 로딩 중엔 기본 인디케이터만 표시(상세 로딩/빈 상태 UI는 추후)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = Persimmon,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // 실패/결과없음은 별도 문구 없이 빈 화면. 실패는 위 토스트로만 안내.
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(spacing.spacing12)
                ) {
                    items(uiState.challenges, key = { it.id }) { challenge ->
                        ChallengeCard(
                            challenge = challenge,
                            onClick = { onChallengeClick(challenge) }
                        )
                    }
                }
            }
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
        // 서버 이미지(URL) 우선, 없으면 로컬 drawable 폴백 (Coil은 String URL/Int res 둘 다 model로 받음)
        AsyncImage(
            model = challenge.imageUrl ?: challenge.imageRes,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)            // 디자인 스펙: 이미지 높이 130
                .background(DarkBrown10)   // 로딩/여백 대비 회색 배경 유지
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(
                    start = spacing.spacing12,
                    end = spacing.spacing10,
                    top = spacing.spacing12,
                    bottom = spacing.spacing10
                )
        ) {
            Text(
                text = challenge.title,
                style = MaterialTheme.typography.bodySmall,  // Body4 (12sp Bold)
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.challenge_person),
                    contentDescription = null,
                    tint = Persimmon,
                    modifier = Modifier.size(10.dp)
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
