package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.PartyApi
import com.example.onuldo_fe.data.party.api.RealPartyApi
import com.example.onuldo_fe.data.party.dto.CreatePartyRequestDto
import com.example.onuldo_fe.data.party.dto.PartyMemberDto
import com.example.onuldo_fe.data.party.dto.RealPartySummaryDto
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
import retrofit2.HttpException
import java.io.IOException

// 파티 생성·대기방 API 요청과 DTO의 도메인 모델 변환 담당
class PartyRepositoryImpl(
    private val fakeApi: PartyApi,
    private val realApi: RealPartyApi,
    private val useRealPartyListApi: Boolean
) : PartyRepository {
    // 서버의 진행 중 파티 응답 목록을 도메인 요약 모델 목록으로 변환
    override suspend fun getParties(): List<PartySummary> = if (useRealPartyListApi) {
        val response = realApi.getParties()
        if (!response.isSuccessful) throw HttpException(response)
        val body = response.body() ?: throw IOException("파티 목록 응답 본문이 비어 있습니다.")
        body.result.map(RealPartySummaryDto::toModel)
    } else {
        fakeApi.getParties().map(PartySummaryDto::toModel)
    }

    override suspend fun createParty(command: CreatePartyCommand): CreatedParty {
        // 화면에서 조합한 생성 명령을 서버 요청 DTO로 변환
        val response = fakeApi.createParty(
            CreatePartyRequestDto(
                name = command.name,
                challengeId = command.challengeId.toLong(),
                durationDays = command.period.filter(Char::isDigit).toInt() * 7,
                depositAmount = command.deposit,
                maxMembers = command.capacity
            )
        )
        // 화면 이동에 생성된 파티 ID와 초대코드만 노출
        return CreatedParty(response.partyId.toString(), response.inviteCode)
    }

    override suspend fun getWaitingRoom(partyId: String): PartyWaitingRoom =
        fakeApi.getWaitingRoom(partyId.toLong()).toModel()

    override suspend fun readyParty(partyId: String): PartyWaitingRoom =
        fakeApi.readyParty(partyId.toLong()).toModel()

    override suspend fun leaveParty(partyId: String) = fakeApi.leaveParty(partyId.toLong())

    override suspend fun startParty(partyId: String) {
        fakeApi.startParty(partyId.toLong())
    }

    override suspend fun getSettlementResult(partyId: Long): PartySettlementResult =
        fakeApi.getSettlementResult(partyId).toModel()
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
            userId = member.userId,
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
    partyId = partyId.toString(),
    partyName = name,
    // 대기방 응답에 목표가 없으면 화면에서 빈 값으로 처리
    challengeName = goal.orEmpty(),
    inviteCode = inviteCode,
    period = "${durationDays}일",
    deposit = depositAmount,
    capacity = maxMembers,
    members = members.mapIndexed { index, member -> member.toModel(index) }
)

// 서버의 역할·준비 상태 문자열을 앱 내부 enum으로 변환
private fun PartyMemberDto.toModel(index: Int) = PartyMember(
    id = userId.toString(),
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    // TODO: 서버가 기본 캐릭터 정보를 제공하면 userId 기반 임시 배정을 제거한다.
    defaultCharacterId = ((userId % 9) + 1).toInt(),
    role = if (role == "HOST") PartyRole.Leader else PartyRole.Member,
    readyStatus = when (status) {
        "READY" -> PartyMemberReadyStatus.Ready
        "WAITING" -> PartyMemberReadyStatus.Waiting
        else -> PartyMemberReadyStatus.NotApplicable
    },
    // 서버가 방장 승계를 처리하므로 화면 정렬용으로 응답 순서만 유지한다.
    joinedOrder = index
)

// 알 수 없는 상태는 모집 중으로 처리하고 알려진 상태만 명시적으로 변환
private fun PartySummaryDto.toModel() = PartySummary(
    partyId = partyId.toString(),
    partyName = name,
    // API 명세의 goal을 화면에서 사용하는 챌린지명으로 변환
    challengeName = goal,
    dDay = "D-$dDay",
    // 마감 시간은 API에서 제공하지 않을 수 있으므로 nullable 응답을 빈 값으로 변환
    deadline = deadline.orEmpty(),
    remainingText = null,
    completedMemberCount = verifiedToday,
    totalMemberCount = totalMembers,
    status = when (status) {
        "ONGOING" -> PartyLifecycleStatus.InProgress
        "DISBANDED" -> PartyLifecycleStatus.Disbanded
        else -> PartyLifecycleStatus.Recruiting
    }
)

/**
 * 현재 목록 API에는 챌린지명과 인증 마감 시간이 없으므로 빈 값으로 유지한다.
 * 종료 상태는 현재 화면의 진행 중 목록에서 제외되도록 Disbanded로 변환한다.
 */
private fun RealPartySummaryDto.toModel() = PartySummary(
    partyId = partyId.toString(),
    partyName = name,
    challengeName = "",
    dDay = "D-$dDay",
    deadline = "",
    remainingText = null,
    completedMemberCount = verifiedToday,
    totalMemberCount = totalMembers,
    status = if (status == "ONGOING") {
        PartyLifecycleStatus.InProgress
    } else {
        PartyLifecycleStatus.Disbanded
    }
)
