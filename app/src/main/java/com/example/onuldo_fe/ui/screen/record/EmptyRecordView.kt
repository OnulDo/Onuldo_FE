package com.example.onuldo_fe.ui.screen.record

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoMediumButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme

@Composable
fun EmptyRecordView(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onBrowseChallenges: () -> Unit = {}
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(spacing.spacing76))

        Image(
            modifier = Modifier
                .width(120.dp)
                .height(120.dp),
            painter = painterResource(id = R.drawable.record_empty_icon),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(spacing.spacing16))

        Text(
            text = title,
            style = OnulDoTypography.title1Bold,
            color = BlackBrown
        )

        Spacer(modifier = Modifier.height(spacing.spacing8))

        Text(
            text = description,
            style = OnulDoTypography.caption1Medium,
            color = DarkBrown,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(spacing.spacing48))

        OnulDoMediumButton(
            text = "챌린지 둘러보기",
            onClick = onBrowseChallenges
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyRecordViewPreview() {
    OnulDo_FETheme {
        EmptyRecordView(
            title = "진행 중인 챌린지가 없어요",
            description = "탐색 탭에서 새로운 챌린지를\n" + "찾아 시작해보세요!"
        )
    }
}