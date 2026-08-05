package com.example.onuldo_fe.viewmodel.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.data.network.onError
import com.example.onuldo_fe.data.network.onSuccess
import com.example.onuldo_fe.data.user.dto.PointTransactionTypeDto
import com.example.onuldo_fe.model.user.PointTransaction
import com.example.onuldo_fe.model.user.WalletSummary
import com.example.onuldo_fe.repository.user.UserRepository
import com.example.onuldo_fe.repository.user.UserRepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 거래 내역 필터.
 *
 * ⚠️ 서버 `PointTransactionType`은 CHARGE·WITHDRAW·DEPOSIT·REFUND **4종뿐**이라,
 * 디자인의 "차감" 칩에 해당하는 서버 타입이 없다. 서버가 가진 종류에 맞춰 칩을 구성했다.
 * (차감 내역은 지갑 요약의 `totalPenalty`로만 확인 가능 — 백엔드에 타입 추가 확인 필요.)
 */
enum class WalletFilter(val label: String, val type: PointTransactionTypeDto?) {
    ALL("전체", null),
    CHARGE("충전", PointTransactionTypeDto.CHARGE),
    DEPOSIT("예치", PointTransactionTypeDto.DEPOSIT),
    REFUND("환급", PointTransactionTypeDto.REFUND),
    WITHDRAW("출금", PointTransactionTypeDto.WITHDRAW),
}

data class PointWalletUiState(
    val summary: WalletSummary = WalletSummary.EMPTY,
    val transactions: List<PointTransaction> = emptyList(),
    val selectedFilter: WalletFilter = WalletFilter.ALL,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasNext: Boolean = false,
    val errorMessage: String? = null,
)

/** 포인트 지갑. 요약(`/wallet/summary`)과 거래내역(`/wallet/transactions`, 커서 페이징)을 함께 다룬다. */
class PointWalletViewModel(
    private val userRepository: UserRepository = UserRepositoryProvider.provide(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(PointWalletUiState())
    val uiState: StateFlow<PointWalletUiState> = _uiState.asStateFlow()

    private var nextCursor: String? = null

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            userRepository.getWalletSummary()
                .onSuccess { summary -> _uiState.update { it.copy(summary = summary) } }
                .onError { _, message -> _uiState.update { it.copy(errorMessage = message) } }

            loadFirstPage()
        }
    }

    fun selectFilter(filter: WalletFilter) {
        if (_uiState.value.selectedFilter == filter) return
        _uiState.update { it.copy(selectedFilter = filter, transactions = emptyList()) }
        viewModelScope.launch { loadFirstPage() }
    }

    /** 목록 끝에 도달했을 때 다음 페이지를 이어 받는다. */
    fun loadMore() {
        val state = _uiState.value
        val cursor = nextCursor
        if (state.isLoading || state.isLoadingMore || !state.hasNext || cursor == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            userRepository.getWalletTransactions(
                type = state.selectedFilter.type,
                cursor = cursor,
            )
                .onSuccess { page ->
                    nextCursor = page.nextCursor
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            transactions = it.transactions + page.items,
                            hasNext = page.hasNext,
                        )
                    }
                }
                .onError { _, message ->
                    _uiState.update { it.copy(isLoadingMore = false, errorMessage = message) }
                }
        }
    }

    private suspend fun loadFirstPage() {
        nextCursor = null
        _uiState.update { it.copy(isLoading = true) }

        userRepository.getWalletTransactions(type = _uiState.value.selectedFilter.type)
            .onSuccess { page ->
                nextCursor = page.nextCursor
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        transactions = page.items,
                        hasNext = page.hasNext,
                    )
                }
            }
            .onError { _, message ->
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            }
    }
}
