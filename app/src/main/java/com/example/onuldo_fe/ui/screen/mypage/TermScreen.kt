package com.example.onuldo_fe.ui.screen.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onuldo_fe.data.auth.dto.TermType
import com.example.onuldo_fe.model.term.TermContentBlock
import com.example.onuldo_fe.model.term.Term
import com.example.onuldo_fe.ui.screen.mypage.component.MyPageTopBar
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.BlackBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.viewmodel.mypage.TermViewModel
import com.example.onuldo_fe.viewmodel.mypage.TermUiState

/**
 * 서버가 내려주는 본문 블록 종류(실측): `h2` 소제목 · `paragraph` 문단 · `linebreak` 빈 줄.
 * 다른 제목 레벨이 추가될 수 있어 `h1`~`h3`를 모두 소제목으로 본다.
 */
private val HEADING_TYPES = setOf("h1", "h2", "h3")
private const val TYPE_LINEBREAK = "linebreak"

/**
 * 약관 상세 (서비스 이용약관 · 개인정보 처리방침 · 환급 정책).
 *
 * 본문은 `GET /api/terms/{termType}`로 받아 `content` 블록 배열을 순서대로 렌더한다.
 * 화면 제목은 진입한 메뉴 이름([fallbackTitle])을 쓰고, 서버가 제목을 주면 그것을 우선한다.
 */
@Composable
fun TermScreen(
    termType: TermType,
    fallbackTitle: String,
    onBack: () -> Unit,
    viewModel: TermViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val spacing = LocalSpacing.current

    LaunchedEffect(termType) { viewModel.load(termType) }

    TermScreenContent(
        state = state,
        fallbackTitle = fallbackTitle,
        onBack = onBack,
    )
}

@Composable
private fun TermScreenContent(
    state: TermUiState,
    fallbackTitle: String,
    onBack: () -> Unit,
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        MyPageTopBar(title = state.term?.title?.takeIf { it.isNotBlank() } ?: fallbackTitle, onBack = onBack)

        when {
            state.isLoading -> LoadingBox()

            state.errorMessage != null -> MessageBox(state.errorMessage.orEmpty())

            state.term == null || state.term?.content.isNullOrEmpty() ->
                MessageBox("약관 내용을 불러오지 못했어요.")

            else -> {
                val term = state.term ?: return@Column
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                ) {
                    Spacer(Modifier.height(spacing.spacing20))
                    Text(
                        text = term.title?.takeIf { it.isNotBlank() } ?: fallbackTitle,
                        style = OnulDoTypography.title1Bold,
                        color = BlackBrown,
                        )
                    Spacer(Modifier.height(spacing.spacing8))
                    term.effectiveDate?.takeIf { it.isNotBlank() }?.let { date ->
                        Text(
                            text = "시행일자 $date",
                            style = OnulDoTypography.caption2Medium,
                            color = DarkBrown50,
                        )
                        Spacer(Modifier.height(spacing.spacing48))
                    }

                    term.content.forEach { block ->
                        if (block.type == TYPE_LINEBREAK) {
                            // 문단 사이 여백 전용 블록.
                            Spacer(Modifier.height(spacing.spacing8))
                        } else {
                            TermBlock(block)
                            Spacer(Modifier.height(4.dp))
                        }
                    }

                    Spacer(Modifier.height(spacing.spacing24))
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun TermScreenPreview() {
    OnulDo_FETheme {
        TermScreenContent(
            state = TermUiState(
                isLoading = false,
                term = Term(
                    termType = TermType.SERVICE,
                    title = "서비스 이용약관",
                    effectiveDate = "2026.08.13",
                    content = listOf(
                        TermContentBlock("h2", "제1조 (목적)"),
                        TermContentBlock(
                            "paragraph",
                            "본 약관은 오늘DO가 제공하는 서비스의 이용 조건과 절차를 규정합니다.",
                        ),
                        TermContentBlock("linebreak", ""),
                        TermContentBlock("h2", "제2조 (서비스 이용)"),
                        TermContentBlock(
                            "paragraph",
                            "사용자는 본 약관과 서비스 운영 정책을 준수하여야 합니다.",
                        ),
                    ),
                ),
            ),
            fallbackTitle = "서비스 이용약관",
            onBack = {},
        )
    }
}

@Composable
private fun TermBlock(block: TermContentBlock) {
    if (block.content.isBlank()) return

    if (block.type in HEADING_TYPES) {
        Text(
            text = block.content,
            style = OnulDoTypography.body4Bold,
            color = BlackBrown,
        )
    } else {
        Text(
            text = block.content,
            style = OnulDoTypography.caption1Medium,
            color = BlackBrown50,
        )
    }
}

@Composable
private fun LoadingBox() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Persimmon)
    }
}

@Composable
private fun MessageBox(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = OnulDoTypography.caption1Regular,
            color = DarkBrown70,
        )
    }
}
