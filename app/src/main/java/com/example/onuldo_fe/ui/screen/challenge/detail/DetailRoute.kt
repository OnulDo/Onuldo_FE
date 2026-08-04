package com.example.onuldo_fe.ui.screen.challenge.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.SourCream
import com.example.onuldo_fe.viewmodel.challenge.ChallengeDetailViewModel

/**
 * 챌린지 상세 화면의 상태 보유 진입점.
 * challengeId로 상세를 조회해 로딩/에러/성공을 처리하고, 성공 시 [DetailScreen]을 그린다.
 * (DetailScreen은 파티 생성 흐름에서도 재사용되므로 상태 없는 순수 UI로 유지한다.)
 */
@Composable
fun DetailRoute(
    challengeId: Long,
    onBackClick: () -> Unit,
    // 참여 화면으로 이동. 이미 조회한 상세의 제목/한줄설명/카테고리/인증시간을 함께 넘김
    onJoinClick: (
        title: String,
        description: String,
        category: String,
        timeStart: String,
        timeEnd: String
    ) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChallengeDetailViewModel = viewModel(
        factory = ChallengeDetailViewModel.factory(challengeId)
    )
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    // 예외: 상세 데이터 로드 실패 시 토스트로 안내하고 챌린지 목록(갤러리)으로 되돌린다.
    LaunchedEffect(uiState.isError) {
        if (uiState.isError) {
            Toast.makeText(
                context,
                "상세 정보를 불러오지 못했어요. 다시 시도해주세요",
                Toast.LENGTH_SHORT
            ).show()
            onBackClick()
        }
    }

    val detail = uiState.detail
    if (detail != null) {
        DetailScreen(
            challenge = Challenge(
                id = detail.id,
                title = detail.title,
                participantCount = detail.participantCount,
                category = detail.category
            ),
            category = detail.category.displayName,
            content = detail.content,
            verificationDescription = detail.verificationDescription,
            verificationImageUrl = detail.verificationExampleImageUrl,
            successConditions = detail.successConditions,
            failureConditions = detail.failureConditions,
            onBackClick = onBackClick,
            // 참여 화면 정보박스용: 제목(name), 한줄설명(explainContent), 카테고리 라벨, 인증 시간
            onJoinClick = {
                onJoinClick(
                    detail.title,
                    detail.summary,
                    "${detail.category.displayName} 챌린지",
                    detail.timeStart,
                    detail.timeEnd
                )
            },
            modifier = modifier
        )
    } else {
        // 로딩 중이거나, 에러 직후(곧 위 LaunchedEffect가 목록으로 되돌림)
        DetailLoading(modifier)
    }
}

@Composable
private fun DetailLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SourCream),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Persimmon)
    }
}
