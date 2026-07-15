package com.example.onuldo_fe.ui.screen.home.data.api

import com.example.onuldo_fe.ui.screen.home.data.dto.HomeResponseDto

interface HomeApi {
    fun getHome(): HomeResponseDto
}
