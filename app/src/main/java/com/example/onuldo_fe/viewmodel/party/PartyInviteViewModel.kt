package com.example.onuldo_fe.viewmodel.party

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.onuldo_fe.model.party.PartyJoinError
import com.example.onuldo_fe.model.party.PartyJoinResult
import com.example.onuldo_fe.repository.party.PartyInviteRepository
import com.example.onuldo_fe.repository.party.PartyInviteRepositoryProvider
import com.example.onuldo_fe.ui.screen.party.InviteCodeError

// 초대코드 참여 요청의 입력 오류·성공 결과·네트워크 상태 관리
data class PartyInviteUiState(
    val error: InviteCodeError? = null,         // 코드 정책 검증 실패 상태
    val joinedPartyId: String? = null,          // 참여 성공 후 이동할 파티 ID
    val isJoining: Boolean = false,             // 참여 요청 중 중복 제출 방지 상태
    val networkErrorMessage: String? = null     // 통신 실패 시 표시할 오류 문구
)

// 초대코드 검증 후 성공한 파티 ID를 PartyRoute에 전달
class PartyInviteViewModel(
    private val repository: PartyInviteRepository = PartyInviteRepositoryProvider.provide()
) : ViewModel() {
    var uiState by mutableStateOf(PartyInviteUiState())
        private set

    fun joinParty(inviteCode: String) {
        // 참가하기 버튼 연속 클릭에 따른 참여 요청 중복 전송 방지
        if (uiState.isJoining) return
        uiState = PartyInviteUiState(isJoining = true)
        viewModelScope.launch {
            uiState = runCatching { repository.joinParty(inviteCode) }
                .fold(
                    onSuccess = { result ->
                        when (result) {
                            // 성공 결과는 Route의 LaunchedEffect가 감지해 대기방으로 이동
                            is PartyJoinResult.Success -> PartyInviteUiState(joinedPartyId = result.partyId)
                            // 서버 정책 오류를 초대코드 다이얼로그의 강조 상태와 문구로 변환
                            is PartyJoinResult.Failure -> PartyInviteUiState(error = result.error.toUiError())
                        }
                    },
                    onFailure = {
                        PartyInviteUiState(networkErrorMessage = "파티 참여 요청에 실패했어요.")
                    }
                )
        }
    }

    fun reset() {
        // 다이얼로그 닫기 또는 다시 입력 시 코드 검증 결과를 기본 상태로 초기화
        uiState = PartyInviteUiState()
    }
}

// 도메인 오류가 UI 계층에 직접 의존하지 않도록 화면 전용 오류 타입으로 변환
private fun PartyJoinError.toUiError() = when (this) {
    PartyJoinError.Invalid -> InviteCodeError.Invalid
    PartyJoinError.AlreadyStarted -> InviteCodeError.AlreadyStarted
    PartyJoinError.Full -> InviteCodeError.Full
    PartyJoinError.Expired -> InviteCodeError.Expired
}
