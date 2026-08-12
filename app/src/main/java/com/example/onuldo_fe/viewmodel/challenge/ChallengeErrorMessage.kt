package com.example.onuldo_fe.viewmodel.challenge

import com.example.onuldo_fe.data.network.ApiResult

/**
 * 챌린지 일반 오류 표시 문구.
 *
 * 비즈니스 code 분기(포인트 부족→충전 팝업, 이미 참여중→안내)는 ViewModel에서 먼저 처리하고,
 * 그 외 **일반 실패**에만 이 매퍼로 HTTP status / 네트워크를 구분한 문구를 만든다.
 * (팀 home 도메인의 `toHomeErrorMessage`와 동일한 문구 정책 — 서버 message 대신 상태별 고정 문구.)
 *
 * `ApiResult.Failure`가 이미 `httpStatus`를 들고 있어 Throwable로 되돌리지 않고 그대로 매핑한다.
 */
internal fun ApiResult<*>.toChallengeErrorMessage(): String = when (this) {
    is ApiResult.NetworkError -> "네트워크 연결을 확인해 주세요."
    is ApiResult.Failure -> when (httpStatus) {
        400 -> "요청을 처리할 수 없어요."
        401, 403 -> "로그인이 만료되었어요. 다시 로그인해 주세요."
        404 -> "챌린지를 찾을 수 없어요."
        in 500..599 -> "서버에 잠시 문제가 생겼어요. 잠시 후 다시 시도해 주세요."
        else -> "챌린지를 불러오지 못했어요."
    }
    // 성공은 이 매퍼를 호출하지 않는다(방어적 기본값).
    is ApiResult.Success -> "챌린지를 불러오지 못했어요."
}
