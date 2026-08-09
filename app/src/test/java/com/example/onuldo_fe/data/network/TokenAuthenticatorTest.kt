package com.example.onuldo_fe.data.network

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TokenAuthenticatorTest {

    @Test
    fun `errorCode TOKEN_EXPIRED일 때만 재발급 대상으로 판단한다`() {
        assertTrue(response("""{"errorCode":"TOKEN_EXPIRED"}""").hasTokenExpiredCode())
        assertFalse(response("""{"errorCode":"UNAUTHORIZED"}""").hasTokenExpiredCode())
    }

    @Test
    fun `기존 code 응답 형식도 지원한다`() {
        assertTrue(response("""{"code":"TOKEN_EXPIRED"}""").hasTokenExpiredCode())
    }

    @Test
    fun `본문 없는 일반 401은 재발급 대상으로 판단하지 않는다`() {
        assertFalse(response("").hasTokenExpiredCode())
    }

    private fun response(json: String): Response = Response.Builder()
        .request(Request.Builder().url("https://example.com/api/test").build())
        .protocol(Protocol.HTTP_1_1)
        .code(401)
        .message("Unauthorized")
        .body(json.toResponseBody("application/json".toMediaType()))
        .build()
}
