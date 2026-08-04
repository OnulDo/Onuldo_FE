package com.example.onuldo_fe.viewmodel.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.data.auth.dto.TermType
import com.example.onuldo_fe.data.network.onError
import com.example.onuldo_fe.data.network.onSuccess
import com.example.onuldo_fe.model.term.Term
import com.example.onuldo_fe.repository.term.TermRepository
import com.example.onuldo_fe.repository.term.TermRepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TermUiState(
    val term: Term? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

/** 약관 본문 조회. `GET /api/terms/{termType}` */
class TermViewModel(
    private val termRepository: TermRepository = TermRepositoryProvider.provide(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(TermUiState())
    val uiState: StateFlow<TermUiState> = _uiState.asStateFlow()

    private var loadedType: TermType? = null

    /** 같은 약관을 다시 요청하면 재호출하지 않는다(화면 재구성 대비). */
    fun load(termType: TermType) {
        if (loadedType == termType && _uiState.value.term != null) return
        loadedType = termType

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            termRepository.getTerm(termType)
                .onSuccess { term ->
                    _uiState.update { it.copy(isLoading = false, term = term) }
                }
                .onError { _, message ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                }
        }
    }
}
