package com.example.onuldo_fe.model.party

enum class PartyJoinError { Invalid, AlreadyStarted, Full, Expired }

sealed interface PartyJoinResult {
    data class Success(val partyId: String) : PartyJoinResult
    data class Failure(val error: PartyJoinError) : PartyJoinResult
}
