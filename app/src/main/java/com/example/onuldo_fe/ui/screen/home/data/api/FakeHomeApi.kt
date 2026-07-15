package com.example.onuldo_fe.ui.screen.home.data.api

import com.example.onuldo_fe.ui.screen.home.data.dto.HomeResponseDto
import com.example.onuldo_fe.ui.screen.home.data.dummy.HomeDummyData

class FakeHomeApi(
    private val hasChallenge: Boolean = true
) : HomeApi {
    override fun getHome(): HomeResponseDto {
        return if (hasChallenge) HomeDummyData.withChallenges else HomeDummyData.empty
    }
}
