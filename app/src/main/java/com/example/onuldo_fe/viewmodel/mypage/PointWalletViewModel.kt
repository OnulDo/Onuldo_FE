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
import kotlinx.coroutines.Job
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
    // 순서는 Figma(5652:2948) 기준: 전체 · 충전 · 출금 · 예치 · 환급
    ALL("전체", null),
    CHARGE("충전", PointTransactionTypeDto.CHARGE),
    WITHDRAW("출금", PointTransactionTypeDto.WITHDRAW),
    DEPOSIT("예치", PointTransactionTypeDto.DEPOSIT),
    REFUND("환급", PointTransactionTypeDto.REFUND),
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

    /**
     * 진행 중인 조회. 새 조회를 시작하기 전에 취소해, 늦게 도착한 옛 응답이
     * 최신 목록과 [nextCursor]를 덮어쓰지 못하게 한다.
     *
     * [load]는 화면이 보일 때마다 불리므로 필터 변경·다음 페이지 요청과 쉽게 겹친다.
     * (예: 필터를 바꾼 직후 화면이 다시 RESUME되면 두 조회가 동시에 돈다.)
     */
    private var loadJob: Job? = null
    private var loadMoreJob: Job? = null

    /**
     * 요약과 거래내역을 함께 조회.
     *
     * 화면이 보일 때마다 호출된다(최초 진입 포함 — `PointWalletScreen`의 `RefreshOnResume`).
     * 그래서 `init`에서 따로 조회하지 않는다. 충전 화면을 다녀오면 잔액뿐 아니라 새 거래도
     * 목록에 들어와야 하므로 둘 다 새로 읽는다.
     */
    fun load() {
        loadJob = restartLoad {
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
        loadJob = restartLoad { loadFirstPage() }
    }

    /**
     * 이전 조회(추가 로드 포함)를 모두 끊고 [block]을 새로 시작한다.
     * 취소된 추가 로드는 `isLoadingMore`를 되돌리지 못하므로 여기서 함께 내린다.
     */
    private fun restartLoad(block: suspend () -> Unit): Job {
        loadJob?.cancel()
        loadMoreJob?.cancel()
        _uiState.update { it.copy(isLoadingMore = false) }
        return viewModelScope.launch { block() }
    }

    /** 목록 끝에 도달했을 때 다음 페이지를 이어 받는다. */
    fun loadMore() {
        val state = _uiState.value
        val cursor = nextCursor
        if (state.isLoading || state.isLoadingMore || !state.hasNext || cursor == null) return

        loadMoreJob = viewModelScope.launch {
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
