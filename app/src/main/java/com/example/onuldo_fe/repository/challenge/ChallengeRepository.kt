package com.example.onuldo_fe.repository.challenge

import com.example.onuldo_fe.model.challenge.ChallengeCategory
import com.example.onuldo_fe.model.challenge.ChallengePage

interface ChallengeRepository {
    // 챌린지 목록 조회 — 오프셋 페이지네이션 + 카테고리/검색어 필터
    suspend fun getChallenges(
        page: Int,
        size: Int,
        category: ChallengeCategory?,
        search: String?
    ): ChallengePage
}
