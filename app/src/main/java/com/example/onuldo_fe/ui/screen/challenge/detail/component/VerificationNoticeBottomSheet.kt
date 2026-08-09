package com.example.onuldo_fe.ui.screen.challenge.detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
    // 성공/실패 조건. 비어 있으면(미전달/서버 데이터 없음) 아래에서 기본 조건으로 폴백한다.
    successConditions: List<String> = emptyList(),
    failureConditions: List<String> = emptyList()
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

        // 조건 리스트는 명세상 optional(required=false) → 비어 있으면 빈 박스 대신 섹션 자체를 숨긴다
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            if (successConditions.isNotEmpty()) {
                ConditionBox(
                    title = "인증 성공 조건",
                    items = successConditions,
                    accent = Green,
                    background = Green2,
                    isSuccess = true
                )
            }

            if (successConditions.isNotEmpty() && failureConditions.isNotEmpty()) {
                Spacer(Modifier.height(spacing.spacing24))
            }

            if (failureConditions.isNotEmpty()) {
                ConditionBox(
                    title = "인증 실패 조건",
                    items = failureConditions,
                    accent = Red,
                    background = Red2,
                    isSuccess = false
                )
            }
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
    // 고정 높이 대신 내용에 맞게 감싸도록 함(조건 BOX) — 조건 개수/길이가 달라져도 ok(실제 API 데이터)
    Column(
        modifier = modifier
            .fillMaxWidth()       // 시트 폭에 맞춤 (부모 padding 20 기준 = 390 프레임에서 350)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)
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

        // 항목 사이 간격은 패딩 4dp로 배분(첫 항목 제외) — TODO: 추후 4추가할 예정
        Column(modifier = Modifier.fillMaxWidth()) {
            items.forEachIndexed { index, item ->
                Text(
                    text = item,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Normal,  // Caption3: 12sp / 400 / lineHeight 20
                    fontSize = 12.sp,
                    lineHeight = 20.sp,
                    color = BlackBrown,
                    modifier = if (index == 0) Modifier else Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

