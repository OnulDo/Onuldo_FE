package com.example.onuldo_fe.ui.screen.party.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.HeaderCream
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Pretendard

// 파티 하위 화면의 뒤로가기 버튼과 제목 위치를 동일한 규격으로 관리
@Composable
fun PartyTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(HeaderCream)
    ) {
        // 아이콘은 피그마 크기로 표시하고 터치 영역은 48x56dp로 확보
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(56.dp)
                .clickable(onClick = onBack)
        ) {
            Image(
                painter = painterResource(R.drawable.back_btn),
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .offset(x = 20.dp, y = 30.dp)
                    .size(width = 8.dp, height = 14.dp)
            )
        }

        Text(
            text = title,
            modifier = Modifier.offset(x = 48.dp, y = 28.dp),
            color = BlackBrown,
            style = OnulDoTypography.body2Bold
        )
    }
}

@Preview(name = "파티 상단 헤더", showBackground = true, widthDp = 390, heightDp = 56)
@Composable
private fun PartyTopBarPreview() {
    OnulDo_FETheme {
        PartyTopBar(title = "파티 만들기", onBack = {})
    }
}
