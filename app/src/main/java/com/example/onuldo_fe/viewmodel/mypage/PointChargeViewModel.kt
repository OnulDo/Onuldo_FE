package com.example.onuldo_fe.viewmodel.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.data.network.onError
import com.example.onuldo_fe.data.network.onSuccess
import com.example.onuldo_fe.repository.user.UserRepository
import com.example.onuldo_fe.repository.user.UserRepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PointChargeUiState(
    val balance: Long = 0L,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

/**
 * 포인트 충전. `POST /api/users/me/wallet/charges`로 잔액을 올린다.
 *
 * ⚠️ 서버 충전 API는 **금액만 받고 결제 수단은 받지 않는다.** 화면의 결제 수단 선택(토스/카드/계좌)은
 * 실제 PG 연동 전까지 표시용이며, 서버에는 전달되지 않는다.
 */
class PointChargeViewModel(
    private val userRepository: UserRepository = UserRepositoryProvider.provide(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(PointChargeUiState())
    val uiState: StateFlow<PointChargeUiState> = _uiState.asStateFlow()

    init {
        loadBalance()
    }

    private fun loadBalance() {
        viewModelScope.launch {
            userRepository.getWalletSummary()
                .onSuccess { summary -> _uiState.update { it.copy(balance = summary.balance) } }
        }
    }

    /** 충전 요청. 성공 시 [onCharged] 호출(보통 이전 화면으로 복귀). */
    fun charge(point: Int, onCharged: () -> Unit) {
        if (_uiState.value.isLoading || point <= 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            userRepository.chargePoint(point)
                .onSuccess { result ->
                    _uiState.update { it.copy(isLoading = false, balance = result.balanceAfter) }
                    onCharged()
                }
                .onError { _, message ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                }
        }
    }
}
