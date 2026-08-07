package com.example.onuldo_fe.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [Validators] 규칙 검증.
 *
 * 기준은 화면설계서 — 로그인 `AUTH_LOGIN_001`, 회원가입 `AUTH_SIGNUP_001`,
 * 프로필 `ONBD_PROFILE_001`. 문구까지 함께 검증해, 설계서 문구가 바뀌면 테스트가 먼저 깨지도록 한다.
 */
class ValidatorsTest {

    // ===== 이메일 (AUTH_LOGIN_001) =====

    @Test
    fun `정상 형식의 이메일을 통과시킨다`() {
        assertTrue(Validators.isValidEmail("hyeon@onuldo.site"))
        assertTrue(Validators.isValidEmail("test.user+tag@example.co.kr"))
        assertTrue(Validators.isValidEmail("a_b-c%d@sub.domain.com"))
    }

    @Test
    fun `형식이 어긋난 이메일을 거른다`() {
        assertFalse(Validators.isValidEmail(""))
        assertFalse(Validators.isValidEmail("hyeon"))
        assertFalse(Validators.isValidEmail("hyeon@"))
        assertFalse(Validators.isValidEmail("@onuldo.site"))
        assertFalse(Validators.isValidEmail("hyeon@@onuldo.site"))
        assertFalse(Validators.isValidEmail("hyeon onuldo@test.com"))
    }

    @Test
    fun `최상위 도메인은 두 글자 이상이어야 한다`() {
        assertTrue(Validators.isValidEmail("hyeon@test.co"))
        assertFalse(Validators.isValidEmail("hyeon@test.c"))
    }

    @Test
    fun `이메일 앞뒤 공백은 무시한다`() {
        // 사용자가 붙여넣기로 입력할 때 흔한 경우.
        assertTrue(Validators.isValidEmail("  hyeon@onuldo.site  "))
    }

    // ===== 비밀번호 (AUTH_SIGNUP_001) =====

    @Test
    fun `영문 숫자 특수문자를 모두 갖춘 8자 이상 비밀번호를 통과시킨다`() {
        assertNull(Validators.passwordErrorMessage("onuldo1!"))
        assertNull(Validators.passwordErrorMessage("Abcd1234!@"))
        assertTrue(Validators.isValidPassword("onuldo1!"))
    }

    @Test
    fun `미입력 비밀번호는 오류 문구를 내지 않는다`() {
        // 화면 진입 직후 빈 입력창에 빨간 문구가 뜨지 않도록 하는 규칙.
        assertNull(Validators.passwordErrorMessage(""))
    }

    @Test
    fun `미입력 비밀번호는 유효하지 않다`() {
        // 오류 문구는 없지만 '다음' 버튼은 비활성이어야 하므로 isValidPassword는 false다.
        assertFalse(Validators.isValidPassword(""))
    }

    @Test
    fun `영문 숫자 특수문자 중 하나라도 빠지면 조합 오류다`() {
        val expected = "영문·숫자·특수문자 조합이어야 합니다"
        assertEquals(expected, Validators.passwordErrorMessage("onuldopw"))   // 숫자·특수 없음
        assertEquals(expected, Validators.passwordErrorMessage("onuldo123"))  // 특수 없음
        assertEquals(expected, Validators.passwordErrorMessage("12345678!"))  // 영문 없음
    }

    @Test
    fun `공백은 특수문자로 인정하지 않는다`() {
        assertEquals(
            "영문·숫자·특수문자 조합이어야 합니다",
            Validators.passwordErrorMessage("onuldo 1234"),
        )
    }

    @Test
    fun `비밀번호 길이는 8자 이상 20자 이하여야 한다`() {
        val expected = "8~20자 이내로 입력해주세요"
        assertEquals(expected, Validators.passwordErrorMessage("onul1!a"))            // 7자
        assertNull(Validators.passwordErrorMessage("onuld1!a"))                       // 8자
        assertNull(Validators.passwordErrorMessage("onuldo12!Abcdefgh345"))             // 20자
        assertEquals(expected, Validators.passwordErrorMessage("onuldo12!Abcdefgh3456")) // 21자
    }

    @Test
    fun `조합 검사가 길이 검사보다 먼저다`() {
        // 둘 다 위반한 짧은 영문 비밀번호는 조합 문구를 먼저 보여준다.
        assertEquals("영문·숫자·특수문자 조합이어야 합니다", Validators.passwordErrorMessage("abc"))
    }

    @Test
    fun `동일 문자를 4회 이상 연속하면 거른다`() {
        val expected = "동일한 문자를 4회 이상 반복할 수 없습니다"
        assertEquals(expected, Validators.passwordErrorMessage("aaaa123!"))
        assertEquals(expected, Validators.passwordErrorMessage("on1!1111"))
        // 3회 연속까지는 허용한다.
        assertNull(Validators.passwordErrorMessage("aaa1234!"))
    }

    @Test
    fun `대소문자가 다르면 연속 반복으로 보지 않는다`() {
        assertNull(Validators.passwordErrorMessage("aAaA123!"))
    }

    @Test
    fun `비밀번호에 이메일 아이디를 포함할 수 없다`() {
        val expected = "이메일 또는 닉네임을 포함할 수 없습니다"
        assertEquals(
            expected,
            Validators.passwordErrorMessage(password = "hyeon12!@", email = "hyeon@onuldo.site"),
        )
        // 도메인이 아니라 @ 앞부분(로컬파트)만 비교한다.
        assertNull(
            Validators.passwordErrorMessage(password = "onuldo12!@", email = "hyeon@onuldo.site"),
        )
    }

    @Test
    fun `비밀번호에 닉네임을 포함할 수 없다`() {
        assertEquals(
            "이메일 또는 닉네임을 포함할 수 없습니다",
            Validators.passwordErrorMessage(password = "chally123!", nickname = "chally"),
        )
    }

    @Test
    fun `이메일 닉네임 포함 검사는 대소문자를 구분하지 않는다`() {
        assertEquals(
            "이메일 또는 닉네임을 포함할 수 없습니다",
            Validators.passwordErrorMessage(password = "HYEON12!@", email = "hyeon@onuldo.site"),
        )
    }

    @Test
    fun `한 글자짜리 이메일 아이디나 닉네임은 포함 검사에서 제외한다`() {
        // 한 글자까지 막으면 정상 비밀번호가 과도하게 걸린다.
        assertNull(Validators.passwordErrorMessage(password = "onuldo1!", email = "o@test.com"))
        assertNull(Validators.passwordErrorMessage(password = "onuldo1!", nickname = "o"))
    }

    // ===== 닉네임 (ONBD_PROFILE_001) =====

    @Test
    fun `한글 영문 숫자 조합 닉네임을 통과시킨다`() {
        assertNull(Validators.nicknameErrorMessage("오늘두"))
        assertNull(Validators.nicknameErrorMessage("onuldo"))
        assertNull(Validators.nicknameErrorMessage("오늘두2026"))
        assertTrue(Validators.isValidNickname("오늘두"))
    }

    @Test
    fun `미입력 닉네임은 오류 문구를 내지 않지만 유효하지도 않다`() {
        assertNull(Validators.nicknameErrorMessage(""))
        assertNull(Validators.nicknameErrorMessage("   "))
        assertFalse(Validators.isValidNickname(""))
        assertFalse(Validators.isValidNickname("   "))
    }

    @Test
    fun `닉네임 길이는 2자 이상 8자 이하여야 한다`() {
        // 서버 검증 기준(2~8자). Figma 표기는 2~10자라 문구 수정 요청 대기 중.
        assertEquals("2자 이상 입력해주세요", Validators.nicknameErrorMessage("두"))
        assertNull(Validators.nicknameErrorMessage("오늘"))
        assertNull(Validators.nicknameErrorMessage("오늘두챌린지도전"))       // 8자
        assertEquals("8자 이내로 입력해주세요", Validators.nicknameErrorMessage("오늘두챌린지도전기")) // 9자
    }

    @Test
    fun `닉네임 길이는 공백을 제외하고 센다`() {
        assertNull(Validators.nicknameErrorMessage("  오늘두  "))
    }

    @Test
    fun `닉네임에 특수문자와 공백을 쓸 수 없다`() {
        val expected = "특수문자는 사용할 수 없어요"
        assertEquals(expected, Validators.nicknameErrorMessage("오늘두!"))
        assertEquals(expected, Validators.nicknameErrorMessage("onul_do"))
        assertEquals(expected, Validators.nicknameErrorMessage("오늘 두"))   // 가운데 공백
        assertEquals(expected, Validators.nicknameErrorMessage("오늘두🔥"))
    }

    @Test
    fun `단독 자음 모음 닉네임을 거른다`() {
        // 완성형 한글(가~힣)만 허용하므로 'ㅋㅋ' 같은 입력은 걸러진다.
        assertEquals("특수문자는 사용할 수 없어요", Validators.nicknameErrorMessage("ㅋㅋ"))
    }
}
