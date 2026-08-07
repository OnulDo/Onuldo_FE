package com.example.onuldo_fe.data.party.dummy

import com.example.onuldo_fe.data.party.dto.CreatePartyRequestDto
import com.example.onuldo_fe.data.party.dto.CreatePartyResponseDto
import com.example.onuldo_fe.data.party.dto.PartyMemberDto
import com.example.onuldo_fe.data.party.dto.PartyStartResponseDto
import com.example.onuldo_fe.data.party.dto.PartySummaryDto
import com.example.onuldo_fe.data.party.dto.PartyWaitingRoomDto
import com.example.onuldo_fe.model.party.PartyJoinError
import com.example.onuldo_fe.model.party.PartyJoinResult
import java.util.Locale

/** 실제 파티 API 응답 DTO와 같은 형태를 사용하는 메모리 Fake 저장소. */
object FakePartyStore {
    const val CURRENT_USER_ID = 99L
    private var createdPartySequence = 200L
    private val createdPartyIds = mutableSetOf<Long>()

    private data class StoredParty(
        val room: PartyWaitingRoomDto,
        val challengeName: String,
        val status: String,
        val verifiedMemberIds: Set<Long> = emptySet()
    )

    data class FeedSnapshot(
        val partyName: String,
        val challengeName: String,
        val members: List<PartyMemberDto>,
        val verifiedMemberIds: Set<Long>
    )

    private val parties = mutableMapOf(
        1L to StoredParty(
            room = waitingRoom(
                partyId = 1L,
                name = "새벽 러너 파티",
                goal = "30분 러닝",
                inviteCode = "START1",
                durationDays = 12,
                depositAmount = 30_000,
                maxMembers = 5,
                members = listOf(
                    member(1L, "민지", "HOST", "WAITING"),
                    member(2L, "서연", "MEMBER", "READY"),
                    member(3L, "지호", "MEMBER", "READY"),
                    member(4L, "수아", "MEMBER", "READY"),
                    member(CURRENT_USER_ID, "하늘", "MEMBER", "READY")
                )
            ),
            challengeName = "30분 러닝",
            status = "ONGOING",
            verifiedMemberIds = setOf(1L, 2L)
        ),
        2L to StoredParty(
            room = waitingRoom(
                partyId = 2L,
                name = "책상 공부 인증 파티",
                goal = "5시간 집중",
                inviteCode = "START2",
                durationDays = 20,
                depositAmount = 20_000,
                maxMembers = 5,
                members = listOf(
                    member(1L, "민지", "HOST", "WAITING"),
                    member(2L, "서연", "MEMBER", "READY"),
                    member(3L, "지호", "MEMBER", "READY"),
                    member(4L, "수아", "MEMBER", "READY"),
                    member(CURRENT_USER_ID, "하늘", "MEMBER", "READY")
                )
            ),
            challengeName = "5시간 집중",
            status = "ONGOING",
            verifiedMemberIds = setOf(1L, 2L, 3L)
        ),
        101L to StoredParty(
            room = waitingRoom(
                partyId = 101L,
                name = "갓생팟",
                goal = "30일 헬스 챌린지",
                inviteCode = PartyInviteDummyData.VALID_CODE,
                durationDays = 28,
                depositAmount = 30_000,
                maxMembers = 5,
                members = listOf(
                    member(5L, "민지", "HOST", "WAITING"),
                    member(7L, "서연", "MEMBER", "READY")
                )
            ),
            challengeName = "30일 헬스 챌린지",
            status = "WAITING"
        )
    )

    private val summaries = mutableMapOf(
        1L to PartySummaryDto(
            partyId = 1L,
            name = "새벽 러너 파티",
            goal = "30분 러닝",
            deadline = "07:00",
            status = "ONGOING",
            dDay = 12,
            progressRate = 0.4,
            verifiedToday = 2,
            totalMembers = 5
        ),
        2L to PartySummaryDto(
            partyId = 2L,
            name = "책상 공부 인증 파티",
            goal = "5시간 집중",
            deadline = null,
            status = "ONGOING",
            dDay = 20,
            progressRate = 0.6,
            verifiedToday = 3,
            totalMembers = 5
        )
    )

    @Synchronized
    fun create(request: CreatePartyRequestDto): CreatePartyResponseDto {
        val partyId = ++createdPartySequence
        val inviteCode = "P${partyId.toString(36).uppercase(Locale.ROOT).padStart(5, '0').takeLast(5)}"
        val members = buildList {
            add(member(CURRENT_USER_ID, "하늘", "HOST", "WAITING"))
            if (PartyTestConfig.CREATE_READY_TO_START) {
                add(member(100L, "서연", "MEMBER", "READY"))
            }
        }
        val room = waitingRoom(
            partyId = partyId,
            name = request.name,
            goal = fakeChallengeName(request.challengeId),
            inviteCode = inviteCode,
            // 실제 생성 API는 주 단위로 받지만 Fake 대기방 모델은 일 단위를 사용한다.
            durationDays = request.durationWeeks * 7,
            depositAmount = request.depositAmount,
            maxMembers = request.maxMembers,
            members = members
        )
        parties[partyId] = StoredParty(
            room = room,
            challengeName = room.goal.orEmpty(),
            status = "WAITING"
        )
        createdPartyIds += partyId
        return CreatePartyResponseDto(
            partyId = partyId,
            name = request.name,
            inviteCode = inviteCode,
            inviteExpiresAt = "2026-08-20T00:00:00",
            status = "WAITING",
            hostUserId = CURRENT_USER_ID,
            maxMembers = request.maxMembers,
            createdAt = "2026-07-27T12:00:00"
        )
    }

    @Synchronized
    fun join(inviteCode: String): PartyWaitingRoomDto {
        val entry = parties.entries.firstOrNull {
            it.value.room.inviteCode == inviteCode.trim().uppercase(Locale.ROOT)
        }
            ?: throw FakePartyJoinException(PartyJoinError.Invalid)
        val stored = entry.value
        if (stored.status == "ONGOING") throw FakePartyJoinException(PartyJoinError.AlreadyStarted)
        if (stored.status == "DISBANDED") throw FakePartyJoinException(PartyJoinError.Expired)
        if (stored.room.members.size >= stored.room.maxMembers) {
            throw FakePartyJoinException(PartyJoinError.Full)
        }
        var room = stored.room
        if (stored.room.members.none { it.userId == CURRENT_USER_ID }) {
            val updatedMembers = stored.room.members + member(CURRENT_USER_ID, "하늘", "MEMBER", "WAITING")
            room = stored.room.withMembers(updatedMembers)
            parties[entry.key] = stored.copy(room = room)
        }
        return room
    }

    @Synchronized
    fun getRoom(partyId: Long): PartyWaitingRoomDto =
        parties[partyId]?.room ?: error("존재하지 않는 파티입니다.")

    // Fake 피드도 고정 인원 목록이 아닌 해당 파티에 실제 저장된 멤버와 인증 상태를 사용
    @Synchronized
    fun getFeedSnapshot(partyId: Long): FeedSnapshot {
        val stored = parties[partyId] ?: error("존재하지 않는 파티입니다.")
        return FeedSnapshot(
            partyName = stored.room.name,
            challengeName = stored.challengeName,
            members = stored.room.members.toList(),
            verifiedMemberIds = stored.verifiedMemberIds.toSet()
        )
    }

    @Synchronized
    fun ready(partyId: Long): PartyWaitingRoomDto {
        val stored = parties[partyId] ?: error("존재하지 않는 파티입니다.")
        val members = stored.room.members.map { member ->
            if (member.userId == CURRENT_USER_ID && member.role != "HOST") {
                member.copy(status = if (member.status == "READY") "WAITING" else "READY")
            } else member
        }
        val room = stored.room.withMembers(members)
        parties[partyId] = stored.copy(room = room)
        return room
    }

    @Synchronized
    fun leave(partyId: Long) {
        val stored = parties[partyId] ?: return
        val leavingMember = stored.room.members.firstOrNull { it.userId == CURRENT_USER_ID } ?: return
        val remaining = stored.room.members.filterNot { it.userId == CURRENT_USER_ID }.toMutableList()
        if (remaining.isEmpty()) {
            parties[partyId] = stored.copy(room = stored.room.withMembers(emptyList()), status = "DISBANDED")
            summaries.remove(partyId)
            FakePartyFeedState.removeParty(partyId)
            return
        }
        if (leavingMember.role == "HOST") {
            remaining[0] = remaining[0].copy(role = "HOST", status = "WAITING")
        }
        val room = stored.room.withMembers(remaining)
        parties[partyId] = stored.copy(room = room)
        summaries[partyId]?.let { summary ->
            summaries[partyId] = summary.copy(
                verifiedToday = summary.verifiedToday.coerceAtMost(remaining.size),
                totalMembers = remaining.size,
                progressRate = if (remaining.isEmpty()) 0.0 else
                    summary.verifiedToday.coerceAtMost(remaining.size).toDouble() / remaining.size
            )
        }
    }

    @Synchronized
    fun start(partyId: Long): PartyStartResponseDto {
        val stored = parties[partyId] ?: error("존재하지 않는 파티입니다.")
        // 대기 중인 파티만 한 번 시작할 수 있으며 진행·해체 상태의 재시작은 허용하지 않음
        check(stored.status == "WAITING") { "이미 시작되었거나 해체된 파티입니다." }
        val members = stored.room.members
        check(canStart(members)) {
            "모든 파티원이 준비되지 않았습니다."
        }
        parties[partyId] = stored.copy(status = "ONGOING")
        summaries[partyId] = PartySummaryDto(
            partyId = partyId,
            name = stored.room.name,
            goal = stored.challengeName,
            // 실제 API 명세에 마감 시간이 추가되기 전까지 Fake 생성 파티는 값 없음으로 유지
            deadline = null,
            status = "ONGOING",
            dDay = stored.room.durationDays,
            progressRate = 0.0,
            verifiedToday = 0,
            totalMembers = members.size
        )
        FakePartyFeedState.updateParty(partyId, stored.room.name, stored.challengeName, members.size)
        return PartyStartResponseDto(partyId, "ONGOING", "2026-07-27T12:00:00")
    }

    @Synchronized
    fun getInProgressParties(): List<PartySummaryDto> = summaries.values.filter { it.status == "ONGOING" }

    // 홈 Fake 응답에는 기본 샘플을 제외하고 이번 실행에서 생성·시작한 파티만 전달
    @Synchronized
    fun getCreatedInProgressParties(): List<PartySummaryDto> =
        summaries.filterKeys { it in createdPartyIds }.values.filter { it.status == "ONGOING" }

    private fun waitingRoom(
        partyId: Long,
        name: String,
        goal: String,
        inviteCode: String,
        durationDays: Int,
        depositAmount: Int,
        maxMembers: Int,
        members: List<PartyMemberDto>
    ) = PartyWaitingRoomDto(
        partyId = partyId,
        name = name,
        goal = goal,
        status = "WAITING",
        inviteCode = inviteCode,
        currentMembers = members.size,
        maxMembers = maxMembers,
        durationDays = durationDays,
        depositAmount = depositAmount,
        members = members,
        isHost = members.any { it.userId == CURRENT_USER_ID && it.role == "HOST" },
        canStart = canStart(members)
    )

    private fun PartyWaitingRoomDto.withMembers(members: List<PartyMemberDto>) = copy(
        currentMembers = members.size,
        members = members,
        isHost = members.any { it.userId == CURRENT_USER_ID && it.role == "HOST" },
        canStart = canStart(members)
    )

    private fun canStart(members: List<PartyMemberDto>): Boolean =
        members.size >= 2 && members.filter { it.role == "MEMBER" }.all { it.status == "READY" }

    // Fake 생성 요청의 challengeId를 화면 테스트용 챌린지명으로 변환
    private fun fakeChallengeName(challengeId: Long): String = when (challengeId) {
        0L -> "새벽 6시 기상"
        1L -> "30분 러닝"
        2L -> "하루 독서 30분"
        3L -> "영양제 챙기기"
        4L -> "영단어 30개"
        5L -> "명상 10분"
        else -> "선택한 챌린지"
    }

    private fun member(userId: Long, nickname: String, role: String, status: String) = PartyMemberDto(
        userId = userId,
        nickname = nickname,
        profileImageUrl = null,
        role = role,
        status = status
    )
}

class FakePartyJoinException(val reason: PartyJoinError) : IllegalStateException()
