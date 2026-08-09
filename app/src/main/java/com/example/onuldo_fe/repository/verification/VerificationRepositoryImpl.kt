package com.example.onuldo_fe.repository.verification

import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.example.onuldo_fe.data.verification.api.VerificationApi
import com.example.onuldo_fe.data.verification.dto.ChallengeVerificationRequestDto
import com.example.onuldo_fe.data.verification.dto.ChallengeVerificationResultDto
import com.example.onuldo_fe.data.verification.dto.ImageUploadResultDto
import com.example.onuldo_fe.model.verification.ChallengeVerificationResult
import com.example.onuldo_fe.model.verification.ImageUploadResult
import com.example.onuldo_fe.model.verification.VerificationReview
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class VerificationRepositoryImpl(
    private val context: Context,
    private val api: VerificationApi
) : VerificationRepository {
    override suspend fun uploadImage(imageUri: Uri): ImageUploadResult = withContext(Dispatchers.IO) {
        val (file, temporaryCopy) = imageUri.toLocalFile()
        try {
            require(file.length() in 1..MAX_IMAGE_BYTES) {
                "사진은 비어 있지 않고 20MB 이하여야 합니다."
            }
            validateImage(file)
            val body = file.asRequestBody(JPEG_MEDIA_TYPE)
            val part = MultipartBody.Part.createFormData("file", file.name, body)
            val result = api.uploadImage(part).result
            require(result.fileId.isNotBlank()) { "서버가 파일 ID를 반환하지 않았습니다." }
            result.toModel()
        } catch (error: CancellationException) {
            throw error
        } finally {
            if (temporaryCopy) file.delete()
        }
    }

    override suspend fun verifyChallenge(
        challengeId: Long,
        fileId: String
    ): ChallengeVerificationResult = withContext(Dispatchers.IO) {
        require(challengeId > 0L) { "챌린지 정보가 올바르지 않습니다." }
        require(fileId.isNotBlank()) { "업로드된 사진 정보가 없습니다." }
        api.verifyChallenge(
            challengeId = challengeId,
            request = ChallengeVerificationRequestDto(fileId)
        ).result.toModel()
    }
    private fun validateImage(file: File) {
        val exif = ExifInterface(file)
        val width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0)
        val height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0)
        require(width > 0 && height > 0 && maxOf(width, height) <= 1920) {
            "사진 크기는 장변 1920px 이하여야 합니다."
        }
        require(width * 4 == height * 3 || width * 3 == height * 4) {
            "사진 비율은 4:3이어야 합니다."
        }
        val capturedAt = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
            ?: exif.getAttribute(ExifInterface.TAG_DATETIME)
        require(!capturedAt.isNullOrBlank()) {
            "촬영 시각 정보가 없는 사진은 제출할 수 없습니다."
        }
    }

    private fun Uri.toLocalFile(): Pair<File, Boolean> {
        if (scheme == "file") return requireNotNull(path).let(::File) to false
        val target = File.createTempFile("verification_upload_", ".jpg", context.cacheDir)
        try {
            context.contentResolver.openInputStream(this)?.use { input ->
                target.outputStream().use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var copiedBytes = 0L

                    while (true) {
                        val readBytes = input.read(buffer)
                        if (readBytes < 0) break

                        copiedBytes += readBytes
                        require(copiedBytes <= MAX_IMAGE_BYTES) {
                            "사진은 비어 있지 않고 20MB 이하여야 합니다."
                        }
                        output.write(buffer, 0, readBytes)
                    }
                }
            } ?: error("사진 파일을 열 수 없습니다.")
            return target to true
        } catch (error: Throwable) {
            target.delete()
            throw error
        }
    }

    private fun ChallengeVerificationResultDto.toModel() = ChallengeVerificationResult(
        verificationId = verificationId,
        challengeId = challengeId,
        participationId = participationId,
        fileId = fileId,
        verificationDate = verificationDate,
        verifiedAt = verifiedAt,
        review = runCatching { VerificationReview.valueOf(review.uppercase()) }
            .getOrDefault(VerificationReview.MANUAL_REVIEW)
    )
    private fun ImageUploadResultDto.toModel() = ImageUploadResult(
        bucket = bucket,
        fileId = fileId,
        url = url,
        contentType = contentType,
        size = size
    )

    private companion object {
        const val MAX_IMAGE_BYTES = 20L * 1024 * 1024
        val JPEG_MEDIA_TYPE = "image/jpeg".toMediaType()
    }
}
