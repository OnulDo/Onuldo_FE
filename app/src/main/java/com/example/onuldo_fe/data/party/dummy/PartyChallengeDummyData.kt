package com.example.onuldo_fe.data.party.dummy

import com.example.onuldo_fe.data.party.dto.PartyChallengeDto

object PartyChallengeDummyData {
    val challenges = listOf(
        PartyChallengeDto("challenge-1", "https://example.com/challenges/morning.jpg", "새벽 6시 기상", 1_234, "4주", 10_000),
        PartyChallengeDto("challenge-2", "https://example.com/challenges/running.jpg", "30분 러닝", 682, "4주", 10_000),
        PartyChallengeDto("challenge-3", "https://example.com/challenges/reading.jpg", "하루 독서 30분", 920, "4주", 10_000),
        PartyChallengeDto("challenge-4", "https://example.com/challenges/supplement.jpg", "영양제 챙기기", 1_532, "4주", 10_000),
        PartyChallengeDto("challenge-5", "https://example.com/challenges/words.jpg", "영단어 30개", 1_149, "4주", 10_000),
        PartyChallengeDto("challenge-6", "https://example.com/challenges/meditation.jpg", "명상 10분", 425, "4주", 10_000)
    )
}
