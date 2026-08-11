package com.example.onuldo_fe.data.network

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CancellationException
import retrofit2.Response

/** 에러 본문 파싱 전용 Gson. Retrofit 컨버터와 별개로 쓰인다. */
private val errorGson = Gson()

/**
 * `BaseResponse<T>`를 돌려주는 API 호출을 [ApiResult]로 감싼다.
 *
 * - 성공: `code == "SUCCESS"` 이고 `result`가 있을 때
 * - 실패: 서버가 준 `code`/`message`를 그대로 전달 (문구가 이미 한국어로 완성되어 옴)
 * - 네트워크 오류: 서버에 닿지 못했거나 파싱 실패
 *
 * 코루틴 취소([CancellationException])는 오류로 처리하지 않고 그대로 전파한다.
 */
suspend fun <T> safeApiCall(call: suspend () -> Response<BaseResponse<T>>): ApiResult<T> =
    runCatchingApi {
        val response = call()
        val body = response.body()

        when {
            response.isSuccessful && body != null && body.isSuccess -> {
                val result = body.result
                if (result != null) {
                    ApiResult.Success(result)
                } else {
                    // 성공 코드인데 result가 비어 있으면 스펙과 어긋난 상태다.
                    ApiResult.Failure(
                        code = ApiErrorCode.UNKNOWN,
                        message = ApiResult.DEFAULT_ERROR_MESSAGE,
                        httpStatus = response.code(),
                    )
                }
            }

            else -> response.toFailure(body?.code, body?.message)
        }
    }

/**
 * 응답 본문이 필요 없는(또는 무시해도 되는) 호출용. 성공 여부만 판정한다.
 */
suspend fun safeUnitApiCall(call: suspend () -> Response<BaseResponse<Unit>>): ApiResult<Unit> =
    runCatchingApi {
        val response = call()
        val body = response.body()

        if (response.isSuccessful && (body == null || body.isSuccess)) {
            ApiResult.Success(Unit)
        } else {
            response.toFailure(body?.code, body?.message)
        }
    }

/**
 * 커서 페이지네이션 응답을 [CursorPage]로 변환한다.
 * 목록이 비어 있는 것은 정상이므로 `content`가 null이면 빈 목록으로 본다.
 */
suspend fun <T> safeCursorApiCall(
    call: suspend () -> Response<CursorPageResponse<T>>,
): ApiResult<CursorPage<T>> = runCatchingApi {
    val response = call()
    val body = response.body()

    if (response.isSuccessful && body != null && body.isSuccess) {
        ApiResult.Success(
            CursorPage(
                items = body.result.orEmpty(),
                nextCursor = body.nextCursor,
                hasNext = body.hasNext,
            )
        )
    } else {
        response.toFailure(body?.code, body?.message)
    }
}

/** 공통 예외 처리. 취소는 그대로 전파하고 나머지는 [ApiResult.NetworkError]로 접는다. */
private inline fun <T> runCatchingApi(block: () -> ApiResult<T>): ApiResult<T> =
    try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ApiResult.NetworkError(e)
    }

/**
 * 실패 응답을 [ApiResult.Failure]로 변환한다.
 * 본문이 파싱 안 된 경우(에러 응답은 보통 `errorBody`에 담긴다) 에러 본문을 다시 읽어 코드·문구를 얻는다.
 */
private fun <T> Response<T>.toFailure(
    parsedCode: String?,
    parsedMessage: String?,
): ApiResult.Failure {
    val fallback = parseErrorBody()
    return ApiResult.Failure(
        code = parsedCode ?: fallback?.code ?: ApiErrorCode.UNKNOWN,
        message = parsedMessage ?: fallback?.message ?: ApiResult.DEFAULT_ERROR_MESSAGE,
        httpStatus = code(),
    )
}

/**
 * `errorBody`를 [ErrorBody]로 파싱한다.
 *
 * `@Valid` 검증 실패(400)일 때 서버가 `result`에 `{필드명: 메시지}` Map을 넣어 보내므로
 * `BaseResponse<T>`로 읽으면 타입이 어긋난다. `code`/`message`만 가진 모델로 읽어 그 문제를 피한다.
 */
private fun <T> Response<T>.parseErrorBody(): ErrorBody? =
    try {
        errorBody()?.string()?.takeIf(String::isNotBlank)?.let {
            errorGson.fromJson(it, ErrorBody::class.java)
        }
    } catch (e: JsonSyntaxException) {
        null
    } catch (e: Exception) {
        null
    }
