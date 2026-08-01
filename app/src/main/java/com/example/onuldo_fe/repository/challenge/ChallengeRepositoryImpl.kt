package com.example.onuldo_fe.repository.challenge

import com.example.onuldo_fe.data.challenge.api.ChallengeApi
import com.example.onuldo_fe.data.challenge.mapper.toApiValue
import com.example.onuldo_fe.data.challenge.mapper.toDetailModel
import com.example.onuldo_fe.data.challenge.mapper.toModel
import com.example.onuldo_fe.model.challenge.ChallengeCategory
import com.example.onuldo_fe.model.challenge.ChallengeDetail
import com.example.onuldo_fe.model.challenge.ChallengePage

// 챌린지 목록 API 요청과 DTO → 도메인 모델 변환 담당 (party RepositoryImpl과 동일 패턴)
class ChallengeRepositoryImpl(private val api: ChallengeApi) : ChallengeRepository {
    override suspend fun getChallenges(
        page: Int,
        size: Int,
        category: ChallengeCategory?,
        search: String?
    ): ChallengePage {
        val response = api.getChallenges(
            page = page,
            size = size,
            category = category?.toApiValue(),          // 앱 enum → API category 문자열
            search = search?.takeIf { it.isNotBlank() }  // 공백이면 s 미전송 (빈목록 처리는 ViewModel 담당)
        )
        return response.result.toModel()   // 공통 래퍼(ApiResponse)에서 result만 도메인으로 변환
    }

    override suspend fun getChallengeDetail(challengeId: Long): ChallengeDetail {
        val response = api.getChallengeDetail(challengeId)
        return response.result.toDetailModel()
    }
}
