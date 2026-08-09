package com.example.onuldo_fe.model.party

// 파티 전체의 정산 완료 상태
enum class PartySettlementStatus {
    AllSuccess,
    PartialSuccess,
    AllFailed
}

// Swagger의 파티원 정산 상태를 손실 없이 유지한다.
enum class PartySettlementMemberStatus {
    Ongoing,
    Success,
    Fail,
    Canceled
}

// 화면과 비즈니스 로직에서 사용하는 파티 정산 결과
data class PartySettlementResult(
    val partyId: Long,
    val partyName: String,
    val status: PartySettlementStatus,
    val title: String,
    val description: String,
    val depositAmount: Int,
    val displayAmount: Int,
    val members: List<PartySettlementMember>
) {
    val completedMemberCount: Int
        get() = members.count { it.status == PartySettlementMemberStatus.Success }
}

// 파티원별 정산 결과와 프로필 정보
data class PartySettlementMember(
    val userId: Long,
    val name: String,
    val profileImageUrl: String,
    val status: PartySettlementMemberStatus,
    val displayAmount: Int
)
