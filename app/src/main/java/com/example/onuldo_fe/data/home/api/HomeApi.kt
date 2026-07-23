package com.example.onuldo_fe.data.home.api

import com.example.onuldo_fe.data.home.dto.HomeResponseDto

interface HomeApi {
    fun getHome(): HomeResponseDto
}
