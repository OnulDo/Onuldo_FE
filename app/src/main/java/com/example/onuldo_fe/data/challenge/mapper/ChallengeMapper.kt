package com.example.onuldo_fe.data.challenge.mapper

import com.example.onuldo_fe.data.challenge.dto.ChallengeItemDto
import com.example.onuldo_fe.data.challenge.dto.ChallengeListResultDto
import com.example.onuldo_fe.model.challenge.ChallengeCategory
import com.example.onuldo_fe.model.challenge.ChallengePage
import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge

// enum 이름을 API category 값과 동일하게 맞췄으므로 별도 매핑표 없이 name/valueOf로 변환한다
// 화면 필터에서 고른 카테고리를 API의 category 파라미터 값으로 변환
fun ChallengeCategory.toApiValue(): String = name

// 서버 category 문자열 → 앱 enum (알 수 없는 값은 생활루틴으로 안전 처리)
private fun String.toChallengeCategory(): ChallengeCategory =
    ChallengeCategory.entries.firstOrNull { it.name == this } ?: ChallengeCategory.LIFESTYLE_ROUTINE

// 목록 아이템 DTO → 화면 모델. 목록 카드에 필요한 필드만 추리기
fun ChallengeItemDto.toModel(): Challenge = Challenge(
    id = id.toInt(), //int형?
    title = name,
    participantCount = participantCount,
    category = category.toChallengeCategory(),
    imageUrl = captionImgUrl
)

// result 페이로드 → 도메인 페이지
fun ChallengeListResultDto.toModel(): ChallengePage = ChallengePage(
    challenges = challenges.map { it.toModel() },
    page = page,
    hasNext = hasNext
)
