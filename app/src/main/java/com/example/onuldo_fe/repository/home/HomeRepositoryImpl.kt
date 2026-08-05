package com.example.onuldo_fe.repository.home

import com.example.onuldo_fe.data.home.api.HomeApi
import com.example.onuldo_fe.data.home.api.RealHomeApi
import com.example.onuldo_fe.data.home.dto.HomeChallengeDto
import com.example.onuldo_fe.data.home.dto.HomeCompletedChallengeDto
import com.example.onuldo_fe.data.home.dto.HomePartyChallengeDto
import com.example.onuldo_fe.data.home.dto.HomeResponseDto
import com.example.onuldo_fe.data.home.dto.RealHomeDailyChallengeDto
import com.example.onuldo_fe.data.challenge.dto.DailyCompletedResultDto
import com.example.onuldo_fe.data.party.api.RealPartyApi
import com.example.onuldo_fe.data.party.dto.PartyFeedDto
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
import kotlinx.coroutines.awaitAll
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

            val response = dailyDeferred.await()
            if (!response.isSuccessful) throw HttpException(response)
            val body = response.body() ?: throw IOException("오늘 챌린지 응답 본문이 비어 있습니다.")

            val dailyItems = body.result.challenges
            val partyFeeds = getPartyFeeds(dailyItems)
            val profile = profileDeferred.await()

            dailyItems.toHomeData(
                now = nowProvider(),
                partyFeeds = partyFeeds,
                completed = completedDeferred.await()
            ).copy(
                userName = profile.nickname,
                userProfileImageUrl = profile.profileImageUrl
            )
        }
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
        DailyCompletedResultDto()
    }

    /** /daily에 partyId가 있는 항목만 파티 피드와 연결한다. */
    private suspend fun getPartyFeeds(
        items: List<RealHomeDailyChallengeDto>
    ): Map<Long, PartyFeedDto> = coroutineScope {
        val api = realPartyApi ?: return@coroutineScope emptyMap()
        items.asSequence()
            .filter { it.participationType == "PARTY" }
            .mapNotNull(RealHomeDailyChallengeDto::partyId)
            .distinct()
            .map { partyId ->
                async {
                    try {
                        val response = api.getPartyFeed(partyId)
                        if (response.isSuccessful) response.body()?.result else null
                    } catch (error: CancellationException) {
                        throw error
                    } catch (error: Exception) {
                        null
                    }
                }
            }
            .toList()
            .awaitAll()
            .filterNotNull()
            .associateBy(PartyFeedDto::partyId)
    }
}

private data class HomeProfile(
    val nickname: String = "",
    val profileImageUrl: String? = null
)

/** 사진으로 전달받은 /daily 명세의 값만 홈 화면 모델로 변환한다. */
internal fun List<RealHomeDailyChallengeDto>.toHomeData(
    now: LocalDateTime,
    partyFeeds: Map<Long, PartyFeedDto> = emptyMap(),
    completed: DailyCompletedResultDto = DailyCompletedResultDto()
): HomeData {
    // 서버의 참여 유형으로 개인과 파티 카드를 나눈다.
    val personalChallenges = filter { it.participationType == "PERSONAL" }
        .map { it.toPersonalModel(now) }
    val partyChallenges = filter { it.participationType == "PARTY" }
        .map { it.toPartyModel(now, it.partyId?.let(partyFeeds::get)) }
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
        settlementBanner = null
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

private fun RealHomeDailyChallengeDto.toPartyModel(
    now: LocalDateTime,
    feed: PartyFeedDto?
): HomePartyChallenge {
    val deadline = timeEnd.toLocalTimeOrNull()
    return HomePartyChallenge(
        // /daily 응답에는 파티명이 없으므로 챌린지명을 기본 제목으로 사용한다.
        title = feed?.name ?: partyName?.takeIf(String::isNotBlank) ?: challengeName,
        subtitle = if (feed != null || !partyName.isNullOrBlank()) challengeName else "",
        remainingDays = endDate.remainingDaysFrom(now.toLocalDate()),
        deadlineAt = deadline,
        // 파티별 인원 및 멤버 목록은 /daily 응답에 없으므로 임의 생성하지 않는다.
        completedMemberCount = feed?.verifiedMemberCount ?: 0,
        totalMemberCount = feed?.totalMemberCount ?: 0,
        status = toChallengeStatus(now.toLocalTime()),
        verifiedAt = null,
        remainingMinutes = deadline?.remainingMinutesFrom(now.toLocalTime(), verifiedOnDate),
        canVerify = canVerifyAt(now.toLocalTime()),
        members = feed?.members.orEmpty().map {
            HomePartyMember(
                memberId = it.userId.toString(),
                profileImageUrl = it.profileImageUrl,
                defaultCharacterId = null,
                isVerifiedToday = it.isVerifiedToday
            )
        }
    )
}

private fun DailyCompletedResultDto.toHomeCompletedChallenges(): List<HomeCompletedChallenge> =
    parties.map {
        HomeCompletedChallenge.Party(
            time = it.verifiedAt.toHomeTimeText(),
            title = it.partyName,
            completedMemberCount = it.verifiedMemberCount,
            totalMemberCount = it.totalMemberCount
        )
    } + challenges.map {
        HomeCompletedChallenge.Personal(
            time = it.verifiedAt.toHomeTimeText(),
            title = it.challengeName,
            streakDays = it.streakDays
        )
    }

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
