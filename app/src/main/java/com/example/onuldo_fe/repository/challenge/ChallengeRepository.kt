package com.example.onuldo_fe.repository.challenge

import com.example.onuldo_fe.data.network.ApiResult
import com.example.onuldo_fe.model.challenge.ChallengeCategory
import com.example.onuldo_fe.model.challenge.ChallengeDetail
import com.example.onuldo_fe.model.challenge.ChallengePage
import com.example.onuldo_fe.model.challenge.ParticipationResult

// 결과를 예외가 아닌 [ApiResult]로 돌려줘, ViewModel이 성공/서버실패/네트워크오류를 구분해 다룰 수 있게 한다.
interface ChallengeRepository {
    // 챌린지 목록 조회 — 오프셋 페이지네이션 + 카테고리/검색어 필터
    suspend fun getChallenges(
        page: Int,
        size: Int,
        category: ChallengeCategory?,
        search: String?
    ): ApiResult<ChallengePage>

    // 챌린지 상세 조회 — 활성 상태 챌린지 1건
    suspend fun getChallengeDetail(challengeId: Long): ApiResult<ChallengeDetail>

    // 개인 챌린지 참여 생성
    suspend fun participate(
        challengeId: Long,
        depositAmount: Int,
        durationWeeks: Int
    ): ApiResult<ParticipationResult>
}
