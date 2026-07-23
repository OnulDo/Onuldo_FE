package com.example.onuldo_fe.data.party.dto

data class PartyFeedItemDto(
    val memberId: String,
    val nickname: String,
    val profileImageUrl: String,
    val verificationImageUrl: String?,
    val verifiedElapsedMinutes: Int?
)
