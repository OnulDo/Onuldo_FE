package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.party.api.PartyInviteApi
import com.example.onuldo_fe.data.party.api.RealPartyApi
import com.example.onuldo_fe.data.party.dto.PartyJoinRequestDto
import com.example.onuldo_fe.data.party.dummy.FakePartyJoinException
import com.example.onuldo_fe.model.party.PartyJoinResult
import java.util.Locale
import retrofit2.HttpException
import java.io.IOException

// 초대코드 참여 요청을 API에 전달하고 참여 결과 반환
class PartyInviteRepositoryImpl(
    private val fakeApi: PartyInviteApi,
    private val realApi: RealPartyApi,
    private val useRealPartyJoinApi: Boolean
) : PartyInviteRepository {
    override suspend fun joinParty(inviteCode: String): PartyJoinResult = try {
        val request = PartyJoinRequestDto(inviteCode.trim().uppercase(Locale.ROOT))
        val room = if (useRealPartyJoinApi) {
            val response = realApi.joinParty(request)
            if (!response.isSuccessful) throw HttpException(response)
            val body = response.body() ?: throw IOException("파티 참여 응답 본문이 비어 있습니다.")
            body.result.toModel()
        } else {
            fakeApi.joinParty(request).toModel()
        }
        // 참여 응답에 포함된 대기방 전체 정보를 보존해 추가 GET 없이 화면에 적용한다.
        PartyJoinResult.Success(room)
    } catch (error: FakePartyJoinException) {
        PartyJoinResult.Failure(error.reason)
    }

    // TODO: 실제 오류 code 명세가 추가되면 HttpException을 Invalid/Started/Full/Expired로 변환한다.
}
