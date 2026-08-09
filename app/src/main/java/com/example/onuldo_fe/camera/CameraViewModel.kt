package com.example.onuldo_fe.camera

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.onuldo_fe.model.verification.ChallengeVerificationResult
import com.example.onuldo_fe.model.verification.VerificationReview
import com.example.onuldo_fe.repository.verification.VerificationRepositoryProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.io.IOException

sealed interface VerificationSubmitState {
    data object Idle : VerificationSubmitState
    data object Uploading : VerificationSubmitState
    data object Reviewing : VerificationSubmitState
    data class Success(val result: ChallengeVerificationResult) : VerificationSubmitState
    data class Failure(val message: String) : VerificationSubmitState
    data class Waiting(val result: ChallengeVerificationResult) : VerificationSubmitState
    data class Error(val message: String) : VerificationSubmitState
}

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = VerificationRepositoryProvider.create(application)
    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri = _imageUri.asStateFlow()
    private val _submitState = MutableStateFlow<VerificationSubmitState>(VerificationSubmitState.Idle)
    val submitState = _submitState.asStateFlow()
    private var uploadedFileId: String? = null
    var activeChallengeId: Long? = null
        private set
    var activeCategory: String = ""
        private set
    var activeTitle: String = ""
        private set
    var activeDeadline: String = ""
        private set
    var activeVerifiedAt: String? = null
        private set

    fun setImageUri(uri: Uri?) {
        if (_imageUri.value != uri) deleteLocalPhoto()
        _imageUri.value = uri
        uploadedFileId = null
        _submitState.value = VerificationSubmitState.Idle
    }

    fun submitVerification(challengeId: Long, category: String, title: String, deadline: String) {
        if (challengeId <= 0L) {
            _submitState.value = VerificationSubmitState.Error("챌린지 정보를 확인할 수 없습니다.")
            return
        }
        if (_submitState.value == VerificationSubmitState.Uploading ||
            _submitState.value == VerificationSubmitState.Reviewing
        ) return

        activeChallengeId = challengeId
        activeCategory = category
        activeTitle = title
        activeDeadline = deadline
        activeVerifiedAt = null
        val uri = _imageUri.value ?: run {
            _submitState.value = VerificationSubmitState.Error(
                "제출할 사진이 없습니다. 다시 촬영해 주세요."
            )
            return
        }

        viewModelScope.launch {
            try {
                val fileId = uploadedFileId ?: run {
                    _submitState.value = VerificationSubmitState.Uploading
                    repository.uploadImage(uri).fileId.also { uploadedFileId = it }
                }

                _submitState.value = VerificationSubmitState.Reviewing
                val result = repository.verifyChallenge(challengeId, fileId)
                activeVerifiedAt = result.verifiedAt
                when (result.review) {
                    VerificationReview.PASS ->
                        _submitState.value = VerificationSubmitState.Success(result)

                    VerificationReview.AUTO_FAIL ->
                        _submitState.value = VerificationSubmitState.Failure(
                            "사진이 챌린지 인증 조건을 충족하지 못했어요."
                        )

                    VerificationReview.PENDING,
                    VerificationReview.MANUAL_REVIEW ->
                        _submitState.value = VerificationSubmitState.Waiting(result)
                }
                deleteSubmittedPhoto()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _submitState.value = VerificationSubmitState.Error(error.toUserMessage())
            }
        }
    }

    fun discardPhoto() {
        deleteLocalPhoto()
        _imageUri.value = null
        uploadedFileId = null
        _submitState.value = VerificationSubmitState.Idle
    }

    fun clearSubmitState() {
        _submitState.value = VerificationSubmitState.Idle
    }

    private fun deleteSubmittedPhoto() {
        deleteLocalPhoto()
        _imageUri.value = null
        uploadedFileId = null
    }

    private fun deleteLocalPhoto() {
        _imageUri.value?.takeIf { it.scheme == "file" }?.path?.let(::File)?.delete()
    }

    private fun Throwable.toUserMessage(): String = when (this) {
        is IllegalArgumentException -> message ?: "사진 정보를 확인해 주세요."
        is IOException -> "네트워크에 연결할 수 없습니다. 촬영본은 유지되니 다시 시도해 주세요."
        is HttpException -> {
            val serverError = runCatching { response()?.errorBody()?.string().orEmpty() }.getOrDefault("")
            when {
                code() == 400 -> "사진 형식이나 파일 정보를 확인해 주세요."
                code() == 401 -> "로그인이 만료되었습니다. 다시 로그인해 주세요."
                code() == 404 && serverError.contains("PARTICIPATION_NOT_FOUND") ->
                    "참여 중인 챌린지를 찾을 수 없습니다."
                code() == 404 -> "챌린지를 찾을 수 없습니다."
                code() == 409 && serverError.contains("DUPLICATE_VERIFICATION_PHOTO") ->
                    "이미 인증에 사용한 사진입니다. 다시 촬영해 주세요."
                code() == 409 && serverError.contains("ALREADY_VERIFIED_TODAY") ->
                    "오늘은 이미 인증을 완료했습니다."
                code() == 409 && serverError.contains("CHALLENGE_PARTICIPATION_ENDED") ->
                    "챌린지 참여 기간이 종료되어 인증할 수 없어요."
                code() == 409 && serverError.contains("CHALLENGE_VERIFICATION_TIME_UNAVAILABLE") ->
                    "지금은 인증 가능 시간이 아니에요."
                code() == 413 -> "사진 용량이 너무 큽니다. 다시 촬영해 주세요."
                code() == 415 -> "JPEG 형식의 사진만 제출할 수 있습니다."
                code() in 500..599 ->
                    "서버에 문제가 발생했습니다. 촬영본은 유지되니 잠시 후 다시 시도해 주세요."
                else -> "인증 요청에 실패했습니다. 다시 시도해 주세요."
            }
        }
        else -> "인증 요청에 실패했습니다. 다시 시도해 주세요."
    }
}
