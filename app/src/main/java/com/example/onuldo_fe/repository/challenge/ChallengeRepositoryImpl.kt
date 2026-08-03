package com.example.onuldo_fe.repository.challenge

import com.example.onuldo_fe.data.challenge.api.ChallengeApi
import com.example.onuldo_fe.data.challenge.dto.ParticipationRequestDto
import com.example.onuldo_fe.data.challenge.mapper.toApiValue
import com.example.onuldo_fe.data.challenge.mapper.toDetailModel
import com.example.onuldo_fe.data.challenge.mapper.toModel
import com.example.onuldo_fe.model.challenge.ChallengeCategory
import com.example.onuldo_fe.model.challenge.ChallengeDetail
import com.example.onuldo_fe.model.challenge.ChallengePage
import com.example.onuldo_fe.model.challenge.ParticipationResult

// 챌린지 목록 API 요청과 DTO → 도메인 모델 변환 담당 (party RepositoryImpl과 동일 패턴)
class ChallengeRepositoryImpl(private val api: ChallengeApi) : ChallengeRepository {
    override suspend fun getChallenges(
        page: Int,
        size: Int,
        category: ChallengeCategory?,
        search: String?
    ): ChallengePage {
        // page는 커서 페이지네이션 전환으로 미사용(인터페이스 호환 위해 파라미터만 유지)
        val response = api.getChallenges(
            size = size,
            category = category?.toApiValue(),          // 앱 enum → API category 문자열
            search = search?.takeIf { it.isNotBlank() }  // 공백이면 s 미전송 (빈목록 처리는 ViewModel 담당)
        )
        return response.toModel()   // 목록 응답(content/hasNext) → 도메인 페이지
    }

    override suspend fun getChallengeDetail(challengeId: Long): ChallengeDetail {
        val response = api.getChallengeDetail(challengeId)
        return response.result.toDetailModel()
    }

    override suspend fun participate(
        challengeId: Long,
        depositAmount: Int,
        durationWeeks: Int
    ): ParticipationResult {
        val response = api.participate(
            challengeId = challengeId,
            body = ParticipationRequestDto(depositAmount = depositAmount, durationWeeks = durationWeeks)
        )
        return response.result.toModel()
    }
}
