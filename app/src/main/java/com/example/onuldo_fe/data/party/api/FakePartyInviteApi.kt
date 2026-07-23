package com.example.onuldo_fe.data.party.api

import com.example.onuldo_fe.data.party.dummy.PartyInviteDummyData
import com.example.onuldo_fe.data.party.dummy.FakePartyStore
import com.example.onuldo_fe.model.party.PartyJoinError
import com.example.onuldo_fe.model.party.PartyJoinResult

// 초대코드별 정상 참여와 네 가지 정책 오류를 실제 서버 없이 재현
class FakePartyInviteApi : PartyInviteApi {
    override suspend fun joinParty(inviteCode: String): PartyJoinResult {
        // 초대코드를 대소문자 구분 없이 서버 전달 전과 같은 형태로 정규화
        val normalizedCode = inviteCode.uppercase()

        // 인원 초과 모달을 파티원 수 설정 없이 바로 확인하기 위한 개발용 테스트 코드
        if (normalizedCode == PartyInviteDummyData.FULL_CODE) {
            return PartyJoinResult.Failure(PartyJoinError.Full)
        }

        // 고정 테스트 코드를 먼저 판별하고 정상 코드는 공유 저장소의 현재 상태로 검증
        return when (normalizedCode) {
            PartyInviteDummyData.STARTED_CODE -> PartyJoinResult.Failure(PartyJoinError.AlreadyStarted)
            PartyInviteDummyData.EXPIRED_CODE -> PartyJoinResult.Failure(PartyJoinError.Expired)
            else -> FakePartyStore.join(normalizedCode)
        }
    }
}
