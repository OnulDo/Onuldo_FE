package com.example.onuldo_fe.repository.challenge

import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge

class ChallengeRepositoryImpl : ChallengeRepository {
    // 더미 데이터 — API 명세 확정 후 실제 통신으로 교체
    private val dummyChallenges = listOf(
        Challenge(id = 0, title = "새벽 6시 기상", participantCount = 1234, imageRes = R.drawable.challenge_sample_1),
        Challenge(id = 1, title = "30분 러닝", participantCount = 682, imageRes = R.drawable.challenge_sample_4),
        Challenge(id = 2, title = "하루 독서 30분", participantCount = 920, imageRes = R.drawable.challenge_sample_6),
        Challenge(id = 3, title = "영양제 챙기기", participantCount = 1532, imageRes = R.drawable.challenge_sample_2),
        Challenge(id = 4, title = "영단어 30개", participantCount = 1149, imageRes = R.drawable.challenge_sample_3),
        Challenge(id = 5, title = "명상 10분", participantCount = 425, imageRes = R.drawable.challenge_sample_5)
    )

    override fun getChallenges(): List<Challenge> = dummyChallenges

    override fun getChallengeById(id: Int): Challenge =
        dummyChallenges.firstOrNull { it.id == id } ?: dummyChallenges.first()
}
