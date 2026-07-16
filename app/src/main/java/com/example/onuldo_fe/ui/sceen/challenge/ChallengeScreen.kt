package com.example.onuldo_fe.ui.sceen.challenge

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.onuldo_fe.ui.theme.DarkBrown10
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

/**
 * 챌린지 탐색 화면의 가운데 스크롤 영역 (챌린지 카드 2열 그리드).
 * 상단 검색바/제목, 하단 네비게이션바는 아직 제외.
 */
data class Challenge(
    val id: Int,
    val title: String,
    val participantCount: Int
)

@Composable
fun ChallengeScreen(
    challenges: List<Challenge> = sampleChallenges,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .background(SourCream),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(challenges, key = { it.id }) { challenge ->
            ChallengeCard(challenge = challenge)
        }
    }
}

@Composable
private fun ChallengeCard(
    challenge: Challenge,
    modifier: Modifier = Modifier
) {
    // 카드 전체: 169 x 180, 배경 #FFFFFF, 테두리 1px #5C2C0333, 라운드 16
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(1.dp, DarkBrown20, RoundedCornerShape(16.dp))
    ) {
        // 사진 169 x 153 (top -23 크롭) → 카드 내 노출 169 x 130. Glide 연결 시 ContentScale.Crop
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
            // 챌린지 제목: Pretendard Bold 13sp, line-height 100%
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

            // 인원수: Pretendard Medium 10sp, line-height 100%
            Row(verticalAlignment = Alignment.CenterVertically) {
                PersonIcon(
                    color = DarkBrown50,
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
 * 사람 실루엣 아이콘 (머리 + 어깨) — Canvas로 임시로 그린 근사 아이콘.
 * TODO: 나중에 피그마 실제 아이콘(벡터 드로어블/에셋)으로 교체 예정.
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
