package com.example.onuldo_fe.ui.screen.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.ui.theme.White

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SourCream)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        HomeHeader(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        )

        EmptyChallengeContent(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 48.dp)
        )
    }
}

@Composable
private fun HomeHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(Persimmon20, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                MiniMascot(modifier = Modifier.size(27.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = "오늘도 함께 도전!",
                    color = DarkBrown.copy(alpha = 0.55f),
                    fontSize = 7.sp,
                    lineHeight = 8.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "꾸미지",
                    color = BlackBrown,
                    fontSize = 15.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Box(
            modifier = Modifier
                .size(30.dp)
                .background(White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_home_bell),
                contentDescription = "알림",
                modifier = Modifier.size(17.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun EmptyChallengeContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(98.dp)
                .background(Persimmon10, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_home_fire),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "아직 시작한 챌린지가 없어요",
            color = BlackBrown,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "도전글을 걸고 작은 습관부터\n갓생을 시작해보세요!",
            color = DarkBrown,
            style = MaterialTheme.typography.labelMedium,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(38.dp))

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(11.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Persimmon,
                contentColor = White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "챌린지 둘러보기",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun MiniMascot(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val orange = Persimmon
        val face = Color(0xFFFFB64E)
        val blush = Color(0xFFFF7E62)

        drawOval(
            color = orange,
            topLeft = Offset(size.width * 0.20f, size.height * 0.10f),
            size = Size(size.width * 0.60f, size.height * 0.70f)
        )
        drawCircle(
            color = face,
            radius = size.width * 0.23f,
            center = Offset(size.width * 0.50f, size.height * 0.47f)
        )
        drawCircle(BlackBrown, size.width * 0.025f, Offset(size.width * 0.42f, size.height * 0.45f))
        drawCircle(BlackBrown, size.width * 0.025f, Offset(size.width * 0.58f, size.height * 0.45f))
        drawCircle(blush, size.width * 0.04f, Offset(size.width * 0.36f, size.height * 0.53f))
        drawCircle(blush, size.width * 0.04f, Offset(size.width * 0.64f, size.height * 0.53f))
    }
}

@Preview(
    name = "Home Empty Challenge",
    showBackground = true,
    showSystemUi = true,
    widthDp = 360,
    heightDp = 740
)
@Composable
private fun HomeScreenPreview() {
    OnulDo_FETheme {
        HomeScreen()
    }
}

