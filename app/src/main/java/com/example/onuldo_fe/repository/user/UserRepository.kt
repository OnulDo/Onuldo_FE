package com.example.onuldo_fe.repository.user

import com.example.onuldo_fe.data.network.ApiResult
import com.example.onuldo_fe.data.network.CursorPage
import com.example.onuldo_fe.data.user.dto.NotificationSettingType
import com.example.onuldo_fe.data.user.dto.PointTransactionTypeDto
import com.example.onuldo_fe.model.user.MyPageSummary
import com.example.onuldo_fe.model.user.NotificationSettings
import com.example.onuldo_fe.model.user.PointChargeResult
import com.example.onuldo_fe.model.user.PointTransaction
import com.example.onuldo_fe.model.user.PointWithdrawResult
import com.example.onuldo_fe.model.user.UserProfile
import com.example.onuldo_fe.model.user.WalletSummary

/**
 * 사용자·포인트 지갑 저장소.
 *
 * 보유 포인트는 이 저장소(충전·가입보너스) 밖에서도 바뀐다 — 챌린지 참여 예치금 차감,
 * 정산 환급 등이 다른 저장소를 거친다. 그래서 잔액 변동을 여기서 신호로 알리지 않고,
 * 잔액을 보여주는 화면이 다시 보일 때 새로 읽는다. (`ui.component.RefreshOnResume`)
 */
interface UserRepository {

    suspend fun getMyPage(): ApiResult<MyPageSummary>

    suspend fun getProfile(): ApiResult<UserProfile>

    /**
     * 프로필(닉네임/사진) 변경. 바꿀 값만 채워 보내고, null은 기존 값을 유지한다.
     * nickname, profileImageUrl 중 변경할 값만 채워서 요청 << 백엔팀!
     */
    suspend fun updateProfile(
        nickname: String? = null,
        profileImageUrl: String? = null,
    ): ApiResult<UserProfile>

    /** 현재 계정 탈퇴. 성공 후에는 기존 토큰이 무효화되므로 호출부에서 세션을 정리해야 한다. */
    suspend fun deleteAccount(): ApiResult<Unit>

    suspend fun getNotificationSettings(): ApiResult<NotificationSettings>

    suspend fun updateNotificationSetting(
        type: NotificationSettingType,
        enabled: Boolean,
    ): ApiResult<Unit>

    suspend fun getWalletSummary(): ApiResult<WalletSummary>

    /** [type]이 null이면 전체 내역. [cursor]는 이전 응답의 `nextCursor`. */
    suspend fun getWalletTransactions(
        type: PointTransactionTypeDto? = null,
        cursor: String? = null,
        size: Int = DEFAULT_PAGE_SIZE,
    ): ApiResult<CursorPage<PointTransaction>>

    suspend fun chargePoint(point: Int): ApiResult<PointChargeResult>

    /** 포인트 출금. 출금된 금액과 출금 후 잔액을 돌려준다. */
    suspend fun withdrawPoint(point: Int): ApiResult<PointWithdrawResult>

    /**
     * 가입 환영 보너스 지급. 서버가 자동 지급하지 않으므로 **가입 성공 직후 앱이 호출**한다.
     * 이미 받은 계정이면 `SIGNUP_BONUS_ALREADY_GRANTED`로 실패한다.
     */
    suspend fun grantSignupBonus(point: Int = SIGNUP_BONUS_POINT): ApiResult<PointChargeResult>

    companion object {
        const val DEFAULT_PAGE_SIZE = 10

        /** 회원가입 화면 안내 문구와 동일한 환영 보너스 금액. */
        const val SIGNUP_BONUS_POINT = 100_000
    }
}
