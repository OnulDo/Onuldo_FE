package com.example.onuldo_fe.ui.component.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

@Composable
fun HomeHeader(
    userName: String,
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {}
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Persimmon20, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_run_light_icon),
                contentDescription = null,
                modifier = Modifier.size(width = 38.dp, height = 37.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "오늘두 함께 도전!",
                color = BlackBrown.copy(alpha = 0.7f),
                fontFamily = Pretendard,
                fontSize = 8.sp,
                lineHeight = 8.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = userName,
                color = BlackBrown,
                fontFamily = Pretendard,
                fontSize = 18.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Box(
            modifier = Modifier
                .size(30.dp)
                .background(White, CircleShape)
                .border(BorderStroke(1.dp, DarkBrown20), CircleShape)
                .clickable(onClick = onNotificationClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_bell_icon),
                contentDescription = "알림",
                modifier = Modifier.size(17.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF7, widthDp = 390)
@Composable
private fun HomeHeaderPreview() {
    OnulDo_FETheme {
        HomeHeader(
            userName = "김민지",
            modifier = Modifier
                .fillMaxWidth()
                .background(SourCream)
                .padding(horizontal = 20.dp)
        )
    }
}
