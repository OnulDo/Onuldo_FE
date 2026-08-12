package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dto.CreatePartyRequestDto
import com.example.onuldo_fe.data.party.dto.CreatePartyResponseDto
import com.example.onuldo_fe.data.party.dto.PartySummaryDto
import com.example.onuldo_fe.data.party.dto.PartyWaitingRoomDto
import com.example.onuldo_fe.data.party.dto.PartyStartResponseDto
import com.example.onuldo_fe.data.party.dto.PartySettlementResultDto
import com.example.onuldo_fe.data.party.dummy.FakePartyStore
import com.example.onuldo_fe.data.party.dummy.PartySettlementDummyData

// 실제 서버 연동 전 파티 생성·대기방·시작·이탈 흐름을 메모리 저장소로 테스트
// Retrofit API 구현 준비 후 PartyRepositoryProvider에서 이 구현체만 교체
class FakePartyApi : PartyApi {
    // 명세에 따라 모집 중 파티를 제외하고 진행 중인 파티만 반환
    override suspend fun getParties(): List<PartySummaryDto> = FakePartyStore.getInProgressParties()

    // 파티 생성 후 서버 발급을 대신하는 fake 초대코드와 partyId 반환
    override suspend fun createParty(request: CreatePartyRequestDto): CreatePartyResponseDto = FakePartyStore.create(request)

    // 참여·준비·승계 결과가 반영된 최신 대기방 전체 정보 반환
    override suspend fun getWaitingRoom(partyId: Long): PartyWaitingRoomDto = FakePartyStore.getRoom(partyId)

    // 현재 파티원을 준비완료로 변경하고 갱신된 멤버 목록 반환
    override suspend fun readyParty(partyId: Long, ready: Boolean): PartyWaitingRoomDto =
        FakePartyStore.ready(partyId, ready)

    // 현재 사용자 이탈 및 필요한 경우 방장 승계 또는 파티 해체 처리
    override suspend fun leaveParty(partyId: Long) = FakePartyStore.leave(partyId)

    // 시작 조건 검증 후 파티를 진행 중 상태로 변경
    override suspend fun startParty(partyId: Long): PartyStartResponseDto = FakePartyStore.start(partyId)

    // partyId에 따라 전원 성공·일부 성공·전원 실패 정산 결과 반환
    override suspend fun getSettlementResult(partyId: Long): PartySettlementResultDto =
        PartySettlementDummyData.get(partyId)
}
