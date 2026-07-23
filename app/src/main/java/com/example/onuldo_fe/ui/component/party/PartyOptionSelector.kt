package com.example.onuldo_fe.ui.component.party

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

@Composable
fun PartyOptionSelector(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    textSize: TextUnit = 14.sp
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEachIndexed { index, text ->
            val selected = index == selectedIndex
            Box(
                Modifier
                    .weight(1f)
                    .height(40.dp)
                    .background(if (selected) Persimmon else White, RoundedCornerShape(14.dp))
                    .then(if (!selected) Modifier.border(1.dp, DarkBrown40, RoundedCornerShape(14.dp)) else Modifier)
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(text, color = if (selected) SourCream else BlackBrown, fontFamily = Pretendard, fontSize = textSize, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(name = "파티 옵션 선택", showBackground = true, widthDp = 390)
@Composable
private fun PartyOptionSelectorPreview() {
    OnulDo_FETheme {
        Box(Modifier.background(SourCream).padding(20.dp)) {
            PartyOptionSelector(options = listOf("2주", "4주", "8주", "12주"), selectedIndex = 1, onSelect = {})
        }
    }
}
