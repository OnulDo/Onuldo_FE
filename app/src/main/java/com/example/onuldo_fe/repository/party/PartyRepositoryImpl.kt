package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.PartyApi
import com.example.onuldo_fe.data.party.api.RealPartyApi
import com.example.onuldo_fe.data.party.dto.CreatePartyRequestDto
import com.example.onuldo_fe.data.party.dto.PartyMemberDto
import com.example.onuldo_fe.data.party.dto.RealPartySummaryDto
import com.example.onuldo_fe.data.party.dto.RealPartyMemberDto
import com.example.onuldo_fe.data.party.dto.RealPartyWaitingRoomDto
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
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

// 파티 생성·대기방 API 요청과 DTO의 도메인 모델 변환 담당
class PartyRepositoryImpl(
    private val fakeApi: PartyApi,
    private val realApi: RealPartyApi,
    private val useRealPartyListApi: Boolean,
    private val useRealPartyWaitingRoomApi: Boolean = false,
    private val useRealPartyCreateApi: Boolean = false,
    private val useRealPartyReadyApi: Boolean = false,
    private val useRealPartyStartApi: Boolean = false
) : PartyRepository {
    // 서버의 진행 중 파티 응답 목록을 도메인 요약 모델 목록으로 변환
    override suspend fun getParties(): List<PartySummary> = if (useRealPartyListApi) {
        // 현재 화면은 첫 페이지 10개를 표시하며, 이후 무한 스크롤 적용 시 nextCursor를 사용한다.
        val response = realApi.getParties(cursor = null, size = 10)
        if (!response.isSuccessful) throw HttpException(response)
        val body = response.body() ?: throw IOException("파티 목록 응답 본문이 비어 있습니다.")
        body.content.map(RealPartySummaryDto::toModel)
    } else {
        fakeApi.getParties().map(PartySummaryDto::toModel)
    }

    override suspend fun createParty(command: CreatePartyCommand): CreatedParty {
        // Fake와 Real이 동일한 JSON Body를 사용하도록 요청 변환은 한 번만 수행한다.
        val request = command.toCreateRequestDto()
        val result = if (useRealPartyCreateApi) {
            val response = realApi.createParty(request)
            if (!response.isSuccessful) throw HttpException(response)
            response.body()?.result ?: throw IOException("파티 생성 응답 본문이 비어 있습니다.")
        } else {
            fakeApi.createParty(request)
        }

        // 이후 화면은 생성된 ID로 대기방을 다시 조회하므로 필요한 식별값만 전달한다.
        return CreatedParty(result.partyId.toString(), result.inviteCode)
    }

    override suspend fun getWaitingRoom(partyId: String): PartyWaitingRoom {
        // 대기방 조회만 독립적으로 Real/Fake를 바꿔 다른 파티 기능에 영향을 주지 않는다.
        if (!useRealPartyWaitingRoomApi) {
            return fakeApi.getWaitingRoom(partyId.toLong()).toModel()
        }

        val response = realApi.getWaitingRoom(partyId.toLong())
        if (!response.isSuccessful) throw HttpException(response)
        val body = response.body() ?: throw IOException("파티 대기방 응답 본문이 비어 있습니다.")
        return body.result.toModel()
    }

    override suspend fun readyParty(partyId: String): PartyWaitingRoom {
        // 준비 완료만 독립적으로 전환해 아직 Fake인 시작·이탈 API에 영향을 주지 않는다.
        if (!useRealPartyReadyApi) return fakeApi.readyParty(partyId.toLong()).toModel()

        val response = realApi.readyParty(partyId.toLong())
        if (!response.isSuccessful) throw HttpException(response)
        val body = response.body() ?: throw IOException("준비 완료 응답 본문이 비어 있습니다.")
        return body.result.toModel()
    }

    override suspend fun leaveParty(partyId: String) = fakeApi.leaveParty(partyId.toLong())

    override suspend fun startParty(partyId: String) {
        if (!useRealPartyStartApi) {
            fakeApi.startParty(partyId.toLong())
            return
        }

        // 성공 응답 본문까지 확인한 뒤에만 ViewModel이 홈 화면으로 이동하도록 완료 처리한다.
        val response = realApi.startParty(partyId.toLong())
        if (!response.isSuccessful) throw HttpException(response)
        response.body()?.result ?: throw IOException("파티 시작 응답 본문이 비어 있습니다.")
        // TODO: 도전금 차감 실패 code가 명세되면 포인트 부족 오류로 변환한다.
    }

    override suspend fun getSettlementResult(partyId: Long): PartySettlementResult =
        fakeApi.getSettlementResult(partyId).toModel()
}

/** 화면의 주 단위 기간을 Swagger의 durationWeeks 값으로 그대로 전달한다. */
private fun CreatePartyCommand.toCreateRequestDto() = CreatePartyRequestDto(
    name = name,
    challengeId = challengeId.toLong(),
    durationWeeks = period.filter(Char::isDigit).toInt(),
    depositAmount = deposit,
    maxMembers = capacity
)

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
internal fun PartyWaitingRoomDto.toModel() = PartyWaitingRoom(
    partyId = partyId.toString(),
    partyName = name,
    inviteCode = inviteCode,
    period = "${durationDays}일",
    deposit = depositAmount,
    capacity = maxMembers,
    members = members.mapIndexed { index, member -> member.toModel(index) },
    isHost = isHost,
    canStart = canStart,
    status = status.toLifecycleStatus()
)

/** 실제 대기방 응답을 화면과 분리된 도메인 모델로 변환한다. */
internal fun RealPartyWaitingRoomDto.toModel() = PartyWaitingRoom(
    partyId = partyId.toString(),
    partyName = name,
    inviteCode = inviteCode,
    period = "${durationDays}일",
    deposit = depositAmount,
    capacity = maxMembers,
    members = members.mapIndexed { index, member -> member.toModel(index) },
    // 클라이언트 추측값 대신 서버가 로그인 사용자 기준으로 계산한 값을 전달한다.
    isHost = isHost,
    canStart = canStart,
    status = status.toLifecycleStatus()
)

/** 대기방 응답의 서버 상태를 앱 공통 파티 상태로 변환한다. */
private fun String.toLifecycleStatus() = when (this) {
    "ONGOING" -> PartyLifecycleStatus.InProgress
    "FINISHED", "DISBANDED" -> PartyLifecycleStatus.Disbanded
    else -> PartyLifecycleStatus.Recruiting
}

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

private fun RealPartyMemberDto.toModel(index: Int) = PartyMember(
    id = userId.toString(),
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    defaultCharacterId = ((userId % 9) + 1).toInt(),
    role = if (role == "HOST") PartyRole.Leader else PartyRole.Member,
    readyStatus = when (status) {
        "READY" -> PartyMemberReadyStatus.Ready
        "WAITING" -> PartyMemberReadyStatus.Waiting
        else -> PartyMemberReadyStatus.NotApplicable
    },
    joinedOrder = index
)

/** 최신 목록 응답을 파티 카드에서 사용하는 값으로 변환한다. */
private fun RealPartySummaryDto.toModel() = PartySummary(
    partyId = partyId.toString(),
    partyName = name,
    challengeName = challengeTitle,
    // 서버 종료일과 오늘 날짜의 차이를 카드의 D-Day 문구로 변환한다.
    dDay = "D-${daysUntil(endDate)}",
    deadline = verificationDeadline,
    // 인증 마감 시각과 현재 시각의 차이를 분 단위로 전달한다.
    // HomePartyCard에서 0~60분일 때만 "N분 남음" 배지를 표시한다.
    remainingText = remainingTextUntil(verificationDeadline),
    completedMemberCount = verifiedMemberCount,
    totalMemberCount = totalMemberCount,
    status = if (status == "ONGOING") {
        PartyLifecycleStatus.InProgress
    } else {
        PartyLifecycleStatus.Disbanded
    }
)

private fun daysUntil(endDate: String): Long = runCatching {
    ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(endDate)).coerceAtLeast(0)
}.getOrDefault(0)

private fun remainingTextUntil(deadline: String): String? = runCatching {
    ChronoUnit.MINUTES.between(LocalTime.now(), LocalTime.parse(deadline))
        .takeIf { it >= 0 }
        ?.let { "${it}분 남음" }
}.getOrNull()
