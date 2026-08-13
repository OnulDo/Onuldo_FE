package com.example.onuldo_fe.data.network

/**
 * Repository 계층이 ViewModel에 돌려주는 API 호출 결과.
 * 예외를 던지지 않고 값으로 표현해, 화면이 로딩/에러 상태를 다루기 쉽게 한다.
 */
sealed interface ApiResult<out T> {

    data class Success<out T>(val data: T) : ApiResult<T>

    /**
     * 서버가 응답은 했지만 실패인 경우. [message]는 서버가 준 한국어 문구라 그대로 노출해도 된다.
     * [code]는 [ApiErrorCode] 참고 — 동작 분기가 필요할 때만 사용한다.
     */
    data class Failure(
        val code: String,
        val message: String,
        val httpStatus: Int = 0,
    ) : ApiResult<Nothing>

    /** 서버에 닿지 못했거나 응답을 해석하지 못한 경우(네트워크 끊김, 타임아웃, 파싱 실패 등). */
    data class NetworkError(val throwable: Throwable) : ApiResult<Nothing>

    val isSuccess: Boolean get() = this is Success

    fun getOrNull(): T? = (this as? Success)?.data

    /**
     * 화면에 띄울 에러 문구. 성공이면 null.
     * 서버 문구를 우선 쓰고, 서버에 닿지 못한 경우에만 클라이언트 기본 문구를 쓴다.
     */
    fun errorMessageOrNull(): String? = when (this) {
        is Success -> null
        is Failure -> message.ifBlank { DEFAULT_ERROR_MESSAGE }
        is NetworkError -> NETWORK_ERROR_MESSAGE
    }

    companion object {
        const val DEFAULT_ERROR_MESSAGE = "요청을 처리하지 못했어요. 잠시 후 다시 시도해주세요."
        const val NETWORK_ERROR_MESSAGE = "네트워크 연결을 확인해주세요."
    }
}

/**
 * 성공 값만 변환하고 실패·네트워크오류는 그대로 통과시킨다.
 * Repository가 DTO를 도메인 모델로 옮길 때 쓴다.
 */
inline fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> = when (this) {
    is ApiResult.Success -> ApiResult.Success(transform(data))
    is ApiResult.Failure -> this
    is ApiResult.NetworkError -> this
}

inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

/** 실패·네트워크오류를 한 번에 처리한다. [message]는 그대로 화면에 노출 가능한 문구. */
inline fun <T> ApiResult<T>.onError(action: (code: String, message: String) -> Unit): ApiResult<T> {
    when (this) {
        is ApiResult.Failure -> action(code, errorMessageOrNull().orEmpty())
        is ApiResult.NetworkError -> action(ApiErrorCode.UNKNOWN, errorMessageOrNull().orEmpty())
        is ApiResult.Success -> Unit
    }
    return this
}

/** 커서 페이지네이션 목록. 서버의 `content`/`nextCursor`/`hasNext`를 도메인 모델로 옮긴 것. */
data class CursorPage<T>(
    val items: List<T>,
    val nextCursor: String?,
    val hasNext: Boolean,
)
