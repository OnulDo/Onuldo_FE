package com.example.onuldo_fe.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard

@Composable
fun HomeRemainingTimeChip(
    remainingMinutes: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = remainingMinutes.coerceAtLeast(0).toRemainingTimeText(),
        style = MaterialTheme.typography.labelLarge,
        color = Persimmon,
        fontFamily = Pretendard,
        modifier = modifier
            .background(Persimmon10, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 2.dp)
    )
}

@Composable
private fun Int.toRemainingTimeText(): String {
    val hours = this / 60
    return if (this >= 60) {
        stringResource(R.string.home_challenge_hours_left, hours)
    } else {
        stringResource(R.string.home_challenge_minutes_left, this)
    }
}
