package com.example.onuldo_fe.repository.party

import com.example.onuldo_fe.data.network.ErrorBody
import com.example.onuldo_fe.data.party.api.PartyInviteApi
import com.example.onuldo_fe.data.party.api.RealPartyApi
import com.example.onuldo_fe.data.party.dto.PartyJoinRequestDto
import com.example.onuldo_fe.data.party.dummy.FakePartyJoinException
import com.example.onuldo_fe.model.party.PartyJoinError
import com.example.onuldo_fe.model.party.PartyJoinResult
import com.google.gson.Gson
import java.io.IOException
import java.util.Locale
import retrofit2.HttpException

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
        // 참여 응답에 포함된 대기방 전체 정보를 추가 GET 없이 화면에 적용한다.
        PartyJoinResult.Success(room)
    } catch (error: FakePartyJoinException) {
        PartyJoinResult.Failure(error.reason)
    } catch (error: HttpException) {
        val errorBody = error.response()
            ?.errorBody()
            ?.string()
            ?.let(::parseErrorBody)
        PartyJoinResult.Failure(
            mapPartyJoinError(
                code = errorBody?.code,
                message = errorBody?.message
            )
        )
    }
}

private val errorBodyGson = Gson()

private fun parseErrorBody(json: String): ErrorBody? =
    runCatching { errorBodyGson.fromJson(json, ErrorBody::class.java) }.getOrNull()

/** 서버 오류 code를 우선 사용하고, Swagger에 code가 문서화되지 않은 동안 message도 함께 판독한다. */
internal fun mapPartyJoinError(code: String?, message: String?): PartyJoinError {
    val errorText = listOfNotNull(code, message).joinToString(" ").uppercase(Locale.ROOT)
    return when {
        errorText.containsAny("EXPIRED", "만료") -> PartyJoinError.Expired
        errorText.containsAny("FULL", "CAPACITY", "정원", "인원 초과") -> PartyJoinError.Full
        errorText.containsAny("STARTED", "ONGOING", "이미 시작", "진행 중") -> PartyJoinError.AlreadyStarted
        errorText.containsAny("INVALID", "NOT_FOUND", "유효하지", "존재하지", "잘못된") -> PartyJoinError.Invalid
        else -> PartyJoinError.Unknown
    }
}

private fun String.containsAny(vararg candidates: String): Boolean =
    candidates.any(::contains)
