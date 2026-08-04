package com.example.onuldo_fe.repository.term

import com.example.onuldo_fe.data.network.NetworkModule
import com.example.onuldo_fe.data.term.api.TermApi

object TermRepositoryProvider {

    private val termApi: TermApi by lazy { NetworkModule.create(TermApi::class.java) }

    private val repository: TermRepository by lazy { TermRepositoryImpl(termApi) }

    fun provide(): TermRepository = repository
}
