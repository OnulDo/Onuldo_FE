package com.example.onuldo_fe.model.party

// 초대코드로 파티 참여 시 발생할 수 있는 정책 오류
enum class PartyJoinError {
    Invalid,                                   // 존재하지 않거나 형식이 잘못된 코드
    AlreadyStarted,                            // 이미 시작되어 참여할 수 없는 파티
    Full,                                      // 모집 최대 인원에 도달한 파티
    Expired,                                   // 해체 등으로 초대코드가 만료된 파티
    Unknown                                    // 아직 분류되지 않은 서버 정책 오류
}

// 초대코드 파티 참여 요청의 성공 또는 정책 오류 결과
sealed interface PartyJoinResult {
    data class Success(
        val waitingRoom: PartyWaitingRoom      // 참여 성공 응답으로 받은 최신 대기방 정보
    ) : PartyJoinResult

    data class Failure(
        val error: PartyJoinError              // 참여 실패 원인을 나타내는 정책 오류
    ) : PartyJoinResult
}
