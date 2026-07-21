package com.example.onuldo_fe.ui.screen.home.data.repository

import com.example.onuldo_fe.ui.screen.home.model.HomeChallenge
import com.example.onuldo_fe.ui.screen.home.model.HomeCompletedChallenge
import com.example.onuldo_fe.ui.screen.home.model.HomePartyChallenge
import com.example.onuldo_fe.ui.screen.home.model.TodayChallenge
import com.example.onuldo_fe.ui.screen.home.model.SettlementBanner

interface HomeRepository {
    fun getUserName(): String
    fun getTodayChallenge(): TodayChallenge?
    fun getPartyChallenges(): List<HomePartyChallenge>
    fun getChallenges(): List<HomeChallenge>
    fun getCompletedChallenges(): List<HomeCompletedChallenge>
    fun getSettlementBanner(): SettlementBanner?
}
