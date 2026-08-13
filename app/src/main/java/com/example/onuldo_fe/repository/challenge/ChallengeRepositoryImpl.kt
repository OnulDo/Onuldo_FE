package com.example.onuldo_fe.repository.challenge

import com.example.onuldo_fe.data.challenge.api.ChallengeApi
import com.example.onuldo_fe.data.challenge.dto.ParticipationRequestDto
import com.example.onuldo_fe.data.challenge.mapper.toApiValue
import com.example.onuldo_fe.data.challenge.mapper.toDetailModel
import com.example.onuldo_fe.data.challenge.mapper.toModel
import com.example.onuldo_fe.data.network.ApiResult
import com.example.onuldo_fe.data.network.map
import com.example.onuldo_fe.data.network.safeApiCall
import com.example.onuldo_fe.data.network.safeCursorApiCall
import com.example.onuldo_fe.model.challenge.ChallengeCategory
import com.example.onuldo_fe.model.challenge.ChallengeDetail
import com.example.onuldo_fe.model.challenge.ChallengePage
import com.example.onuldo_fe.model.challenge.ParticipationResult

// 챌린지 API 요청과 DTO → 도메인 모델 변환 담당 (user/party RepositoryImpl과 동일 패턴)
class ChallengeRepositoryImpl(private val api: ChallengeApi) : ChallengeRepository {
    override suspend fun getChallenges(
        page: Int,
        size: Int,
        category: ChallengeCategory?,
        search: String?
    ): ApiResult<ChallengePage> =
        // page는 커서 페이지네이션 전환으로 미사용(인터페이스 호환 위해 파라미터만 유지)
        safeCursorApiCall {
            api.getChallenges(
                size = size,
                category = category?.toApiValue(),          // 앱 enum → API category 문자열
                search = search?.takeIf { it.isNotBlank() }  // 공백이면 미전송 (빈목록 처리는 ViewModel 담당)
            )
        }.map { page ->                                      // 커서 페이지(items/hasNext) → 도메인 페이지
            ChallengePage(
                challenges = page.items.map { it.toModel() },
                page = 0,
                hasNext = page.hasNext,
            )
        }

    override suspend fun getChallengeDetail(challengeId: Long): ApiResult<ChallengeDetail> =
        safeApiCall { api.getChallengeDetail(challengeId) }.map { it.toDetailModel() }

    override suspend fun participate(
        challengeId: Long,
        depositAmount: Int,
        durationWeeks: Int
    ): ApiResult<ParticipationResult> =
        safeApiCall {
            api.participate(
                challengeId = challengeId,
                body = ParticipationRequestDto(depositAmount = depositAmount, durationWeeks = durationWeeks)
            )
        }.map { it.toModel() }
}
