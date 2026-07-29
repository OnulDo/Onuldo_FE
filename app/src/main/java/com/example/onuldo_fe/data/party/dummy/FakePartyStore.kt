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
        1L to PartySummaryDto(1L, "새벽 러너 파티", "ONGOING", 12, 0.4, 2, 5),
        2L to PartySummaryDto(2L, "책상 공부 인증 파티", "ONGOING", 20, 0.6, 3, 5)
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
            inviteCode = inviteCode,
            durationDays = request.durationDays,
            depositAmount = request.depositAmount,
            maxMembers = request.maxMembers,
            members = members
        )
        parties[partyId] = StoredParty(
            room = room,
            // TODO: 대기방 API에 챌린지 정보가 추가되면 응답 DTO에서 사용한다.
            challengeName = "챌린지",
            status = "WAITING"
        )
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
        summaries[partyId] = PartySummaryDto(partyId, stored.room.name, "ONGOING", stored.room.durationDays, 0.0, 0, members.size)
        FakePartyFeedState.updateParty(partyId, stored.room.name, stored.challengeName, members.size)
        return PartyStartResponseDto(partyId, "ONGOING", "2026-07-27T12:00:00")
    }

    @Synchronized
    fun getInProgressParties(): List<PartySummaryDto> = summaries.values.filter { it.status == "ONGOING" }

    private fun waitingRoom(
        partyId: Long,
        name: String,
        inviteCode: String,
        durationDays: Int,
        depositAmount: Int,
        maxMembers: Int,
        members: List<PartyMemberDto>
    ) = PartyWaitingRoomDto(
        partyId = partyId,
        name = name,
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

    private fun member(userId: Long, nickname: String, role: String, status: String) = PartyMemberDto(
        userId = userId,
        nickname = nickname,
        profileImageUrl = null,
        role = role,
        status = status
    )
}

class FakePartyJoinException(val reason: PartyJoinError) : IllegalStateException()
