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
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.onuldo_fe.ui.theme.BlackBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.ui.theme.LocalSpacing

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterPickerSheet(
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val spacing = LocalSpacing.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SourCream,
        // Figma(5154:4894)에는 드래그 핸들이 없다. 기본 핸들을 끄고 상단 여백을 직접 준다.
        dragHandle = null,
        scrimColor = BlackBrown40,
        // 기본 시트는 절반 높이에서 멈춰 9개가 한 화면에 안 들어온다.
        // skipPartiallyExpanded로 처음부터 전체 높이로 펼친다.
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        // 시트 상단 304 기준: 제목 325 · 그리드 365 · 닫기 746 · 시트 끝 844
        Spacer(Modifier.height(21.dp))
        Text(
            text = "프로필 사진 선택",
            // Figma(5154:4927)는 이 제목만 Pretendard SemiBold 16이다(디자인시스템에 이름 붙은 스타일 없음).
            style = OnulDoTypography.body2Bold.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            ),
            color = BlackBrown,
            modifier = Modifier.padding(start = spacing.spacing26),
        )
        Spacer(Modifier.height(21.dp))

        // 3×3 원형 그리드. 셀 100 + 가로 간격 19 + 좌우 26 = 390에 정확히 맞는다.
        ProfileCharacters.chunked(3).forEachIndexed { rowIndex, rowItems ->
            if (rowIndex > 0) Spacer(Modifier.height(spacing.spacing26))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.spacing26),
                horizontalArrangement = Arrangement.spacedBy(19.dp, Alignment.CenterHorizontally),
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

        Spacer(Modifier.height(22.dp))
        OnulDoButton(text = "닫기", onClick = onDismiss)
        Spacer(Modifier.height(42.dp))
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
            .size(100.dp)
            .clip(CircleShape)
            // Figma: 셀 배경은 흰색이 아니라 아이보리(SourCream), 테두리는 1.5px brown/20.
            .background(if (selected) Persimmon10 else SourCream)
            .border(
                width = if (selected) 2.dp else 1.5.dp,
                color = if (selected) Persimmon else DarkBrown20,
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(drawable),
            contentDescription = null,
            modifier = Modifier.size(74.dp),
        )
    }
}
