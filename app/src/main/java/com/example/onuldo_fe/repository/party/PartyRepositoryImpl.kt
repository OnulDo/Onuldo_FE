package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.RealPartyApi
import com.example.onuldo_fe.data.party.dto.CreatePartyRequestDto
import com.example.onuldo_fe.data.party.dto.PartyReadinessRequestDto
import com.example.onuldo_fe.data.party.dto.RealPartySummaryDto
import com.example.onuldo_fe.data.party.dto.RealPartyMemberDto
import com.example.onuldo_fe.data.party.dto.RealPartyWaitingRoomDto
import com.example.onuldo_fe.data.party.dto.PartySettlementResultDto
import com.example.onuldo_fe.model.party.CreatePartyCommand
import com.example.onuldo_fe.model.party.CreatedParty
import com.example.onuldo_fe.model.party.PartyLifecycleStatus
import com.example.onuldo_fe.model.party.PartyMember
import com.example.onuldo_fe.model.party.PartyMemberReadyStatus
import com.example.onuldo_fe.model.party.PartyRole
import com.example.onuldo_fe.model.party.PartySummary
import com.example.onuldo_fe.model.party.PartySummaryMember
import com.example.onuldo_fe.model.party.PartyVerificationStatus
import com.example.onuldo_fe.model.party.PartyWaitingRoom
import com.example.onuldo_fe.model.party.PartySettlementMember
import com.example.onuldo_fe.model.party.PartySettlementMemberStatus
import com.example.onuldo_fe.model.party.PartySettlementResult
import com.example.onuldo_fe.model.party.PartySettlementStatus
import com.google.gson.JsonElement
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalTime
import java.time.temporal.ChronoUnit

// 파티 목록·생성·대기방·이탈·시작·정산 API와 도메인 변환 담당
class PartyRepositoryImpl(
    private val realApi: RealPartyApi
) : PartyRepository {
    // 서버의 진행 중 파티 응답 목록을 도메인 요약 모델 목록으로 변환
    override suspend fun getParties(): List<PartySummary> {
        // 현재 화면은 첫 페이지 10개를 표시하며, 이후 무한 스크롤 적용 시 nextCursor를 사용한다.
        val response = realApi.getParties(cursor = null, size = 10)
        if (!response.isSuccessful) throw HttpException(response)
        val body = response.body() ?: throw IOException("파티 목록 응답 본문이 비어 있습니다.")
        return body.result.map(RealPartySummaryDto::toModel)
    }

    override suspend fun createParty(command: CreatePartyCommand): CreatedParty {
        val request = command.toCreateRequestDto()
        val response = realApi.createParty(request)
        if (!response.isSuccessful) throw HttpException(response)
        val result = response.body()?.result ?: throw IOException("파티 생성 응답 본문이 비어 있습니다.")

        // 이후 화면은 생성된 ID로 대기방을 다시 조회하므로 필요한 식별값만 전달한다.
        return CreatedParty(result.partyId.toString(), result.inviteCode)
    }

    override suspend fun getWaitingRoom(partyId: String): PartyWaitingRoom {
        val response = realApi.getWaitingRoom(partyId.toLong())
        if (!response.isSuccessful) throw HttpException(response)
        val body = response.body() ?: throw IOException("파티 대기방 응답 본문이 비어 있습니다.")
        return body.result.toModel()
    }

    override suspend fun readyParty(partyId: String, ready: Boolean): PartyWaitingRoom {
        val response = realApi.readyParty(
            partyId = partyId.toLong(),
            request = PartyReadinessRequestDto(ready = ready)
        )
        if (!response.isSuccessful) throw HttpException(response)
        val body = response.body() ?: throw IOException("준비 완료 응답 본문이 비어 있습니다.")
        return body.result.toModel()
    }

    override suspend fun leaveParty(partyId: String) {
        // 실제 이탈 성공 응답을 확인한 후에만 ViewModel이 대기방을 닫도록 한다.
        val response = realApi.leaveParty(partyId.toLong())
        if (!response.isSuccessful) throw HttpException(response)
        response.body()?.result ?: throw IOException("파티 이탈 응답 본문이 비어 있습니다.")
    }

    override suspend fun startParty(partyId: String) {
        // 성공 응답 본문까지 확인한 뒤에만 ViewModel이 홈 화면으로 이동하도록 완료 처리한다.
        val response = realApi.startParty(partyId.toLong())
        if (!response.isSuccessful) throw HttpException(response)
        response.body()?.result ?: throw IOException("파티 시작 응답 본문이 비어 있습니다.")
    }

    override suspend fun getSettlementResult(partyId: Long): PartySettlementResult {
        // 결과 조회 성공 시 서버가 홈 정산 배너도 확인 처리한다.
        val response = realApi.getSettlementResult(partyId)
        if (!response.isSuccessful) throw HttpException(response)
        val body = response.body() ?: throw IOException("파티 정산 결과 응답 본문이 비어 있습니다.")
        return body.result.toModel()
    }
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
internal fun PartySettlementResultDto.toModel(): PartySettlementResult {
    val settlementStatus = when (resultType) {
        "ALL_SUCCESS" -> PartySettlementStatus.AllSuccess
        "PARTIAL_SUCCESS" -> PartySettlementStatus.PartialSuccess
        "ALL_FAIL" -> PartySettlementStatus.AllFailed
        else -> error("Unsupported settlement result type: $resultType")
    }
    val memberModels = members.map { member ->
        PartySettlementMember(
            userId = member.userId,
            name = member.nickname,
            profileImageUrl = member.profileImageUrl,
            status = when (member.status) {
                "ONGOING" -> PartySettlementMemberStatus.Ongoing
                "SUCCESS" -> PartySettlementMemberStatus.Success
                "FAIL" -> PartySettlementMemberStatus.Fail
                "CANCELED" -> PartySettlementMemberStatus.Canceled
                else -> error("Unsupported party member settlement status: ${member.status}")
            },
            displayAmount = member.displayAmount
        )
    }
    val completedMemberCount = memberModels.count { it.status == PartySettlementMemberStatus.Success }
    val (title, description) = settlementStatus.temporaryCopy(completedMemberCount)

    return PartySettlementResult(
        partyId = partyId,
        partyName = name,
        status = settlementStatus,
        title = title,
        description = description,
        depositAmount = myDepositAmount,
        displayAmount = myDisplayAmount,
        members = memberModels
    )
}

/** Swagger에 문구가 없어 최종 Figma 확정 전까지 기존 화면 문구를 유지한다. */
private fun PartySettlementStatus.temporaryCopy(completedMemberCount: Int): Pair<String, String> =
    when (this) {
        PartySettlementStatus.AllSuccess ->
            "전원 성공!" to "파티 전원이 챌린지를 완주했어요"

        PartySettlementStatus.PartialSuccess ->
            "${completedMemberCount}명이 완주했어요" to "미완주 파티원의 도전금이 완주자에게 배분됐어요"

        PartySettlementStatus.AllFailed ->
            "아쉽게 실패했어요" to "이번엔 아무도 목표를 채우지 못했어요"
    }

// 대기방 응답과 중첩된 파티원 DTO를 도메인 모델로 함께 변환
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
    "WAITING" -> PartyLifecycleStatus.Recruiting
    "ONGOING" -> PartyLifecycleStatus.InProgress
    "FINISHED", "DISSOLVED" -> PartyLifecycleStatus.Disbanded
    else -> PartyLifecycleStatus.Recruiting
}

// 서버의 역할·준비 상태 문자열을 앱 내부 enum으로 변환
// 알 수 없는 상태는 모집 중으로 처리하고 알려진 상태만 명시적으로 변환
private fun RealPartyMemberDto.toModel(index: Int) = PartyMember(
    id = userId.toString(),
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    defaultCharacterId = null,
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
    challengeId = challengeId,
    partyName = name,
    challengeName = challengeTitle,
    goal = goal,
    // 서버의 정렬·종료일 정책과 동일한 계산 dDay를 그대로 사용한다.
    dDay = "D-$dDay",
    deadline = verificationDeadline.toLocalTimeTextOrNull().orEmpty(),
    // 인증 마감 시각과 현재 시각의 차이를 분 단위로 전달한다.
    // HomePartyCard에서 0~60분일 때만 "N분 남음" 배지를 표시한다.
    remainingText = remainingTextUntil(verificationDeadline.toLocalTimeTextOrNull()),
    completedMemberCount = verifiedMemberCount,
    totalMemberCount = totalMemberCount,
    status = status.toLifecycleStatus(),
    verificationStatus = when (myDailyStatus ?: myStatus) {
        "REVIEW_PENDING", "PENDING" -> PartyVerificationStatus.Pending
        "SUCCESS" -> PartyVerificationStatus.Success
        "FAIL" -> PartyVerificationStatus.Fail
        else -> PartyVerificationStatus.NotVerified
    },
    myDailyStatus = myDailyStatus ?: "WAITING",
    verifiedAt = verifiedAt,
    members = members.map { member ->
        PartySummaryMember(
            userId = member.userId,
            nickname = member.nickname,
            profileImageUrl = member.profileImageUrl,
            isVerifiedToday = member.isVerifiedToday
        )
    }
)

private fun remainingTextUntil(deadline: String?): String? = runCatching {
    if (deadline.isNullOrBlank()) return@runCatching null
    ChronoUnit.MINUTES.between(LocalTime.now(), LocalTime.parse(deadline))
        .takeIf { it >= 0 }
        ?.let { "${it}분 남음" }
}.getOrNull()

private fun JsonElement?.toLocalTimeTextOrNull(): String? {
    if (this == null || isJsonNull) return null
    return runCatching {
        if (isJsonPrimitive) {
            asString
        } else {
            val time = asJsonObject
            val hour = time.get("hour")?.takeIf { it.isJsonPrimitive }?.asInt ?: return@runCatching null
            val minute = time.get("minute")?.takeIf { it.isJsonPrimitive }?.asInt ?: return@runCatching null
            val second = time.get("second")?.takeIf { it.isJsonPrimitive }?.asInt ?: return@runCatching null
            if (hour !in 0..23 || minute !in 0..59 || second !in 0..59) return@runCatching null
            "%02d:%02d:%02d".format(hour, minute, second)
        }
    }.getOrNull()
}
