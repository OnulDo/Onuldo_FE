package com.example.onuldo_fe.model.challenge

// 챌린지 상세 본문(description JSON)의 블록 (1개씩)
// 서버가 마크다운을 파싱해 [{ "type": ..., "content": ... }] 배열(문자열)로 내려줌
data class ContentBlock(
    val type: BlockType,
    val content: String
)

// 서버가 내려주는 블록 타입. 목록에 없는 값이 와도 앱이 죽지 않도록 UNKNOWN으로
enum class BlockType {
    H2,          // 섹션 제목 ("이 챌린지는?", "하면 좋은 점" 등)
    H3,          // 소제목
    PARAGRAPH,   // 본문 문단
    LINEBREAK,   // 빈 줄(간격)
    BLOCKQUOTE,  // 강조 항목 (하면 좋은 점/추천 항목)
    UNKNOWN;     // 미지 타입 — 렌더링하지 않음

    companion object {
        fun from(raw: String?): BlockType =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: UNKNOWN
    }
}
