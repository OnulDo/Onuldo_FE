package com.example.onuldo_fe.repository.record

import com.example.onuldo_fe.data.party.network.PartyNetworkClient
import com.example.onuldo_fe.data.record.api.RecordApi

object RecordRepositoryProvider {
    fun create(): RecordRepository {
        val api = PartyNetworkClient.create(RecordApi::class.java)
        return RecordRepositoryImpl(api)
    }
}