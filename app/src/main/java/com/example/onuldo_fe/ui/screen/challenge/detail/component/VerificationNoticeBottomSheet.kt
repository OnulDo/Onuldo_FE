package com.example.onuldo_fe.ui.screen.challenge.detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.Green2
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.Red
import com.example.onuldo_fe.ui.theme.Red2
import com.example.onuldo_fe.ui.theme.SourCream
import kotlinx.coroutines.launch


//인증 유의사항 바텀시트 — 상세 화면의 "인증 유의사항 보기"에서 올라옴

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationNoticeBottomSheet(
    challengeTitle: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    // TODO: 실제 데이터(챌린지별 인증 조건)로 교체 / 더미 데이터 — API 연동 시 교체
    successConditions: List<String> = defaultSuccessConditions,
    failureConditions: List<String> = defaultFailureConditions
) {
    // skipPartiallyExpanded = 처음부터 전체 펼침 → 확인 버튼까지 바로 보임
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val spacing = LocalSpacing.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SourCream,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(DarkBrown20)
                )
            }
        },
        modifier = modifier
    ) {
        Spacer(Modifier.height(spacing.spacing18))

        Text(
            text = "인증 유의사항",
            fontFamily = Pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 18.sp,
            color = BlackBrown,
            modifier = Modifier.padding(start = 24.dp)
        )

        Spacer(Modifier.height(19.dp))

        HorizontalDivider(thickness = 1.dp, color = DarkBrown20)

        Spacer(Modifier.height(13.dp))

        Text(
            text = challengeTitle,
            fontFamily = Pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            lineHeight = 11.sp,
            color = DarkBrown,
            modifier = Modifier.padding(start = 24.dp)
        )

        Spacer(Modifier.height(spacing.spacing12))

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            ConditionBox(
                title = "인증 성공 조건",
                items = successConditions,
                accent = Green,
                background = Green2,
                isSuccess = true
            )

            Spacer(Modifier.height(spacing.spacing24))

            ConditionBox(
                title = "인증 실패 조건",
                items = failureConditions,
                accent = Red,
                background = Red2,
                isSuccess = false
            )
        }

        Spacer(Modifier.height(spacing.spacing36))   // 빨간 박스 ↔ 확인 버튼 36

        // 확인 — 내려가는 애니메이션 후 닫기 (클로드 추천)
        OnulDoButton(
            text = "확인",
            onClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) onDismiss()
                }
            }
        )

        Spacer(Modifier.height(spacing.spacing24))
        Spacer(Modifier.navigationBarsPadding())
    }
}

//성공/실패 조건 박스
@Composable
private fun ConditionBox(
    title: String,
    items: List<String>,
    accent: Color,
    background: Color,
    isSuccess: Boolean,
    modifier: Modifier = Modifier
) {
    // 고정 높이 대신 내용에 맞게 감싸도록 함 — 조건 개수/길이가 달라져도(실제 API 데이터)
    // 잘리거나 간격이 깨지지 않고, 항목 간 간격은 항상 10dp로 일정하게 유지된다.
    Column(
        modifier = modifier
            .fillMaxWidth()       // 시트 폭에 맞춤 (부모 padding 20 기준 = 390 프레임에서 350)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .padding(start = 16.dp, end = 16.dp, top = 17.dp, bottom = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(
                    if (isSuccess) R.drawable.challenge_check_icon else R.drawable.challenge_x_icon
                ),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.width(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,  // Body3
                color = accent
            )
        }

        Spacer(Modifier.height(12.dp))   // 헤더 ↔ 목록

        // 항목은 일정 간격(10dp)으로 배분 — 개수·줄바꿈에 따라 박스가 자연스럽게 늘어남
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items.forEach { item ->
                Text(
                    text = item,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Normal,  // Caption3: 12sp / 400 / lineHeight 20
                    fontSize = 12.sp,
                    lineHeight = 20.sp,
                    color = BlackBrown
                )
            }
        }
    }
}

// 더미 데이터 — 나중에 DB
private val defaultSuccessConditions = listOf(
    "침대와 개어진 이불이 사진에 함께 보여요",
    "인증 가능 시간(05:00 - 07:00) 내에 촬영",
    "카메라로 직접 촬영한 사진만 인정돼요",
    "본인 침대가 명확히 식별돼요"
)

private val defaultFailureConditions = listOf(
    "갤러리에서 업로드한 사진",
    "침대 또는 이불이 보이지 않음",
    "이불이 정돈되지 않은 상태",
    "미션 조건(개어진 이불) 미충족",
    "동일/유사 사진 재사용"
)
