package com.example.onuldo_fe.repository.record

import com.example.onuldo_fe.model.record.CompletedRecordSummary
import com.example.onuldo_fe.model.record.OngoingChallenge

interface RecordRepository {
    suspend fun getOngoingChallenges(): List<OngoingChallenge>
    suspend fun getCompletedChallenges(): CompletedRecordSummary
}