package com.example.onuldo_fe.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

/** 프로필에서 선택 가능한 기본 캐릭터 목록 (Figma 캐릭터 그리드 순서). */
val ProfileCharacters = listOf(
    R.drawable.ic_char_01,
    R.drawable.ic_char_02,
    R.drawable.ic_char_03,
    R.drawable.ic_char_04,
    R.drawable.ic_char_05,
    R.drawable.ic_char_06,
    R.drawable.ic_char_07,
    R.drawable.ic_char_08,
    R.drawable.ic_char_09,
)

/**
 * 프로필 사진(캐릭터) 선택 바텀시트 — Ready for Dev node `5154:4894`.
 * 3×3 원형 캐릭터 그리드, 선택 항목은 오렌지 링으로 강조. 하단 "닫기"로 확정/닫기.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterPickerSheet(
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SourCream,
    ) {
        Text(
            text = "프로필 사진 선택",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = BlackBrown,
            modifier = Modifier.padding(start = 24.dp),
        )
        Spacer(Modifier.height(24.dp))

        // 3×3 원형 그리드.
        ProfileCharacters.chunked(3).forEachIndexed { rowIndex, rowItems ->
            if (rowIndex > 0) Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            ) {
                rowItems.forEachIndexed { colIndex, drawable ->
                    val index = rowIndex * 3 + colIndex
                    CharacterCell(
                        drawable = drawable,
                        selected = index == selectedIndex,
                        onClick = { onSelect(index) },
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))
        OnulDoButton(text = "닫기", onClick = onDismiss)
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CharacterCell(
    drawable: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(90.dp)
            .clip(CircleShape)
            .background(if (selected) Persimmon10 else White)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Persimmon else DarkBrown40,
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(drawable),
            contentDescription = null,
            modifier = Modifier.size(68.dp),
        )
    }
}
