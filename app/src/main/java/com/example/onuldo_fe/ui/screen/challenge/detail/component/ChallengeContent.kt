package com.example.onuldo_fe.ui.screen.challenge.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.model.challenge.BlockType
import com.example.onuldo_fe.model.challenge.ContentBlock
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard

/**
 * 챌린지 상세 본문. 서버 description(블록 JSON)을 파싱한 [blocks]를 타입별로 렌더링한다.
 * 각 블록의 텍스트 스타일은 기존 하드코딩 상세 화면(섹션 제목/summary/benefit/추천)의 스타일 그대로.
 * 미지 타입(UNKNOWN)은 그리지 않는다.
 */
@Composable
fun ChallengeContent(
    blocks: List<ContentBlock>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        blocks.forEachIndexed { index, block ->
            val prevType = blocks.getOrNull(index - 1)?.type

            // 블록 사이 세로 간격
            val topPadding = when {
                index == 0 -> 0.dp
                block.type == BlockType.H2 -> 18.dp
                block.type == BlockType.H3 -> 12.dp
                // BLOCKQUOTE가 연속되는 경우(단독 항목 나열)만 8dp
                prevType == BlockType.BLOCKQUOTE && block.type == BlockType.BLOCKQUOTE -> 8.dp
                prevType == BlockType.H2 && block.type == BlockType.PARAGRAPH -> 12.dp
                // 제목→첫 항목: 제목형 항목(뒤에 설명 붙음)이면 10, 단독 항목(추천 등)이면 14
                prevType == BlockType.H2 && block.type == BlockType.BLOCKQUOTE ->
                    if (blocks.getOrNull(index + 1)?.type == BlockType.PARAGRAPH) 10.dp else 14.dp
                prevType == BlockType.H2 -> 12.dp
                block.type == BlockType.PARAGRAPH -> 2.dp
                block.type == BlockType.BLOCKQUOTE -> 6.dp
                else -> 0.dp
            }

            when (block.type) {
                // 섹션 제목 (Body1 22/700/40)
                BlockType.H2 -> Text(
                    text = block.content,
                    modifier = Modifier.padding(top = topPadding),
                    style = OnulDoTypography.title1Bold,
                    color = BlackBrown
                )

                BlockType.H3 -> Text(
                    text = block.content,
                    modifier = Modifier.padding(top = topPadding),
                    style = OnulDoTypography.body4Bold,
                    color = BlackBrown
                )

                BlockType.PARAGRAPH -> {
                    if (prevType == BlockType.H2) {
                        // 섹션 리드 문단 — 기존 summary 스타일(주황 막대 + 13/400/24)
                        Row(
                            modifier = Modifier.padding(top = topPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 4.dp, height = 19.dp)
                                    .background(Persimmon, RoundedCornerShape(4.dp))
                            )
                            Text(
                                text = block.content,
                                modifier = Modifier.padding(start = 5.dp),
                                style = OnulDoTypography.caption1Regular,
                                color = BlackBrown
                            )
                        }
                    } else {
                        // 일반 문단 — 기존 benefit 설명 스타일 (13/400/19)
                        Text(
                            text = block.content,
                            modifier = Modifier.padding(top = topPadding),
                            style = OnulDoTypography.caption1Regular,
                            color = BlackBrown
                        )
                    }
                }

                // blockquote는 두 용도(타입 동일). 뒤에 설명(paragraph)이 붙으면 강조 제목(Body4 14/700/22),
                // 아니면 단독 항목(추천 등, 14/400/20). 둘 다 DarkBrown.
                BlockType.BLOCKQUOTE -> {
                    val isTitle = blocks.getOrNull(index + 1)?.type == BlockType.PARAGRAPH
                    if (isTitle) {
                        Text(
                            text = block.content,
                            modifier = Modifier.padding(top = topPadding),
                            style = OnulDoTypography.body4Bold,
                            color = DarkBrown
                        )
                    } else {
                        Text(
                            text = block.content,
                            modifier = Modifier.padding(top = topPadding),
                            style = OnulDoTypography.body4Regular,
                            color = DarkBrown
                        )
                    }
                }

                // 빈 줄 — 항목 간 여백
                BlockType.LINEBREAK -> Spacer(Modifier.height(12.dp))

                BlockType.UNKNOWN -> Unit
            }
        }
    }
}
