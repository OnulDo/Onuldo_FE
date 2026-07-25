package com.example.onuldo_fe.repository.challenge

import com.example.onuldo_fe.ui.screen.challenge.gallery.Challenge

interface ChallengeRepository {
    // 챌린지 갤러리 목록
    fun getChallenges(): List<Challenge>

    // id로 단일 챌린지 조회 (상세/참여/시작 화면에서 사용)
    fun getChallengeById(id: Int): Challenge
}
