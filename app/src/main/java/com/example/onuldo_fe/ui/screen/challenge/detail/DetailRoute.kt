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
    // CTA 클릭 시 동작. 조회한 상세 값을 [ChallengeActionData]로 묶어 넘긴다.
    // (챌린지 탭=참여 화면 이동, 파티 생성 흐름=선택 확정 등 호출부마다 다르게 사용)
    onActionClick: (ChallengeActionData) -> Unit,
    modifier: Modifier = Modifier,
    // CTA 라벨 — 챌린지 탭은 "참여하기", 파티 생성 흐름은 "파티 만들기"로 재사용
    actionText: String = "참여하기",
    // challengeId를 key로 줘, 같은 ViewModelStoreOwner에서 챌린지가 바뀌면 새 상세를 조회
    // (파티 생성 흐름은 nav 목적지가 아니라 화면 상태 전환이라 key가 없으면 이전 챌린지 VM이 재사용된다.)
    viewModel: ChallengeDetailViewModel = viewModel(
        key = challengeId.toString(),
        factory = ChallengeDetailViewModel.factory(challengeId)
    )
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    // 예외: 상세 데이터 로드 실패 시 토스트로 안내하고 챌린지 목록(갤러리)으로 되돌림
    LaunchedEffect(uiState.isError) {
        if (uiState.isError) {
            Toast.makeText(
                context,
                uiState.errorMessage ?: "상세 정보를 불러오지 못했어요. 다시 시도해주세요",
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
            // 참여 화면 정보박스용: id, 제목(name), 한줄설명(explainContent), 카테고리 라벨, 인증 시간
            //현재는 title만 사용하나, 화면 재사용을 위해 전체로 작성
            onActionClick = {
                onActionClick(
                    ChallengeActionData(
                        challengeId = detail.id,
                        title = detail.title,
                        description = detail.summary,
                        category = "${detail.category.displayName} 챌린지",
                        timeStart = detail.timeStart,
                        timeEnd = detail.timeEnd
                    )
                )
            },
            ctaText = actionText,
            modifier = modifier
        )
    } else {
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