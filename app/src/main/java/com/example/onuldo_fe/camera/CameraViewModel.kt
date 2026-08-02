package com.example.onuldo_fe.camera

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
    data object Loading : VerificationSubmitState
    data class Success(val fileId: String) : VerificationSubmitState
    data class Error(val message: String) : VerificationSubmitState
}

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = VerificationRepositoryProvider.create(application)
    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri = _imageUri.asStateFlow()
    private val _submitState = MutableStateFlow<VerificationSubmitState>(VerificationSubmitState.Idle)
    val submitState = _submitState.asStateFlow()

    fun setImageUri(uri: Uri?) {
        if (_imageUri.value != uri) deleteLocalPhoto()
        _imageUri.value = uri
        _submitState.value = VerificationSubmitState.Idle
    }

    fun uploadImage() {
        if (_submitState.value == VerificationSubmitState.Loading) return
        val uri = _imageUri.value ?: run {
            _submitState.value = VerificationSubmitState.Error("제출할 사진이 없습니다. 다시 촬영해 주세요.")
            return
        }
        viewModelScope.launch {
            _submitState.value = VerificationSubmitState.Loading
            try {
                val result = repository.uploadImage(uri)
                _submitState.value = VerificationSubmitState.Success(result.fileId)
                deleteLocalPhoto()
                _imageUri.value = null
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                // 실패 시 imageUri를 지우지 않아 같은 촬영본으로 재시도할 수 있다.
                _submitState.value = VerificationSubmitState.Error(error.toUserMessage())
            }
        }
    }

    fun discardPhoto() {
        deleteLocalPhoto()
        _imageUri.value = null
        _submitState.value = VerificationSubmitState.Idle
    }

    fun clearSubmitState() {
        _submitState.value = VerificationSubmitState.Idle
    }

    private fun deleteLocalPhoto() {
        _imageUri.value?.takeIf { it.scheme == "file" }?.path?.let(::File)?.delete()
    }

    private fun Throwable.toUserMessage(): String = when (this) {
        is IllegalArgumentException -> message ?: "사진 정보를 확인해 주세요."
        is IOException -> "네트워크에 연결할 수 없습니다. 촬영본은 유지했으니 다시 시도해 주세요."
        is HttpException -> when (code()) {
            400 -> "사진 정보가 올바르지 않습니다."
            401 -> "로그인이 만료되었습니다. 다시 로그인해 주세요."
            413 -> "사진 용량이 너무 큽니다. 다시 촬영해 주세요."
            415 -> "JPEG 형식의 사진만 제출할 수 있습니다."
            in 500..599 -> "서버에 문제가 생겼습니다. 촬영본은 유지했으니 잠시 후 다시 시도해 주세요."
            else -> "사진 업로드에 실패했습니다. 다시 시도해 주세요."
        }
        else -> "사진 업로드에 실패했습니다. 다시 시도해 주세요."
    }
}