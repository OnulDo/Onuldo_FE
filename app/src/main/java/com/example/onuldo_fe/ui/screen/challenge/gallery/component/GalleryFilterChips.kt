package com.example.onuldo_fe.ui.screen.challenge.gallery.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.style.TextAlign
import com.example.onuldo_fe.ui.theme.Persimmon
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.OnulDoTypography

//카테고리 칩
@Composable
fun GalleryFilterChips(
    categories: List<String>,
    selectedCategory: String?, // 선택 시 변환
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp)
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        // 칩 사이 12dp 간격 (가로 스크롤은 LazyRow라 기본 지원)
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryChip(
                text = category,
                selected = category == selectedCategory,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    selected: Boolean, // 선택
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(22.dp)
            .widthIn(min = 69.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Persimmon else White)
            .border(
                1.dp,
                if (selected) Persimmon else DarkBrown40,
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        // 안 눌렀을 때 가운데, 선택(주황)일 때 왼쪽(10 → X → 8 → 텍스트)
        contentAlignment = if (selected) Alignment.CenterStart else Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)   // X ↔ 텍스트 gap 8
        ) {
            if (selected) {
                Image(
                    painter = painterResource(R.drawable.challenge_close),
                    contentDescription = null,
                    modifier = Modifier.size(10.dp)
                )
            }

            Text(
                text = text,
                style = OnulDoTypography.caption4Medium,
                color = if (selected) White else BlackBrown,   // #1B130C / 선택 시 흰색
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 360)
@Composable
private fun ChallengeFilterChipsPreview() {
    OnulDo_FETheme {
        GalleryFilterChips(
            categories = listOf("피트니스", "취미", "자기계발", "생활루틴", "식습관"),
            selectedCategory = "피트니스",
            onCategoryClick = {}
        )
    }
}