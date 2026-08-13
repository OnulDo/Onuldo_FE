package com.example.onuldo_fe.data.network

/**
 * 서버 공통 응답 래퍼.
 *
 * 성공: `{ "timestamp": ..., "code": "SUCCESS", "message": "요청에 성공하였습니다.", "result": {...} }`
 * 실패: `{ "timestamp": ..., "code": "DUPLICATE_EMAIL", "message": "이미 가입된 이메일입니다." }`
 *
 * 서버는 `isSuccess` 같은 boolean 필드를 주지 않으므로 성공 판정은 [code] 값으로 한다.
 * 실패 응답에는 `result` 필드 자체가 빠져서 내려온다(`@JsonInclude(NON_NULL)`).
 */
data class BaseResponse<T>(
    val timestamp: String? = null,
    val code: String? = null,
    val message: String? = null,
    val result: T? = null,
) {
    val isSuccess: Boolean get() = code == CODE_SUCCESS

    companion object {
        const val CODE_SUCCESS = "SUCCESS"
    }
}

/**
 * 커서 페이지네이션 응답. 지갑 거래내역처럼 목록을 나눠 받는 API가 사용한다.
 * 공통 래퍼와 달리 `result`가 아니라 [content]에 목록이 담긴다.
 */
data class CursorPageResponse<T>(
    val timestamp: String? = null,
    val code: String? = null,
    val message: String? = null,
    val result: List<T>? = null,
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
) {
    val isSuccess: Boolean get() = code == BaseResponse.CODE_SUCCESS
}

/**
 * 실패 응답 전용 파싱 모델.
 *
 * `@Valid` 검증 실패(400)일 때만 서버가 `result`에 `{필드명: 메시지}` Map을 실어 보내는데,
 * 이때 [BaseResponse]<T>로 파싱하면 타입이 어긋난다. 에러 본문은 항상 이 모델로 읽는다.
 */
data class ErrorBody(
    val code: String? = null,
    val errorCode: String? = null,
    val message: String? = null,
) {
    val effectiveCode: String? get() = errorCode?.takeIf(String::isNotBlank) ?: code
}
