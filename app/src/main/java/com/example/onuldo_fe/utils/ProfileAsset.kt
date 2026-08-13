package com.example.onuldo_fe.utils

import com.example.onuldo_fe.BuildConfig
/**
 * 기본 프로필 캐릭터와 서버 `profileImageUrl`(프리셋 URL) 사이의 변환.
 * 회원가입 시 `profileImageUrl`은 **필수**이며 위 9종 중 하나여야 한다(그 외 값은 서버 400,
 * 단 커스텀 업로드 URL은 예외로 그대로 저장). 프리셋이 아닌 URL은 [toCharacterIndex]가 null을
 * 돌려주므로 화면은 기본 아바타로 대체한다.
 */
object ProfileAsset {

    //env// S3에 저장된 프로필 프리셋 이미지의 기본 URL. 파일명은 `1.png` ~ `9.png`.
    private val BASE_URL = BuildConfig.PROFILE_BASE_URL
    /** 앱이 제공하는 캐릭터 수. `ProfileCharacters` 목록 크기와 일치해야 한다. */
    const val CHARACTER_COUNT = 9

    /** 캐릭터 그리드 인덱스(0부터) → 프리셋 이미지 URL. (0 → 1.png … 8 → 9.png) */
    fun fromCharacterIndex(index: Int): String = "$BASE_URL${index + 1}.png"

    /**
     * 프로필 이미지 URL → 캐릭터 그리드 인덱스.
     * 프리셋이 아니거나(커스텀 업로드 URL 등) 앱 범위를 벗어나면 null.
     */
    fun toCharacterIndex(profileImageUrl: String?): Int? {
        val raw = profileImageUrl?.trim().orEmpty()
        if (!raw.startsWith(BASE_URL)) return null

        val number = raw.removePrefix(BASE_URL).removeSuffix(".png").toIntOrNull() ?: return null
        val index = number - 1
        return index.takeIf { it in 0 until CHARACTER_COUNT }
    }
}
