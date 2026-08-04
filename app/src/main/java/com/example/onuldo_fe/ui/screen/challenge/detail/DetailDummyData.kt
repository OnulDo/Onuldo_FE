package com.example.onuldo_fe.ui.screen.challenge.detail

import com.example.onuldo_fe.model.challenge.BlockType
import com.example.onuldo_fe.model.challenge.ContentBlock

// ===== 챌린지 상세 화면 임시(더미) 본문 블록 =====

// - 값을 넘기지 않는 경로(프리뷰/파티)에서만 이 더미가 폴백으로 쓰인다.
// ※ 내용은 기존 더미 그대로 유지 — 임의 수정·삭제 금지(항목/문구 변경 X).
internal val dummyContentBlocks: List<ContentBlock> = listOf(
    ContentBlock(BlockType.H2, "이 챌린지는?"),
    ContentBlock(BlockType.PARAGRAPH, "세상보다 먼저 눈뜨는 21일, 나만의 새벽 30분"),
    ContentBlock(BlockType.LINEBREAK, ""),

    ContentBlock(BlockType.H2, "하면 좋은 점"),
    ContentBlock(BlockType.BLOCKQUOTE, "아침에 나만의 30분이 생겨요"),
    ContentBlock(BlockType.PARAGRAPH, "세상이 조용한 시간, 방해 없이 나에게 집중할 수 있어요"),
    ContentBlock(BlockType.LINEBREAK, ""),
    ContentBlock(BlockType.BLOCKQUOTE, "마음의 여유가 생겨요"),
    ContentBlock(BlockType.PARAGRAPH, "허둥지둥 뛰는 아침 대신, 커피 한 잔의 여유를 챙겨요"),
    ContentBlock(BlockType.LINEBREAK, ""),
    ContentBlock(BlockType.BLOCKQUOTE, "생체 리듬이 정렬돼요"),
    ContentBlock(BlockType.PARAGRAPH, "일찍 일어나면 밤에 잠도 잘 오는 선순환이 만들어져요"),
    ContentBlock(BlockType.LINEBREAK, ""),
    ContentBlock(BlockType.BLOCKQUOTE, "하루의 주도권을 되찾아요"),
    ContentBlock(BlockType.PARAGRAPH, "'시작 당한' 게 아니라 '시작한' 감각으로 살게 돼요"),
    ContentBlock(BlockType.LINEBREAK, ""),

    ContentBlock(BlockType.H2, "이런 분께 추천해요"),
    ContentBlock(BlockType.BLOCKQUOTE, "미라클 모닝을 여러 번 시도했지만 매번 3일을 못 넘긴 분"),
    ContentBlock(BlockType.BLOCKQUOTE, "출근·등교 전 늘 시간에 쫓기는 게 지친 분"),
    ContentBlock(BlockType.BLOCKQUOTE, "아침형 인간이 되고 싶어 자기 계발을 시작하려는 분"),
    ContentBlock(BlockType.BLOCKQUOTE, "혼자서는 자꾸 무너져서 함께할 동료가 필요한 분")
)
