package com.example.onuldo_fe.utils

/**
 * 기본 프로필 캐릭터와 서버 `profileImageUrl` 문자열 사이의 변환.
 *
 * 서버는 프로필 이미지를 `"default_asset:{번호}"` 형식으로 저장하며, 미지정 시 **1~12 중 랜덤**으로
 * 채운다(백엔드 `AuthService.resolveProfileImageUrl`).
 *
 * ⚠️ 서버 기본 에셋은 12종인데 앱 캐릭터 그리드는 9종(`ProfileCharacters`)뿐이라, 서버가 10~12번을
 * 배정하면 앱에서 보여줄 이미지가 없다. 그 경우 [toCharacterIndex]가 null을 돌려주므로 화면은
 * 기본 아바타로 대체한다. 디자인·백엔드와 개수를 맞추면 이 대체 처리는 없앨 수 있다.
 */
object ProfileAsset {

    private const val PREFIX = "default_asset:"

    /** 앱이 제공하는 캐릭터 수. `ProfileCharacters` 목록 크기와 일치해야 한다. */
    const val CHARACTER_COUNT = 9

    /** 캐릭터 그리드 인덱스(0부터) → 서버 저장 형식. */
    fun fromCharacterIndex(index: Int): String = "$PREFIX${index + 1}"

    /**
     * 서버 문자열 → 캐릭터 그리드 인덱스.
     * 형식이 다르거나(직접 업로드한 URL 등) 앱이 가진 범위를 벗어나면 null.
     */
    fun toCharacterIndex(profileImageUrl: String?): Int? {
        val raw = profileImageUrl?.trim().orEmpty()
        if (!raw.startsWith(PREFIX)) return null

        val number = raw.removePrefix(PREFIX).toIntOrNull() ?: return null
        val index = number - 1
        return index.takeIf { it in 0 until CHARACTER_COUNT }
    }
}
