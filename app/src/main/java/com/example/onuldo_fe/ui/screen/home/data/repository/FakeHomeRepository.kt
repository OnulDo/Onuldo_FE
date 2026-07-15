package com.example.onuldo_fe.ui.screen.home.data.repository

import com.example.onuldo_fe.ui.screen.home.data.api.FakeHomeApi
import com.example.onuldo_fe.ui.screen.home.data.api.HomeApi
import com.example.onuldo_fe.ui.screen.home.model.HomeChallenge
import com.example.onuldo_fe.ui.screen.home.model.TodayChallenge

class FakeHomeRepository(
    private val homeApi: HomeApi = FakeHomeApi()
) : HomeRepository {
    private val repository = HomeRepositoryImpl(homeApi.getHome())

    override fun getTodayChallenge(): TodayChallenge? {
        return repository.getTodayChallenge()
    }

    override fun getChallenges(): List<HomeChallenge> {
        return repository.getChallenges()
    }
}
