package com.example.onuldo_fe.ui.screen.challenge

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.screen.challenge.component.ChallengeFilterButton
import com.example.onuldo_fe.ui.screen.challenge.component.ChallengeFilterChips
import com.example.onuldo_fe.ui.screen.challenge.component.ChallengeSearchBar
import com.example.onuldo_fe.ui.theme.DarkBrown10
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

/**
 * 챌린지 탐색 화면
 */
data class Challenge(
    val id: Int,
    val title: String,
    val participantCount: Int
)

// 더미 카테고리 —DB(서버 API) 연결하면 교체 예정
private val sampleCategories = listOf("피트니스", "취미", "자기계발", "생활루틴", "식습관")

@Composable
fun ChallengeScreen(
    challenges: List<Challenge> = sampleChallenges,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var filterSelected by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SourCream)
            .statusBarsPadding()
    ) {
        // ── 헤더 (제목 / 부제 / 검색 — 고정) ──
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(23.dp)) // 상단 여백(상태바 아래)
            Text(
                text = "챌린지",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(2.dp)) // 제목
            Text(
                text = "나에게 맞는 챌린지를 찾아보세요",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkBrown50
            )
            Spacer(Modifier.height(17.dp)) // 부제
            Row(verticalAlignment = Alignment.CenterVertically) {
                ChallengeSearchBar(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(9.dp)) // 검색창
                ChallengeFilterButton(
                    onClick = { filterSelected = !filterSelected }, // TODO: 필터 열기
                    selected = filterSelected
                )
            }
        }

        // ++ 필터 카테고리 칩 (필터 버튼 눌렀을 때만 노출) TODO: 추후 수정!!( 디자인 확정 x)
//        if (filterSelected) {
//            Spacer(Modifier.height(13.dp)) // 검색창
//            ChallengeFilterChips(
//                categories = sampleCategories,
//                onCategoryClick = { /* TODO: 카테고리 필터 적용 */ }
//            )
//            Spacer(Modifier.height(14.dp)) // 칩
//        } else {
//            Spacer(Modifier.height(21.dp)) // 검색창
//        }

        //임시
        Spacer(Modifier.height(21.dp)) // 검색창

        // 챌린지 카드 그리드 (스크롤)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(challenges, key = { it.id }) { challenge ->
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
    // 카드 전체
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(1.dp, DarkBrown20, RoundedCornerShape(16.dp))
    ) {
        // 사진 169 x 153 (top -23 크롭) → 카드 내 노출 169 x 130
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(169f / 130f)
                .background(DarkBrown10) // 이미지 로딩 전 임시 placeholder
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 10.dp)
        ) {
            // 챌린지 제목
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

            // 인원수
            Row(verticalAlignment = Alignment.CenterVertically) {
                PersonIcon(
                    color = Persimmon,
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

/**
 * TODO: 나중에 피그마 실제 아이콘(벡터 드로어블/에셋) 교체??? (이름이 다 백터라서 보류함(충돌날까바))
 */
@Composable
private fun PersonIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        // 머리
        drawCircle(
            color = color,
            radius = w * 0.23f,
            center = Offset(w * 0.5f, h * 0.27f)
        )
        // 어깨 (아래쪽 반원 돔)
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(w * 0.12f, h * 0.52f),
            size = Size(w * 0.76f, h * 0.9f)
        )
    }
}

// 더미 데이터 — 나중에 DB(서버 API) 연결하면 실제 챌린지 목록으로 교체 예정
internal val sampleChallenges = listOf(
    Challenge(id = 0, title = "새벽 6시 기상", participantCount = 1234),
    Challenge(id = 1, title = "30분 러닝", participantCount = 682),
    Challenge(id = 2, title = "하루 독서 30분", participantCount = 920),
    Challenge(id = 3, title = "영양제 챙기기", participantCount = 1532),
    Challenge(id = 4, title = "영단어 30개", participantCount = 1149),
    Challenge(id = 5, title = "명상 10분", participantCount = 425)
)

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun ChallengeScreenPreview() {
    OnulDo_FETheme {
        ChallengeScreen()
    }
}
