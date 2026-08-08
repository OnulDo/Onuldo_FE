package com.example.onuldo_fe.viewmodel.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.data.network.ApiErrorCode
import com.example.onuldo_fe.data.network.onError
import com.example.onuldo_fe.data.network.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.onuldo_fe.repository.user.UserRepository
import com.example.onuldo_fe.repository.user.UserRepositoryProvider

data class PointWithdrawUiState(
    /** 보유 포인트(정산 완료분). null이면 아직 조회 전. */
    val balance: Long? = null,
    /** 진행 중(예치 등으로 묶인) 포인트. */
    val pendingPoints: Long = 0L,
    val isSubmitting: Boolean = false,
    /** 출금 실패 안내(일회성). 화면이 토스트로 노출한 뒤 [onErrorShown]으로 비운다. */
    val errorMessage: String? = null,
) {
    /** 실제 출금 가능한 금액 = 보유 − 진행 중(음수 방지). 조회 전에는 null. */
    val withdrawable: Long? get() = balance?.let { (it - pendingPoints).coerceAtLeast(0L) }
}

/** 포인트 출금. 보유 잔액을 보여주고 `POST /api/users/me/wallet/withdraw`로 출금한다. */
class PointWithdrawViewModel(
    private val userRepository: UserRepository = UserRepositoryProvider.provide(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(PointWithdrawUiState())
    val uiState: StateFlow<PointWithdrawUiState> = _uiState.asStateFlow()

    /** 출금 가능 금액 표시용 지갑 요약 조회. 화면이 보일 때마다 최신 잔액을 읽는다. */
    fun loadBalance() {
        viewModelScope.launch {
            userRepository.getWalletSummary()
                .onSuccess { summary ->
                    _uiState.update {
                        it.copy(
                            balance = summary.balance,
                            pendingPoints = summary.pendingPoints,
                            errorMessage = null,
                        )
                    }
                }
                .onError { code, message ->
                    val display =
                        if (ApiErrorCode.isTokenInvalid(code)) {
                            null
                        } else {
                            message.ifBlank { DEFAULT_ERROR }
                        }

                    _uiState.update {
                        it.copy(errorMessage = display)
                    }
                }
        }
    }
    /**
     * 출금 요청. 성공한 경우에만 [onSuccess]로 이전 화면에 되돌린다.
     * 잔액 부족·인증 오류 등 실패 사유는 서버 문구를 그대로 노출한다(인증 오류는 세션 흐름이 처리하므로 제외).
     */
    fun withdraw(point: Int, onSuccess: () -> Unit) {
        if (_uiState.value.isSubmitting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            userRepository.withdrawPoint(point)
                .onSuccess {
                    _uiState.update { it.copy(isSubmitting = false) }
                    onSuccess()
                }
                .onError { code, message ->
                    val display = if (ApiErrorCode.isTokenInvalid(code)) null else message.ifBlank { DEFAULT_ERROR }
                    _uiState.update { it.copy(isSubmitting = false, errorMessage = display) }
                }
        }
    }

    /** 실패 안내를 화면이 노출한 뒤 호출해 한 번만 뜨도록 비운다. */
    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private companion object {
        const val DEFAULT_ERROR = "출금에 실패했어요. 잠시 후 다시 시도해주세요."
    }
}
