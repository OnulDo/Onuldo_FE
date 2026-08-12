package com.example.onuldo_fe.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.Persimmon

// 홈과 파티에서 공통으로 사용하는 빈 상태의 아이콘·제목·설명 영역
@Composable
fun EmptyStateContent(
    @DrawableRes iconRes: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Persimmon.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(width = 82.dp, height = 95.dp)
                    .offset(x = (-5).dp, y = 2.5.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.height(spacing.spacing16))
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            color = BlackBrown,
            style = MaterialTheme.typography.titleLarge,
            lineHeight = 26.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Spacer(Modifier.height(9.dp))
        Text(
            text = description,
            modifier = Modifier.fillMaxWidth(),
            color = DarkBrown,
            style = MaterialTheme.typography.labelLarge,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}
