package com.example.onuldo_fe.repository.term

import com.example.onuldo_fe.data.auth.dto.TermType
import com.example.onuldo_fe.data.network.ApiResult
import com.example.onuldo_fe.data.network.map
import com.example.onuldo_fe.data.network.safeApiCall
import com.example.onuldo_fe.data.term.api.TermApi
import com.example.onuldo_fe.model.term.Term
import com.example.onuldo_fe.model.term.TermContentBlock

class TermRepositoryImpl(
    private val termApi: TermApi,
) : TermRepository {

    override suspend fun getTerm(termType: TermType): ApiResult<Term> =
        safeApiCall { termApi.getTerm(termType.name) }.map { dto ->
            Term(
                termType = dto.termType?.let { raw ->
                    TermType.entries.firstOrNull { it.name == raw }
                },
                title = dto.title.orEmpty(),
                effectiveDate = dto.effectiveDate,
                content = dto.content.orEmpty().map { block ->
                    TermContentBlock(
                        type = block.type.orEmpty(),
                        content = block.content.orEmpty(),
                    )
                },
            )
        }
}
