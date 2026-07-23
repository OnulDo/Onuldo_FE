package com.example.onuldo_fe.data.party.dummy

import com.example.onuldo_fe.data.party.dto.PartyChallengeDto

object PartyChallengeDummyData {
    val challenges = listOf(
        challenge("challenge-1", "morning", "새벽 6시 기상", 1_234, "생활루틴", "세상보다 먼저 눈뜨는 아침 루틴을 만들어요"),
        challenge("challenge-2", "running", "30분 러닝", 682, "피트니스", "매일 30분 달리며 꾸준한 체력을 만들어요"),
        challenge("challenge-3", "reading", "하루 독서 30분", 920, "자기계발", "매일 책을 읽는 집중 시간을 만들어요"),
        challenge("challenge-4", "supplement", "영양제 챙기기", 1_532, "건강", "매일 잊지 않고 건강 습관을 챙겨요"),
        challenge("challenge-5", "words", "영단어 30개", 1_149, "자기계발", "매일 새로운 단어를 익혀 실력을 쌓아요"),
        challenge("challenge-6", "meditation", "명상 10분", 425, "마음챙김", "짧은 명상으로 하루의 마음을 정돈해요")
    )

    private fun challenge(
        id: String,
        imageName: String,
        title: String,
        participantCount: Int,
        category: String,
        summary: String
    ) = PartyChallengeDto(
        id = id,
        imageUrl = "https://example.com/challenges/$imageName.jpg",
        title = title,
        participantCount = participantCount,
        category = category,
        summary = summary,
        benefits = listOf("꾸준한 습관을 만들 수 있어요", "함께 도전하며 동기를 유지할 수 있어요"),
        recommendations = listOf("혼자서는 꾸준히 실천하기 어려운 분", "작은 성공을 매일 쌓고 싶은 분"),
        verificationInstruction = "$title 인증 사진을 촬영해주세요",
        verificationImageUrl = null
    )
}
