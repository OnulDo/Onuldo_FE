package com.example.onuldo_fe.utils

/**
 * 입력값 유효성 검사 모음.
 * 규칙·에러 문구는 화면설계서(AUTH_LOGIN_001 · AUTH_SIGNUP_001 · ONBD_PROFILE_001) 기준.
 */
object Validators {
    private val EMAIL_REGEX =
        Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

    // --- 비밀번호 (AUTH_SIGNUP_001) ---
    // 영문+숫자+특수문자 조합, 8~20자, 동일문자 4회 이상 연속 반복 금지, 이메일/닉네임 포함 금지.
    /** 비밀번호 규칙 위반 시 에러 문구, 통과 시 null. 빈 문자열도 null(미입력 상태). */
    fun passwordErrorMessage(password: String, email: String = "", nickname: String = ""): String? {
        if (password.isEmpty()) return null
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() && !it.isWhitespace() }
        if (!(hasLetter && hasDigit && hasSpecial)) return "영문·숫자·특수문자 조합이어야 합니다"
        if (password.length < 8 || password.length > 20) return "8~20자 이내로 입력해주세요"
        if (hasRepeatedRun(password, 4)) return "동일한 문자를 4회 이상 반복할 수 없습니다"
        val emailLocal = email.substringBefore("@").trim()
        val includesEmail = emailLocal.length >= 2 && password.contains(emailLocal, ignoreCase = true)
        val includesNickname = nickname.length >= 2 && password.contains(nickname, ignoreCase = true)
        if (includesEmail || includesNickname) return "이메일 또는 닉네임을 포함할 수 없습니다"
        return null
    }

    fun isValidPassword(password: String, email: String = "", nickname: String = ""): Boolean =
        password.isNotEmpty() && passwordErrorMessage(password, email, nickname) == null

    /** 동일 문자가 [count]회 이상 연속되면 true (예: "aaaa"). */
    private fun hasRepeatedRun(s: String, count: Int): Boolean {
        var run = 1
        for (i in 1 until s.length) {
            if (s[i] == s[i - 1]) {
                run++
                if (run >= count) return true
            } else {
                run = 1
            }
        }
        return false
    }

    // --- 닉네임 (ONBD_PROFILE_001) ---
    // 한글·영문·숫자 2~8자, 특수문자 불가. (욕설·차별 표현 감지는 서버가 담당하며 앱에서는 검사하지 않는다.)
    /** 닉네임 규칙 위반 시 에러 문구, 통과 시 null. 빈 문자열도 null(미입력 상태). */
    fun nicknameErrorMessage(nickname: String): String? {
        val n = nickname.trim()
        if (n.isEmpty()) return null
        if (n.length < 2) return "2자 이상 입력해주세요"
        if (n.length > 8) return "8자 이내로 입력해주세요"
        val allAllowed = n.all { it in '가'..'힣' || it in 'a'..'z' || it in 'A'..'Z' || it in '0'..'9' }
        if (!allAllowed) return "특수문자는 사용할 수 없어요"
        return null
    }

    fun isValidNickname(nickname: String): Boolean =
        nickname.trim().isNotEmpty() && nicknameErrorMessage(nickname) == null
}
