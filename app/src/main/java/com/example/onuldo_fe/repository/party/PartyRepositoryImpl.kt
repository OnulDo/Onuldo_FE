package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.PartyApi
import com.example.onuldo_fe.data.party.dto.CreatePartyRequestDto
import com.example.onuldo_fe.data.party.dto.PartyMemberDto
import com.example.onuldo_fe.data.party.dto.PartySummaryDto
import com.example.onuldo_fe.data.party.dto.PartyWaitingRoomDto
import com.example.onuldo_fe.data.party.dto.PartySettlementResultDto
import com.example.onuldo_fe.model.party.CreatePartyCommand
import com.example.onuldo_fe.model.party.CreatedParty
import com.example.onuldo_fe.model.party.PartyLifecycleStatus
import com.example.onuldo_fe.model.party.PartyMember
import com.example.onuldo_fe.model.party.PartyMemberReadyStatus
import com.example.onuldo_fe.model.party.PartyRole
import com.example.onuldo_fe.model.party.PartySummary
import com.example.onuldo_fe.model.party.PartyWaitingRoom
import com.example.onuldo_fe.model.party.PartySettlementMember
import com.example.onuldo_fe.model.party.PartySettlementMemberStatus
import com.example.onuldo_fe.model.party.PartySettlementResult
import com.example.onuldo_fe.model.party.PartySettlementStatus

// 파티 생성·대기방 API 요청과 DTO의 도메인 모델 변환 담당
class PartyRepositoryImpl(private val api: PartyApi) : PartyRepository {
    // 서버의 진행 중 파티 응답 목록을 도메인 요약 모델 목록으로 변환
    override suspend fun getParties(): List<PartySummary> = api.getParties().map(PartySummaryDto::toModel)

    override suspend fun createParty(command: CreatePartyCommand): CreatedParty {
        // 화면에서 조합한 생성 명령을 서버 요청 DTO로 변환
        val response = api.createParty(
            CreatePartyRequestDto(
                name = command.name,
                challengeId = command.challengeId,
                challengeName = command.challengeName,
                period = command.period,
                deposit = command.deposit,
                capacity = command.capacity
            )
        )
        // 화면 이동에 생성된 파티 ID와 초대코드만 노출
        return CreatedParty(response.partyId, response.inviteCode)
    }

    override suspend fun getWaitingRoom(partyId: String): PartyWaitingRoom =
        api.getWaitingRoom(partyId).toModel()

    override suspend fun readyParty(partyId: String): PartyWaitingRoom =
        api.readyParty(partyId).toModel()

    override suspend fun leaveParty(partyId: String) = api.leaveParty(partyId)

    override suspend fun startParty(partyId: String): PartySummary =
        api.startParty(partyId).toModel()

    override suspend fun getSettlementResult(partyId: String): PartySettlementResult =
        api.getSettlementResult(partyId).toModel()
}

// 서버 정산 상태와 파티원 결과를 앱에서 사용하는 도메인 모델로 변환
internal fun PartySettlementResultDto.toModel() = PartySettlementResult(
    partyId = partyId,
    status = when (overallStatus) {
        "ALL_SUCCESS" -> PartySettlementStatus.AllSuccess
        "PARTIAL_SUCCESS" -> PartySettlementStatus.PartialSuccess
        "ALL_FAILED" -> PartySettlementStatus.AllFailed
        else -> error("Unsupported settlement status: $overallStatus")
    },
    title = overallTitle,
    description = overallDescription,
    refundAmount = myResult.depositRefundAmount,
    adjustmentAmount = myResult.bonusAmount,
    members = memberResults.map { member ->
        PartySettlementMember(
            memberId = member.memberId,
            name = member.name,
            profileImageUrl = member.profileImageUrl,
            defaultCharacterId = member.defaultCharacterId,
            status = if (member.isSuccess) {
                PartySettlementMemberStatus.Completed
            } else {
                PartySettlementMemberStatus.Incomplete
            },
            adjustmentAmount = member.bonusAmount
        )
    }
)

// 대기방 응답과 중첩된 파티원 DTO를 도메인 모델로 함께 변환
private fun PartyWaitingRoomDto.toModel() = PartyWaitingRoom(
    partyId = partyId,
    partyName = partyName,
    challengeName = challengeName,
    inviteCode = inviteCode,
    period = period,
    deposit = deposit,
    capacity = capacity,
    members = members.map(PartyMemberDto::toModel)
)

// 서버의 역할·준비 상태 문자열을 앱 내부 enum으로 변환
private fun PartyMemberDto.toModel() = PartyMember(
    id = id,
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    defaultCharacterId = defaultCharacterId,
    role = PartyRole.valueOf(role.lowercase().replaceFirstChar(Char::uppercase)),
    readyStatus = when (readyStatus) {
        "READY" -> PartyMemberReadyStatus.Ready
        "WAITING" -> PartyMemberReadyStatus.Waiting
        else -> PartyMemberReadyStatus.NotApplicable
    },
    joinedOrder = joinedOrder
)

// 알 수 없는 상태는 모집 중으로 처리하고 알려진 상태만 명시적으로 변환
private fun PartySummaryDto.toModel() = PartySummary(
    partyId = partyId,
    partyName = partyName,
    challengeName = challengeName,
    dDay = dDay,
    deadline = deadline,
    remainingText = remainingText,
    completedMemberCount = completedMemberCount,
    totalMemberCount = totalMemberCount,
    status = when (status) {
        "IN_PROGRESS" -> PartyLifecycleStatus.InProgress
        "DISBANDED" -> PartyLifecycleStatus.Disbanded
        else -> PartyLifecycleStatus.Recruiting
    }
)
