package com.example.onuldo_fe.repository.home

import com.example.onuldo_fe.model.home.HomeData

interface HomeRepository {
    // 홈 API 한 번의 응답을 단일 데이터로 전달
    fun getHome(): HomeData
}
