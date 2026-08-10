package com.example.onuldo_fe.viewmodel.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.model.record.ChallengeRecordType
import com.example.onuldo_fe.model.record.CompletedResultStatus
import com.example.onuldo_fe.repository.record.RecordRepository
import com.example.onuldo_fe.repository.record.RecordRepositoryProvider
import com.example.onuldo_fe.ui.screen.record.data.CompleteRecord
import com.example.onuldo_fe.ui.screen.record.data.ProgressRecord
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import retrofit2.HttpException
import java.io.IOException

data class RecordUiState(
    val isLoading: Boolean = false,
    val ongoingRecords: List<ProgressRecord> = emptyList(),
    val completedRecords: List<CompleteRecord> = emptyList(),
    val totalCompletedCount: Int = 0,
    val successRate: Int = 0,
    val totalSavedAmount: Int = 0,
    val errorMessage: String? = null
)

class RecordViewModel(
    private val repository: RecordRepository = RecordRepositoryProvider.create()
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecordUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    fun loadRecords() = fetchRecords(showLoading = true)

    fun refreshRecords() = fetchRecords(showLoading = false)

    private fun fetchRecords(showLoading: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = showLoading || _uiState.value.isLoading,
                errorMessage = null
            )
            try {
                val (ongoing, completed) = supervisorScope {
                    val ongoingRequest = async { repository.getOngoingChallenges() }
                    val completedRequest = async { repository.getCompletedChallenges() }
                    ongoingRequest.await() to completedRequest.await()
                }

                _uiState.value = RecordUiState(
                    ongoingRecords = ongoing.map { item ->
                        ProgressRecord(
                            participationId = item.participationId,
                            challengeId = item.challengeId,
                            category = when (item.type) {
                                ChallengeRecordType.PERSONAL -> "개인 챌린지"
                                ChallengeRecordType.PARTY -> "파티 챌린지"
                                ChallengeRecordType.UNKNOWN -> "챌린지"
                            },
                            title = item.title,
                            dDay = item.daysUntilEnd,
                            progress = item.achievementRate,
                            depositAmount = item.depositAmount,
                            isTodayVerified = item.isVerifiedToday
                        )
                    },
                    completedRecords = completed.challenges.map { item ->
                        CompleteRecord(
                            participationId = item.participationId,
                            challengeId = item.challengeId,
                            isSuccess = item.resultStatus == CompletedResultStatus.SUCCESS,
                            title = item.title,
                            progress = item.achievementRate,
                            completeDate = item.endedDate,
                            depositAmount = item.depositAmount,
                            point = item.adjustmentAmount
                        )
                    },
                    totalCompletedCount = completed.totalCompletedCount,
                    successRate = completed.successRate,
                    totalSavedAmount = completed.totalSavedAmount
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.toMessage())
            }
        }
    }

    fun loadOngoingRecords() = loadRecords()

    private fun Throwable.toMessage(): String = when (this) {
        is IOException -> "네트워크 연결을 확인해 주세요."
        is HttpException -> when (code()) {
            401 -> "로그인이 만료되었습니다.\n다시 로그인해 주세요."
            in 500..599 -> "서버에 문제가 생겼습니다. 잠시 후 다시 시도해 주세요."
            else -> "챌린지 기록을 불러오지 못했습니다."
        }
        else -> "챌린지 기록을 불러오지 못했습니다."
    }
}
