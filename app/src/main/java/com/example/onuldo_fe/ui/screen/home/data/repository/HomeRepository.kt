package com.example.onuldo_fe.ui.screen.home.data.repository

import com.example.onuldo_fe.ui.screen.home.model.HomeChallenge
import com.example.onuldo_fe.ui.screen.home.model.TodayChallenge

interface HomeRepository {
    fun getTodayChallenge(): TodayChallenge?
    fun getChallenges(): List<HomeChallenge>
}
