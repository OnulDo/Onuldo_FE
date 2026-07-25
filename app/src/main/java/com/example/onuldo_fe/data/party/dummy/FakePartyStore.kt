package com.example.onuldo_fe.data.party.dummy

import com.example.onuldo_fe.data.party.dto.CreatePartyRequestDto
import com.example.onuldo_fe.data.party.dto.CreatePartyResponseDto
import com.example.onuldo_fe.data.party.dto.PartyMemberDto
import com.example.onuldo_fe.data.party.dto.PartySummaryDto
import com.example.onuldo_fe.data.party.dto.PartyWaitingRoomDto
import com.example.onuldo_fe.model.party.PartyJoinError
import com.example.onuldo_fe.model.party.PartyJoinResult

/**
 * 모든 fake 파티 화면이 공유하는 단일 저장소
 * 실제 API 연동 시 각 함수 호출을 서버 요청으로 교체
 */
object FakePartyStore {
    // TODO 로그인 연동 시 인증 서버가 내려주는 현재 사용자 ID로 교체
    const val CURRENT_USER_ID = "current-user"
    private var createdPartySequence = 0L

    // 대기방 정보와 파티 생명주기를 함께 저장해 초대코드 검증과 목록 노출에 사용
    private data class StoredParty(
        val room: PartyWaitingRoomDto,
        val status: String
    )

    // 앱 실행 중 생성·참여·준비·이탈 결과를 모든 fake API가 공유하는 메모리 저장소
    // 앱 프로세스 종료 시 상태 초기화
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

    // 파티 홈에 모집 중 대기방이 아닌 진행 중 파티 요약만 제공하기 위한 저장소
    private val summaries = mutableMapOf(
        "party-1" to PartySummaryDto("party-1", "새벽 러너 파티", "30분 러닝", "D-12", "7:00 마감", "45분 남음", 2, 5, "IN_PROGRESS"),
        "party-2" to PartySummaryDto("party-2", "책상 공부 인증 파티", "5시간 집중", "D-20", "6:00 마감", "1시간 남음", 3, 5, "IN_PROGRESS")
    )

    @Synchronized
    fun create(request: CreatePartyRequestDto): CreatePartyResponseDto {
        // Fake 환경에서도 생성된 파티가 기존 파티를 덮어쓰지 않도록 고유 ID와 코드를 발급
        val sequence = ++createdPartySequence
        val partyId = "party-created-$sequence"
        val inviteCode = "P${sequence.toString(36).uppercase().padStart(5, '0').takeLast(5)}"

        // 파티 생성자는 항상 방장이며 준비완료 대상에서 제외
        val room = PartyWaitingRoomDto(
            partyId = partyId,
            partyName = request.name,
            challengeName = request.challengeName,
            inviteCode = inviteCode,
            period = request.period,
            deposit = request.deposit,
            capacity = request.capacity,
            members = buildList {
                add(member(CURRENT_USER_ID, "민지", "LEADER", "NOT_APPLICABLE", 0))
                // 시작 화면 이동 테스트 시 PartyTestConfig 값을 true로 사용
                if (PartyTestConfig.CREATE_READY_TO_START) {
                    add(member("member-test-1", "서연", "MEMBER", "READY", 1))
                }
            }
        )
        parties[partyId] = StoredParty(room, "RECRUITING")
        return CreatePartyResponseDto(partyId, room.inviteCode)
    }

    @Synchronized
    fun join(inviteCode: String): PartyJoinResult {
        // 초대코드 상태와 정원 확인 후 방장 승인 없이 파티원을 즉시 추가
        val normalizedCode = inviteCode.uppercase()
        val entry = parties.entries.firstOrNull { it.value.room.inviteCode == normalizedCode }
            ?: return PartyJoinResult.Failure(PartyJoinError.Invalid)
        val stored = entry.value
        if (stored.status == "IN_PROGRESS") return PartyJoinResult.Failure(PartyJoinError.AlreadyStarted)
        if (stored.status == "DISBANDED") return PartyJoinResult.Failure(PartyJoinError.Expired)
        if (stored.room.members.size >= stored.room.capacity) return PartyJoinResult.Failure(PartyJoinError.Full)

        // 이미 참여한 사용자는 중복 추가하지 않고 기존 파티 ID만 반환
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

    // 상태 변경 후 화면 전체를 다시 그릴 수 있도록 최신 대기방 스냅샷 반환
    @Synchronized
    fun getRoom(partyId: String): PartyWaitingRoomDto =
        parties[partyId]?.room ?: error("존재하지 않는 파티입니다.")

    @Synchronized
    fun ready(partyId: String): PartyWaitingRoomDto {
        // 실제 API에서는 서버의 포인트 검증 성공 응답 후 Ready 상태 반영
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
        // 현재 사용자 제거 후 방장 이탈이면 가장 먼저 입장한 파티원에게 권한 승계
        val stored = parties[partyId] ?: return
        val leavingMember = stored.room.members.firstOrNull { it.id == CURRENT_USER_ID } ?: return
        val remaining = stored.room.members.filterNot { it.id == CURRENT_USER_ID }.toMutableList()

        if (remaining.isEmpty()) {
            // 마지막 파티원 이탈 시 파티 해체 및 초대코드 만료 처리
            parties[partyId] = stored.copy(
                room = stored.room.copy(members = emptyList()),
                status = "DISBANDED"
            )
            // 해체된 파티가 진행 목록이나 피드에 이전 상태로 남지 않도록 제거
            summaries.remove(partyId)
            FakePartyFeedState.removeParty(partyId)
            return
        }

        if (leavingMember.role == "LEADER") {
            // joinedOrder가 가장 작은 파티원을 새 방장으로 지정
            val successorIndex = remaining.indices.minBy { remaining[it].joinedOrder }
            remaining[successorIndex] = remaining[successorIndex].copy(
                role = "LEADER",
                readyStatus = "NOT_APPLICABLE"
            )
        }
        val updatedRoom = stored.room.copy(members = remaining)
        parties[partyId] = stored.copy(room = updatedRoom)

        // 진행 중 파티에서 이탈한 경우 목록 요약과 피드의 전체 인원 수도 함께 갱신
        summaries[partyId]?.let { summary ->
            summaries[partyId] = summary.copy(
                completedMemberCount = summary.completedMemberCount.coerceAtMost(remaining.size),
                totalMemberCount = remaining.size
            )
            FakePartyFeedState.updateParty(
                partyId = partyId,
                partyName = updatedRoom.partyName,
                challengeName = updatedRoom.challengeName,
                memberCount = remaining.size
            )
        }
    }

    @Synchronized
    fun start(partyId: String): PartySummaryDto {
        // 정원 충족 여부와 관계없이 2명 이상이고 현재 파티원 전원이 준비했을 때 시작
        val stored = parties[partyId] ?: error("존재하지 않는 파티입니다.")
        val members = stored.room.members
        check(members.size >= 2 && members.filter { it.role == "MEMBER" }.all { it.readyStatus == "READY" }) {
            "모든 파티원이 준비되지 않았습니다."
        }
        parties[partyId] = stored.copy(status = "IN_PROGRESS")
        // 시작된 파티는 대기방 상태 갱신 후 파티 홈 요약 목록에도 추가
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
        // 피드 화면이 실제 참여 인원과 파티 정보를 사용하도록 fake 피드 상태도 갱신
        FakePartyFeedState.updateParty(
            partyId = partyId,
            partyName = stored.room.partyName,
            challengeName = stored.room.challengeName,
            memberCount = members.size
        )
        return summary
    }

    @Synchronized
    fun getInProgressParties(): List<PartySummaryDto> =
        summaries.values.filter { it.status == "IN_PROGRESS" }

    // 테스트 멤버 생성 시 반복되는 DTO 조립을 한곳에서 처리
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
