package com.example.onuldo_fe.data.party.dummy

object FakePartyInviteState {
    private val startedCodes = mutableSetOf<String>()
    private val expiredCodes = mutableSetOf<String>()
    private val activeCodes = mutableSetOf<String>()

    fun markStarted(code: String) {
        startedCodes += code.uppercase()
        expiredCodes -= code.uppercase()
        activeCodes -= code.uppercase()
    }

    fun markExpired(code: String) {
        expiredCodes += code.uppercase()
        startedCodes -= code.uppercase()
        activeCodes -= code.uppercase()
    }

    fun activate(code: String) {
        startedCodes -= code.uppercase()
        expiredCodes -= code.uppercase()
        activeCodes += code.uppercase()
    }

    fun isStarted(code: String) = code.uppercase() in startedCodes
    fun isExpired(code: String) = code.uppercase() in expiredCodes
    fun isActive(code: String) = code.uppercase() in activeCodes
}
