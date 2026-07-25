package com.example.onuldo_fe.ui.component.party

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.SourCream

@Composable
fun PartyCapacitySelector(
    capacity: Int,
    onCapacityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minCapacity: Int = 2,
    maxCapacity: Int = 5
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(White, RoundedCornerShape(14.dp))
            .border(1.dp, DarkBrown40, RoundedCornerShape(14.dp))
            .padding(horizontal = 19.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CapacityControlIcon(isPlus = false, enabled = capacity > minCapacity) {
            onCapacityChange((capacity - 1).coerceAtLeast(minCapacity))
        }
        Text("$capacity 명", Modifier.weight(1f), color = BlackBrown, fontFamily = Pretendard, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        CapacityControlIcon(isPlus = true, enabled = capacity < maxCapacity) {
            onCapacityChange((capacity + 1).coerceAtMost(maxCapacity))
        }
    }
}

@Composable
private fun CapacityControlIcon(isPlus: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val color = if (isPlus) Persimmon else DarkBrown70
    val accessibilityLabel = if (isPlus) "인원 늘리기" else "인원 줄이기"
    Canvas(
        Modifier
            .size(25.dp)
            .clickable(
                enabled = enabled,
                onClickLabel = accessibilityLabel,
                onClick = onClick
            )
            .semantics { contentDescription = accessibilityLabel }
    ) {
        val alpha = if (enabled) 1f else 0.25f
        drawCircle(color.copy(alpha = alpha), style = Stroke(width = 2.dp.toPx()))
        drawLine(color.copy(alpha = alpha), Offset(size.width * 0.29f, size.height * 0.5f), Offset(size.width * 0.71f, size.height * 0.5f), strokeWidth = 2.dp.toPx())
        if (isPlus) {
            drawLine(color.copy(alpha = alpha), Offset(size.width * 0.5f, size.height * 0.29f), Offset(size.width * 0.5f, size.height * 0.71f), strokeWidth = 2.dp.toPx())
        }
    }
}

@Preview(name = "파티 모집 인원", showBackground = true, widthDp = 390)
@Composable
private fun PartyCapacitySelectorPreview() {
    OnulDo_FETheme {
        Box(Modifier.background(SourCream).padding(20.dp)) {
            PartyCapacitySelector(capacity = 5, onCapacityChange = {})
        }
    }
}
