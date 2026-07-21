package com.example.onuldo_fe.ui.screen.home.data.repository

import com.example.onuldo_fe.ui.screen.home.model.HomeData

interface HomeRepository {
    // 홈 API 한 번의 응답을 단일 데이터로 전달
    fun getHome(): HomeData
}
