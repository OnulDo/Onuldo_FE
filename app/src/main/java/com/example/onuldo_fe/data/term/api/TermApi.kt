package com.example.onuldo_fe.data.term.api

import com.example.onuldo_fe.data.network.BaseResponse
import com.example.onuldo_fe.data.term.dto.TermResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/** 약관 조회 API. 인증 없이 호출 가능하다. */
interface TermApi {

    /** [termType]은 `SERVICE` · `PRIVACY` · `REFUND` · `AGE_14` · `MARKETING`. */
    @GET("api/terms/{termType}")
    suspend fun getTerm(@Path("termType") termType: String): Response<BaseResponse<TermResponseDto>>
}
