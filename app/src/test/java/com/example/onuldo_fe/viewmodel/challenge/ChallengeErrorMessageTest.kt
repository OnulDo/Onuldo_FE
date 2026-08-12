package com.example.onuldo_fe.viewmodel.challenge

import com.example.onuldo_fe.data.network.ApiResult
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

class ChallengeErrorMessageTest {

    // 서버에 닿지 못한 경우와 주요 HTTP 상태를 구분해 문구를 만드는지 검증한다.
    // (home의 toHomeErrorMessage와 동일한 문구 정책 — 서버 message가 아니라 상태별 고정 문구.)
    @Test
    fun `챌린지 오류 메시지는 네트워크와 주요 HTTP 상태를 구분한다`() {
        assertEquals(
            "네트워크 연결을 확인해 주세요.",
            ApiResult.NetworkError(IOException()).toChallengeErrorMessage()
        )
        assertEquals("요청을 처리할 수 없어요.", failure(400).toChallengeErrorMessage())
        assertEquals(
            "로그인이 만료되었어요. 다시 로그인해 주세요.",
            failure(401).toChallengeErrorMessage()
        )
        assertEquals(
            "로그인이 만료되었어요. 다시 로그인해 주세요.",
            failure(403).toChallengeErrorMessage()
        )
        assertEquals("챌린지를 찾을 수 없어요.", failure(404).toChallengeErrorMessage())
        assertEquals(
            "서버에 잠시 문제가 생겼어요. 잠시 후 다시 시도해 주세요.",
            failure(500).toChallengeErrorMessage()
        )
        // 표에 없는 상태는 일반 실패 문구로 떨어진다.
        assertEquals("챌린지를 불러오지 못했어요.", failure(418).toChallengeErrorMessage())
    }

    // code는 비즈니스 분기용이라 문구 매핑에는 영향을 주지 않는다(status만 본다).
    private fun failure(status: Int) =
        ApiResult.Failure(code = "ANY", message = "서버 문구", httpStatus = status)
}
