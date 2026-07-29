package com.example.onuldo_fe.model.party

// 파티 전체의 정산 완료 상태
enum class PartySettlementStatus {
    AllSuccess,
    PartialSuccess,
    AllFailed
}

// 파티원 개인의 챌린지 완주 상태
enum class PartySettlementMemberStatus {
    Completed,
    Incomplete
}

// 화면과 비즈니스 로직에서 사용하는 파티 정산 결과
data class PartySettlementResult(
    val partyId: Long,
    val status: PartySettlementStatus,
    val title: String,
    val description: String,
    val refundAmount: Int,
    val adjustmentAmount: Int,
    val members: List<PartySettlementMember>
) {
    val completedMemberCount: Int
        get() = members.count { it.status == PartySettlementMemberStatus.Completed }
}

// 파티원별 정산 결과와 프로필 정보
data class PartySettlementMember(
    val memberId: String,
    val name: String,
    val profileImageUrl: String?,
    val defaultCharacterId: Int?,
    val status: PartySettlementMemberStatus,
    val adjustmentAmount: Int
)
