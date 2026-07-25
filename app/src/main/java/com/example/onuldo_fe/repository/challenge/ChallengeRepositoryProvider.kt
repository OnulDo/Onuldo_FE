package com.example.onuldo_fe.repository.challenge

object ChallengeRepositoryProvider {
    // 실제 API 주입 전 기본 구현체 사용 (홈 도메인 Provider 패턴과 동일)
    fun provide(): ChallengeRepository = ChallengeRepositoryImpl()
}
