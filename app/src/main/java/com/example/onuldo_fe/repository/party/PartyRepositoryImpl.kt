package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.PartyApi
import com.example.onuldo_fe.data.party.dto.CreatePartyRequestDto
import com.example.onuldo_fe.data.party.dto.PartyMemberDto
import com.example.onuldo_fe.data.party.dto.PartySummaryDto
import com.example.onuldo_fe.data.party.dto.PartyWaitingRoomDto
import com.example.onuldo_fe.model.party.CreatePartyCommand
import com.example.onuldo_fe.model.party.CreatedParty
import com.example.onuldo_fe.model.party.PartyLifecycleStatus
import com.example.onuldo_fe.model.party.PartyMember
import com.example.onuldo_fe.model.party.PartyMemberReadyStatus
import com.example.onuldo_fe.model.party.PartyRole
import com.example.onuldo_fe.model.party.PartySummary
import com.example.onuldo_fe.model.party.PartyWaitingRoom

class PartyRepositoryImpl(private val api: PartyApi) : PartyRepository {
    override suspend fun getParties(): List<PartySummary> = api.getParties().map(PartySummaryDto::toModel)

    override suspend fun createParty(command: CreatePartyCommand): CreatedParty {
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
        return CreatedParty(response.partyId, response.inviteCode)
    }

    override suspend fun getWaitingRoom(partyId: String): PartyWaitingRoom =
        api.getWaitingRoom(partyId).toModel()

    override suspend fun readyParty(partyId: String): PartyWaitingRoom =
        api.readyParty(partyId).toModel()

    override suspend fun leaveParty(partyId: String) = api.leaveParty(partyId)

    override suspend fun startParty(partyId: String): PartySummary =
        api.startParty(partyId).toModel()
}

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

private fun PartyMemberDto.toModel() = PartyMember(
    id = id,
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    role = PartyRole.valueOf(role.lowercase().replaceFirstChar(Char::uppercase)),
    readyStatus = when (readyStatus) {
        "READY" -> PartyMemberReadyStatus.Ready
        "WAITING" -> PartyMemberReadyStatus.Waiting
        else -> PartyMemberReadyStatus.NotApplicable
    },
    joinedOrder = joinedOrder
)

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
