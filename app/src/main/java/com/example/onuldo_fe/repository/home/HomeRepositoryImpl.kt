package com.example.onuldo_fe.repository.home

import android.util.Log
import com.example.onuldo_fe.data.home.api.HomeApi
import com.example.onuldo_fe.data.home.api.RealHomeApi
import com.example.onuldo_fe.data.home.dto.HomeChallengeDto
import com.example.onuldo_fe.data.home.dto.HomeCompletedChallengeDto
import com.example.onuldo_fe.data.home.dto.HomePartyChallengeDto
import com.example.onuldo_fe.data.home.dto.HomeResponseDto
import com.example.onuldo_fe.data.home.dto.RealHomeDailyChallengeDto
import com.example.onuldo_fe.data.challenge.dto.DailyCompletedResultDto
import com.example.onuldo_fe.data.party.api.RealPartyApi
import com.example.onuldo_fe.data.party.dto.PartyHomeItemDto
import com.example.onuldo_fe.data.party.dto.PartyHomeResultDto
import com.example.onuldo_fe.data.user.api.UserApi
import com.example.onuldo_fe.model.home.ChallengeStatus
import com.example.onuldo_fe.model.home.HomeChallenge
import com.example.onuldo_fe.model.home.HomeCompletedChallenge
import com.example.onuldo_fe.model.home.HomeData
import com.example.onuldo_fe.model.home.HomePartyChallenge
import com.example.onuldo_fe.model.home.HomePartyMember
import com.example.onuldo_fe.model.home.SettlementBanner
import com.example.onuldo_fe.model.home.TodayChallenge
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException

class HomeRepositoryImpl(
    private val fakeApi: HomeApi,
    private val realApi: RealHomeApi? = null,
    private val realPartyApi: RealPartyApi? = null,
    private val userApi: UserApi? = null,
    private val useRealDailyApi: Boolean = false,
    private val nowProvider: () -> LocalDateTime = LocalDateTime::now
) : HomeRepository {
    override suspend fun getHome(): HomeData {
        // 설정값이 false면 기존 Fake 홈을 사용한다.
        if (!useRealDailyApi) return fakeApi.getHome().toModel()

        return coroutineScope {
            // 서로 독립적인 오늘 챌린지와 프로필을 동시에 조회한다.
            val dailyDeferred = async {
                requireNotNull(realApi) { "Real 홈 API가 설정되지 않았습니다." }
                    .getDailyChallenges()
            }
            val profileDeferred = async { getProfileOrEmpty() }
            val completedDeferred = async { getCompletedOrEmpty() }
            // 홈 전용 파티 API는 카드 정보와 미확인 정산 배너를 한 번에 반환한다.
            val partyHomeDeferred = async { getPartyHome() }

            val response = dailyDeferred.await()
            if (!response.isSuccessful) throw HttpException(response)
            val body = response.body() ?: throw IOException("오늘 챌린지 응답 본문이 비어 있습니다.")

            val dailyItems = body.result.challenges
            val partyHome = partyHomeDeferred.await()
            val profile = profileDeferred.await()

            dailyItems.toHomeData(
                now = nowProvider(),
                partyHome = partyHome,
                completed = completedDeferred.await()
            ).copy(
                userName = profile.nickname,
                userProfileImageUrl = profile.profileImageUrl
            )
        }
    }

    /** 홈 전용 API에서 진행 중 파티 카드와 미확인 정산 배너를 조회한다. */
    private suspend fun getPartyHome(): PartyHomeResultDto {
        val api = requireNotNull(realPartyApi) { "Real 파티 API가 설정되지 않았습니다." }
        val response = api.getHomeParties()
        if (!response.isSuccessful) throw HttpException(response)
        val body = response.body() ?: throw IOException("홈 파티 응답 본문이 비어 있습니다.")
        return body.result
    }

    /** 프로필 실패는 홈 전체 오류로 처리하지 않는다. */
    private suspend fun getProfileOrEmpty(): HomeProfile = try {
        val response = userApi?.getProfile()
        if (response?.isSuccessful == true) {
            response.body()?.result?.let {
                HomeProfile(it.nickname.orEmpty(), it.profileImageUrl)
            } ?: HomeProfile()
        } else {
            HomeProfile()
        }
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        Log.w(TAG, "홈 프로필 조회 실패", error)
        HomeProfile()
    }

    /** 완료 목록 실패 시 진행 중인 홈 카드는 그대로 표시한다. */
    private suspend fun getCompletedOrEmpty(): DailyCompletedResultDto = try {
        val response = realApi?.getDailyCompleted()
        if (response?.isSuccessful == true) {
            response.body()?.result ?: DailyCompletedResultDto()
        } else {
            DailyCompletedResultDto()
        }
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        Log.w(TAG, "오늘 완료 챌린지 조회 실패", error)
        DailyCompletedResultDto()
    }

    private companion object {
        const val TAG = "HomeRepository"
    }
}

private data class HomeProfile(
    val nickname: String = "",
    val profileImageUrl: String? = null
)

/** /daily와 /parties/home 응답을 홈 화면 모델로 합친다. */
internal fun List<RealHomeDailyChallengeDto>.toHomeData(
    now: LocalDateTime,
    partyHome: PartyHomeResultDto = PartyHomeResultDto(),
    completed: DailyCompletedResultDto = DailyCompletedResultDto()
): HomeData {
    // 서버의 참여 유형으로 개인과 파티 카드를 나눈다.
    val personalChallenges = filter { it.participationType == "PERSONAL" }
        .map { it.toPersonalModel(now) }
    // /daily의 partyId로 홈 파티를 찾아 인증에 필요한 challengeId와 category를 연결한다.
    val partyDailyChallenges = filter { it.participationType == "PARTY" }
        .mapNotNull { daily ->
            daily.partyId?.let { partyId -> partyId to daily }
        }
        .toMap()
    // 카드 표시 상태와 파티원 인증 현황은 /parties/home 응답을 기준으로 구성한다.
    val partyChallenges = partyHome.parties.map {
        it.toHomeModel(
            now = now,
            dailyChallenge = partyDailyChallenges[it.partyId]
        )
    }
    val completedCount = count(RealHomeDailyChallengeDto::verifiedOnDate)

    return HomeData(
        // /daily 응답에는 닉네임이 없으므로 다른 사용자 API가 연결되기 전까지 비워 둔다.
        userName = "",
        todayChallenge = takeIf(List<*>::isNotEmpty)?.let {
            TodayChallenge(
                date = now.toLocalDate().toString(),
                progress = completedCount.toFloat() / size,
                completedCount = completedCount,
                totalCount = size
            )
        },
        partyChallenges = partyChallenges,
        challenges = personalChallenges,
        completedChallenges = completed.toHomeCompletedChallenges(),
        // 현재 UI는 배너 한 건만 지원하므로 미확인 정산 중 첫 번째 항목을 노출한다.
        settlementBanner = partyHome.settlementBanners.firstOrNull()?.let {
            SettlementBanner(partyName = it.partyName, partyId = it.partyId)
        }
    )
}

private fun RealHomeDailyChallengeDto.toPersonalModel(now: LocalDateTime): HomeChallenge {
    val deadline = timeEnd.toLocalTimeOrNull() ?: LocalTime.MAX
    return HomeChallenge(
        title = challengeName,
        // 값이 없거나 음수이면 0일로 처리한다.
        streakDays = streakDays?.coerceAtLeast(0) ?: 0,
        remainingDays = endDate.remainingDaysFrom(now.toLocalDate()),
        deadlineAt = deadline,
        status = toChallengeStatus(now.toLocalTime()),
        verifiedAt = null,
        remainingMinutes = deadline.remainingMinutesFrom(now.toLocalTime(), verifiedOnDate),
        canVerify = canVerifyAt(now.toLocalTime())
    )
}

private fun PartyHomeItemDto.toHomeModel(
    now: LocalDateTime,
    dailyChallenge: RealHomeDailyChallengeDto?
): HomePartyChallenge {
    val deadline = verificationDeadline.toLocalTimeOrNull()
    val isDeadlinePassed = deadline?.let(now.toLocalTime()::isAfter) == true
    // 시간으로 상태를 추정하지 않고 서버가 계산한 나의 오늘 인증 상태를 사용한다.
    val challengeStatus = status.toPartyChallengeStatus()
    return HomePartyChallenge(
        title = name,
        subtitle = challengeTitle,
        remainingDays = endDate.remainingDaysFrom(now.toLocalDate()),
        deadlineAt = deadline,
        completedMemberCount = members.count { it.isVerifiedToday },
        totalMemberCount = members.size,
        status = challengeStatus,
        verifiedAt = verifiedAt.toVerifiedTimeOrNull(),
        remainingMinutes = deadline
            ?.takeIf { showRemainingTime }
            ?.remainingMinutesFrom(
                now = now.toLocalTime(),
                verified = challengeStatus != ChallengeStatus.NeedCertification
            ),
        canVerify = challengeStatus == ChallengeStatus.NeedCertification && !isDeadlinePassed,
        members = members.map {
            HomePartyMember(
                memberId = it.userId.toString(),
                profileImageUrl = it.profileImageUrl,
                defaultCharacterId = null,
                isVerifiedToday = it.isVerifiedToday
            )
        }
    )
}

/** Swagger의 홈 파티 인증 상태를 화면 공통 상태로 변환한다. */
private fun String.toPartyChallengeStatus() = when (this) {
    "PENDING" -> ChallengeStatus.WaitingReview
    "SUCCESS" -> ChallengeStatus.Success
    "FAIL" -> ChallengeStatus.Failed
    else -> ChallengeStatus.NeedCertification
}

/** verifiedAt의 date-time 형식과 시간 단독 형식을 모두 화면용 LocalTime으로 변환한다. */
private fun String?.toVerifiedTimeOrNull(): LocalTime? = this?.let { value ->
    runCatching { LocalDateTime.parse(value).toLocalTime() }
        .recoverCatching { LocalTime.parse(value) }
        .getOrNull()
}

private fun DailyCompletedResultDto.toHomeCompletedChallenges(): List<HomeCompletedChallenge> =
    (parties.map {
        it.verifiedAt to HomeCompletedChallenge.Party(
            time = it.verifiedAt.toHomeTimeText(),
            title = it.partyName,
            completedMemberCount = it.verifiedMemberCount,
            totalMemberCount = it.totalMemberCount
        )
    } + challenges.map {
        it.verifiedAt to HomeCompletedChallenge.Personal(
            time = it.verifiedAt.toHomeTimeText(),
            title = it.challengeName,
            streakDays = it.streakDays
        )
    }).sortedBy { (verifiedAt, _) -> verifiedAt }
        .map { (_, challenge) -> challenge }

private fun String.toHomeTimeText(): String =
    runCatching { LocalDateTime.parse(this).toLocalTime().toString().take(5) }
        .getOrElse { substringAfter('T', this).take(5) }

private fun RealHomeDailyChallengeDto.canVerifyAt(now: LocalTime): Boolean {
    // 인증 완료 또는 인증 가능 시간 밖이면 버튼을 숨긴다.
    if (verifiedOnDate) return false
    val start = timeStart.toLocalTimeOrNull()
    val end = timeEnd.toLocalTimeOrNull()
    return (start == null || !now.isBefore(start)) && (end == null || !now.isAfter(end))
}

/** 인증 완료 여부와 마감 시각으로 오늘 카드 상태를 정한다. */
private fun RealHomeDailyChallengeDto.toChallengeStatus(now: LocalTime): ChallengeStatus {
    if (verifiedOnDate) return ChallengeStatus.Success
    val deadline = timeEnd.toLocalTimeOrNull()
    return if (deadline != null && now.isAfter(deadline)) {
        ChallengeStatus.Failed
    } else {
        ChallengeStatus.NeedCertification
    }
}

private fun String?.toLocalTimeOrNull(): LocalTime? =
    this?.let { runCatching { LocalTime.parse(it) }.getOrNull() }

private fun String.remainingDaysFrom(today: LocalDate): Int =
    runCatching {
        // 종료일과 오늘 날짜의 차이로 D-Day를 계산한다.
        ChronoUnit.DAYS.between(today, LocalDate.parse(this)).coerceAtLeast(0).toInt()
    }.getOrDefault(0)

private fun LocalTime.remainingMinutesFrom(now: LocalTime, verified: Boolean): Int? {
    if (verified || now.isAfter(this)) return null
    // 인증 마감 시각과 현재 시각의 차이를 분으로 계산한다.
    return ChronoUnit.MINUTES.between(now, this).toInt()
}

internal fun HomeResponseDto.toModel(): HomeData {
    val partyModels = partyChallenges.map { it.toModel() }
    val challengeModels = challenges.map { it.toModel() }
    val completedModels = completedChallenges.map { it.toModel() }

    val todayModel = todayChallenge?.let { source ->
        val progress = if (source.totalCount == 0) 0f
        else source.completedCount.toFloat() / source.totalCount
        TodayChallenge(source.date, progress, source.completedCount, source.totalCount)
    }

    return HomeData(
        userName = userName,
        todayChallenge = todayModel,
        partyChallenges = partyModels,
        challenges = challengeModels,
        completedChallenges = completedModels,
        settlementBanner = settlementBanner
            ?.takeUnless { it.isChecked }
            ?.let { SettlementBanner(it.partyName, it.partyId) }
    )
}

private fun String.toChallengeStatus() = when (this) {
    "WAITING_REVIEW" -> ChallengeStatus.WaitingReview
    "FAILED" -> ChallengeStatus.Failed
    "SUCCESS" -> ChallengeStatus.Success
    else -> ChallengeStatus.NeedCertification
}

private fun HomeChallengeDto.toModel() = HomeChallenge(
    title = title,
    streakDays = streakDays,
    remainingDays = remainingDays,
    deadlineAt = LocalTime.parse(deadlineAt),
    status = status.toChallengeStatus(),
    verifiedAt = verifiedAt?.let { runCatching { LocalTime.parse(it) }.getOrNull() },
    remainingMinutes = remainingMinutes,
    canVerify = canVerify
)

private fun HomePartyChallengeDto.toModel() = HomePartyChallenge(
    title = title,
    subtitle = subtitle,
    remainingDays = remainingDays,
    deadlineAt = runCatching { LocalTime.parse(deadlineAt) }.getOrNull(),
    completedMemberCount = completedMemberCount,
    totalMemberCount = totalMemberCount,
    status = status.toChallengeStatus(),
    verifiedAt = verifiedAt?.let { runCatching { LocalTime.parse(it) }.getOrNull() },
    remainingMinutes = remainingMinutes,
    canVerify = canVerify,
    members = members.map {
        HomePartyMember(it.memberId, it.profileImageUrl, it.defaultCharacterId, it.isVerifiedToday)
    }
)

private fun HomeCompletedChallengeDto.toModel(): HomeCompletedChallenge = when (type) {
    "PARTY" -> HomeCompletedChallenge.Party(
        time,
        title,
        requireNotNull(completedMemberCount),
        requireNotNull(totalMemberCount)
    )
    "PERSONAL" -> HomeCompletedChallenge.Personal(time, title, requireNotNull(streakDays))
    else -> error("Unsupported completed challenge type: $type")
}
