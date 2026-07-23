package com.example.onuldo_fe.data.party.dummy

import com.example.onuldo_fe.data.party.dto.CreatePartyRequestDto
import com.example.onuldo_fe.data.party.dto.CreatePartyResponseDto
import com.example.onuldo_fe.data.party.dto.PartyMemberDto
import com.example.onuldo_fe.data.party.dto.PartySummaryDto
import com.example.onuldo_fe.data.party.dto.PartyWaitingRoomDto
import com.example.onuldo_fe.model.party.PartyJoinError
import com.example.onuldo_fe.model.party.PartyJoinResult

/**
 * 모든 fake 파티 화면이 공유하는 단일 저장소입니다.
 * 실제 API 연동 시 각 함수 호출을 서버 요청으로 교체합니다.
 */
object FakePartyStore {
    // TODO 로그인 연동 시 인증 서버가 내려주는 현재 사용자 ID로 교체
    const val CURRENT_USER_ID = "current-user"
    private const val CREATED_PARTY_ID = "party-created"

    private data class StoredParty(
        val room: PartyWaitingRoomDto,
        val status: String
    )

    private val parties = mutableMapOf(
        "party-001" to StoredParty(
            room = PartyWaitingRoomDto(
                partyId = "party-001",
                partyName = "갓생팟",
                challengeName = "30일 헬스 챌린지",
                inviteCode = PartyInviteDummyData.VALID_CODE,
                period = "4주",
                deposit = 30_000,
                capacity = 5,
                members = listOf(
                    member("leader-001", "민지", "LEADER", "NOT_APPLICABLE", 0),
                    member("member-001", "서연", "MEMBER", "READY", 1)
                )
            ),
            status = "RECRUITING"
        )
    )

    private val summaries = mutableMapOf(
        "party-1" to PartySummaryDto("party-1", "새벽 러너 파티", "30분 러닝", "D-12", "7:00 마감", "45분 남음", 2, 5, "IN_PROGRESS"),
        "party-2" to PartySummaryDto("party-2", "책상 공부 인증 파티", "5시간 집중", "D-20", "6:00 마감", "1시간 남음", 3, 5, "IN_PROGRESS")
    )

    @Synchronized
    fun create(request: CreatePartyRequestDto): CreatePartyResponseDto {
        // 파티 생성자는 항상 방장이고 준비완료 대상에서 제외됩니다.
        val room = PartyWaitingRoomDto(
            partyId = CREATED_PARTY_ID,
            partyName = request.name,
            challengeName = request.challengeName,
            inviteCode = PartyInviteDummyData.CREATED_PARTY_CODE,
            period = request.period,
            deposit = request.deposit,
            capacity = request.capacity,
            members = buildList {
                add(member(CURRENT_USER_ID, "민지", "LEADER", "NOT_APPLICABLE", 0))
                // 시작 화면 이동 테스트가 필요하면 PartyTestConfig의 값을 true로 사용합니다.
                if (PartyTestConfig.CREATE_READY_TO_START) {
                    add(member("member-test-1", "서연", "MEMBER", "READY", 1))
                }
            }
        )
        parties[CREATED_PARTY_ID] = StoredParty(room, "RECRUITING")
        return CreatePartyResponseDto(CREATED_PARTY_ID, room.inviteCode)
    }

    @Synchronized
    fun join(inviteCode: String): PartyJoinResult {
        // 초대코드 상태와 정원을 확인한 뒤 방장 승인 없이 파티원을 즉시 추가합니다.
        val normalizedCode = inviteCode.uppercase()
        val entry = parties.entries.firstOrNull { it.value.room.inviteCode == normalizedCode }
            ?: return PartyJoinResult.Failure(PartyJoinError.Invalid)
        val stored = entry.value
        if (stored.status == "IN_PROGRESS") return PartyJoinResult.Failure(PartyJoinError.AlreadyStarted)
        if (stored.status == "DISBANDED") return PartyJoinResult.Failure(PartyJoinError.Expired)
        if (stored.room.members.size >= stored.room.capacity) return PartyJoinResult.Failure(PartyJoinError.Full)

        if (stored.room.members.none { it.id == CURRENT_USER_ID }) {
            val nextOrder = (stored.room.members.maxOfOrNull { it.joinedOrder } ?: 0) + 1
            val updatedRoom = stored.room.copy(
                members = stored.room.members + member(
                    CURRENT_USER_ID,
                    "준호",
                    "MEMBER",
                    "WAITING",
                    nextOrder
                )
            )
            parties[entry.key] = stored.copy(room = updatedRoom)
        }
        return PartyJoinResult.Success(entry.key)
    }

    fun getRoom(partyId: String): PartyWaitingRoomDto =
        parties[partyId]?.room ?: error("존재하지 않는 파티입니다.")

    @Synchronized
    fun ready(partyId: String): PartyWaitingRoomDto {
        // 실제 API에서는 서버의 포인트 검증 성공 응답을 받은 뒤 Ready 상태를 반영합니다.
        val stored = parties[partyId] ?: error("존재하지 않는 파티입니다.")
        val room = stored.room.copy(
            members = stored.room.members.map {
                if (it.id == CURRENT_USER_ID && it.role != "LEADER") it.copy(readyStatus = "READY") else it
            }
        )
        parties[partyId] = stored.copy(room = room)
        return room
    }

    @Synchronized
    fun leave(partyId: String) {
        // 현재 사용자를 제거하고, 방장 이탈이면 가장 먼저 입장한 파티원에게 권한을 승계합니다.
        val stored = parties[partyId] ?: return
        val leavingMember = stored.room.members.firstOrNull { it.id == CURRENT_USER_ID } ?: return
        val remaining = stored.room.members.filterNot { it.id == CURRENT_USER_ID }.toMutableList()

        if (remaining.isEmpty()) {
            // 마지막 파티원이 이탈하면 파티를 해체하고 초대코드를 만료 상태로 처리합니다.
            parties[partyId] = stored.copy(
                room = stored.room.copy(members = emptyList()),
                status = "DISBANDED"
            )
            return
        }

        if (leavingMember.role == "LEADER") {
            val successorIndex = remaining.indices.minBy { remaining[it].joinedOrder }
            remaining[successorIndex] = remaining[successorIndex].copy(
                role = "LEADER",
                readyStatus = "NOT_APPLICABLE"
            )
        }
        parties[partyId] = stored.copy(room = stored.room.copy(members = remaining))
    }

    @Synchronized
    fun start(partyId: String): PartySummaryDto {
        // 정원 충족 여부와 관계없이 2명 이상이고 현재 파티원 전원이 준비했을 때 시작합니다.
        val stored = parties[partyId] ?: error("존재하지 않는 파티입니다.")
        val members = stored.room.members
        check(members.size >= 2 && members.filter { it.role == "MEMBER" }.all { it.readyStatus == "READY" }) {
            "모든 파티원이 준비되지 않았습니다."
        }
        parties[partyId] = stored.copy(status = "IN_PROGRESS")
        val summary = PartySummaryDto(
            partyId = partyId,
            partyName = stored.room.partyName,
            challengeName = stored.room.challengeName,
            dDay = "D-28",
            deadline = "오늘 마감",
            remainingText = null,
            completedMemberCount = 0,
            totalMemberCount = members.size,
            status = "IN_PROGRESS"
        )
        summaries[partyId] = summary
        FakePartyFeedState.updateParty(
            partyId = partyId,
            partyName = stored.room.partyName,
            challengeName = stored.room.challengeName,
            memberCount = members.size
        )
        return summary
    }

    fun getInProgressParties(): List<PartySummaryDto> =
        summaries.values.filter { it.status == "IN_PROGRESS" }

    private fun member(
        id: String,
        nickname: String,
        role: String,
        readyStatus: String,
        joinedOrder: Int
    ) = PartyMemberDto(
        id = id,
        nickname = nickname,
        profileImageUrl = null,
        role = role,
        readyStatus = readyStatus,
        joinedOrder = joinedOrder
    )
}
