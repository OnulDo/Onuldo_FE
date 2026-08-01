package com.example.onuldo_fe.ui.screen.challenge.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.model.challenge.ChallengeDetail
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
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
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChallengeDetailViewModel = viewModel(
        factory = ChallengeDetailViewModel.factory(challengeId)
    )
) {
    val uiState = viewModel.uiState

    when {
        uiState.detail != null -> {
            val detail = uiState.detail!!
            DetailScreen(
                challenge = Challenge(
                    id = detail.id.toInt(),
                    title = detail.title,
                    participantCount = detail.participantCount,
                    category = detail.category
                ),
                category = detail.category.displayName,
                summary = detail.summary,
                verificationDescription = detail.verificationDescription,
                verificationImageUrl = detail.verificationExampleImageUrl,
                onBackClick = onBackClick,
                onJoinClick = onJoinClick,
                modifier = modifier
            )
        }

        uiState.isLoading -> DetailLoading(modifier)

        // 로딩도 아니고 데이터도 없으면 에러 상태
        else -> DetailError(onRetry = viewModel::retry, modifier = modifier)
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

@Composable
private fun DetailError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SourCream),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "챌린지 정보를 불러오지 못했어요",
                fontFamily = Pretendard,
                color = BlackBrown
            )
            OnulDoButton(
                text = "다시 시도",
                onClick = onRetry,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        }
    }
}
