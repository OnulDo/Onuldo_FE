package com.example.onuldo_fe.repository.term

import com.example.onuldo_fe.data.auth.dto.TermType
import com.example.onuldo_fe.data.network.ApiResult
import com.example.onuldo_fe.model.term.Term

/** 약관 저장소. 인증 없이 조회 가능하다. */
interface TermRepository {
    suspend fun getTerm(termType: TermType): ApiResult<Term>
}
