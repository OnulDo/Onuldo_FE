package com.example.onuldo_fe.data.challenge.mapper

import com.example.onuldo_fe.data.challenge.dto.ChallengeItemDto
import com.example.onuldo_fe.data.challenge.dto.ChallengeListResultDto
import com.example.onuldo_fe.data.challenge.dto.ParticipationResultDto
import com.example.onuldo_fe.model.challenge.BlockType
import com.example.onuldo_fe.model.challenge.ChallengeCategory
import com.example.onuldo_fe.model.challenge.ChallengeDetail
import com.example.onuldo_fe.model.challenge.ChallengePage
import com.example.onuldo_fe.model.challenge.ContentBlock
import com.example.onuldo_fe.model.challenge.ParticipationResult
import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge

// enum 이름을 API category 값과 동일하게 맞췄으므로 별도 매핑표 없이 name/valueOf로 변환한다
// 화면 필터에서 고른 카테고리를 API의 category 파라미터 값으로 변환
fun ChallengeCategory.toApiValue(): String = name

// 서버 category 문자열 → 앱 enum (디폴트: 생활루틴으로 처리)
private fun String.toChallengeCategory(): ChallengeCategory =
    ChallengeCategory.entries.firstOrNull { it.name == this } ?: ChallengeCategory.LIFESTYLE_ROUTINE

// 목록 아이템 DTO → 화면 모델. 목록 카드에 필요한 필드만 추리기
fun ChallengeItemDto.toModel(): Challenge = Challenge(
    id = challengeId,
    title = name,
    participantCount = participantCount,
    category = category.toChallengeCategory(),
    imageUrl = captionImgUrl
)

// 목록 응답 → 도메인 페이지 (커서 기반: page는 미사용이라 0)
//TODO: 무한 스크롤 구현시 page 사용 (2순위)
fun ChallengeListResultDto.toModel(): ChallengePage = ChallengePage(
    challenges = content.map { it.toModel() },
    page = 0,
    hasNext = hasNext
)

// 상세 응답 DTO → 상세 도메인 모델.
// description(블록 JSON 문자열)은 파싱해 content 블록 리스트로, 인증 문구는 null이면 빈 문자열로 안전 처리.
fun ChallengeItemDto.toDetailModel(): ChallengeDetail = ChallengeDetail(
    id = challengeId,
    title = name,
    summary = explainContent.orEmpty(),
    participantCount = participantCount,
    category = category.toChallengeCategory(),
    timeStart = timeStart.orEmpty(),
    timeEnd = timeEnd.orEmpty(),
    content = description.orEmpty().map {
        ContentBlock(type = BlockType.from(it.type), content = it.content.orEmpty())
    },
    verificationDescription = verifyMethodContent.orEmpty(),
    verificationExampleImageUrl = verificationExamplePhotoUrl,
    successConditions = successConditionList.orEmpty(),
    failureConditions = failureConditionList.orEmpty()
)

// 참여 응답 DTO → 도메인 모델
fun ParticipationResultDto.toModel(): ParticipationResult = ParticipationResult(
    startDate = startDate,
    endDate = endDate,
    durationWeeks = durationWeeks,
    durationDays = durationDays,
    depositAmount = depositAmount,
    expectedRefundAmount = expectedRefundAmount
)

