package com.example.onuldo_fe.ui.screen.challenge.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Pretendard

/**
 * 탐색(필터) 상태에서 검색창 아래 노출되는 카테고리 칩 목록.
 * 칩: 60x22, 라운드 11, 1px #5C2C0366(= DarkBrown40) 테두리.
 * 글씨: 8sp Medium, #1B130C, 가운데. (디자인은 Noto Sans KR = 시스템 기본 폰트)
 */
@Composable
fun ChallengeFilterChips(
    categories: List<String>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp)
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(4.dp) // 칩 간격 4
    ) {
        items(categories) { category ->
            CategoryChip(text = category, onClick = { onCategoryClick(category) })
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 60.dp, height = 22.dp)
            .clip(RoundedCornerShape(11.dp))
            .border(1.dp, DarkBrown40, RoundedCornerShape(11.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            // TODO(디자인 확정 후): 디자인 스펙은 Noto Sans KR 8sp Medium.
            //  현재 프로젝트엔 Noto Sans KR / Pretendard Medium(500) 폰트가 없어 임시로 Pretendard 적용.
            fontFamily = Pretendard,
            fontSize = 8.sp,
            lineHeight = 8.sp,
            fontWeight = FontWeight.Medium,
            color = BlackBrown,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 360)
@Composable
private fun ChallengeFilterChipsPreview() {
    OnulDo_FETheme {
        ChallengeFilterChips(
            categories = listOf("피트니스", "취미", "자기계발", "생활루틴", "식습관"),
            onCategoryClick = {}
        )
    }
}
