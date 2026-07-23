package com.example.onuldo_fe.model.party

data class PartyFeedItem(
    val memberId: String,
    val nickname: String,
    val profileImageUrl: String,
    val verificationImageUrl: String?,
    val verifiedElapsedMinutes: Int?
)
