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
        horizontalArrangement = Arrangement.spacedBy(4.dp)
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
            .size(width = 69.dp, height = 22.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(if (selected) Persimmon else White)
            .border(
                1.dp,
                if (selected) Persimmon else DarkBrown40,
                RoundedCornerShape(11.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Image(
                    painter = painterResource(R.drawable.ic_challenge_close),
                    contentDescription = null,
                    modifier = Modifier.size(8.dp) // 피그마 크기에 맞게 조절
                )

                Spacer(Modifier.width(8.dp))
            }

            Text(
                text = text,
                fontFamily = Pretendard,
                fontSize = 8.sp,
                lineHeight = 8.sp,
                fontWeight = FontWeight.Medium,
                color = if (selected) White else BlackBrown,
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