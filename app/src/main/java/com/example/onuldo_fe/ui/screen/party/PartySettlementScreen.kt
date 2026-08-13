package com.example.onuldo_fe.ui.screen.party

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.model.party.PartySettlementMember
import com.example.onuldo_fe.model.party.PartySettlementMemberStatus
import com.example.onuldo_fe.model.party.PartySettlementResult
import com.example.onuldo_fe.model.party.PartySettlementStatus
import com.example.onuldo_fe.ui.screen.party.components.PartyMemberCard
import com.example.onuldo_fe.ui.screen.party.components.PartyTopBar
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDoTypography
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream
import java.text.NumberFormat
import java.util.Locale

private val SettlementLossRed = Color(0xFFD9534F)

@Composable
fun PartySettlementScreen(
    onBack: () -> Unit,
    result: PartySettlementResult,
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit = onBack
) {
    val spacing = LocalSpacing.current
    val content = result.content()
    // 서버 금액의 부호가 정산의 성격을 결정하며 resultType과는 독립적이다.
    val displayContent = result.displayAmount.displayContent()

    Column(modifier.fillMaxSize().background(SourCream)) {
        PartyTopBar(title = "파티 정산 결과", onBack = onBack)

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(bottom = spacing.spacing16)
        ) {
            item {
                Spacer(Modifier.height(47.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(135.dp)
                            .background(content.characterBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        PartySettlementCharacter(
                            iconRes = content.characterRes,
                            width = content.characterWidth,
                            height = content.characterHeight
                        )
                    }
                }
                Spacer(Modifier.height(spacing.spacing12))
                Text(
                    text = content.title,
                    modifier = Modifier.fillMaxWidth(),
                    color = BlackBrown,
                    style = OnulDoTypography.title1Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = content.description,
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkBrown,
                    style = OnulDoTypography.caption1Regular,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(spacing.spacing50))
                SettlementSectionTitle("내 정산 결과")
                Spacer(Modifier.height(spacing.spacing8))
                SettlementSummaryCard(
                    depositAmount = result.depositAmount.toPointText(),
                    displayLabel = content.displayLabel,
                    displayAmount = result.displayAmount.toSignedPointText(),
                    displayColor = displayContent.color,
                    depositLabelColor = content.depositLabelColor,
                    modifier = Modifier.padding(horizontal = spacing.spacing20)
                )
                Spacer(Modifier.height(spacing.spacing16))
                SettlementSectionTitle("파티원 결과")
                Spacer(Modifier.height(spacing.spacing8))
            }

            items(result.members, key = { it.userId }) { member ->
                PartySettlementMemberCard(
                    member = member,
                    showCompletionStatus = content.showCompletionStatus,
                    modifier = Modifier.padding(horizontal = spacing.spacing20)
                )
                Spacer(Modifier.height(spacing.spacing8))
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().height(138.dp).background(SourCream),
            contentAlignment = Alignment.TopCenter
        ) {
            OnulDoButton(
                text = "확인",
                onClick = onConfirm,
                modifier = Modifier
                    .padding(top = 40.dp),
                height = 52.dp
            )
        }
    }
}

@Composable
private fun SettlementSectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = LocalSpacing.current.spacing24),
        color = BlackBrown,
        style = OnulDoTypography.caption2Bold
    )
}

@Composable
private fun PartySettlementCharacter(
    @DrawableRes iconRes: Int,
    width: Dp,
    height: Dp
) {
    Image(
        painter = painterResource(iconRes),
        contentDescription = null,
        modifier = Modifier.size(width = width, height = height),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun SettlementSummaryCard(
    depositAmount: String,
    displayLabel: String,
    displayAmount: String,
    displayColor: Color,
    depositLabelColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(Persimmon10, RoundedCornerShape(14.dp))
            .border(BorderStroke(1.dp, Persimmon.copy(alpha = 0.2f)), RoundedCornerShape(14.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettlementAmount("도전금", depositAmount, depositLabelColor, BlackBrown, Modifier.weight(1f))
        SettlementAmount(displayLabel, displayAmount, displayColor, displayColor, Modifier.weight(1f))
    }
}

@Composable
private fun SettlementAmount(
    label: String,
    amount: String,
    labelColor: Color,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = labelColor,
            style = OnulDoTypography.caption3Bold
        )
        Text(
            text = amount,
            modifier = Modifier.padding(top = 3.dp),
            color = amountColor,
            style = OnulDoTypography.body2Bold
        )
    }
}

@Composable
private fun PartySettlementMemberCard(
    member: PartySettlementMember,
    showCompletionStatus: Boolean,
    modifier: Modifier = Modifier
) {
    val resultColor = member.displayAmount.displayContent().color

    PartyMemberCard(
        name = member.name,
        profileImageUrl = member.profileImageUrl,
        // 회원이 가입 시 프로필을 필수로 선택하므로 정산 응답에는 기본 캐릭터가 없다.
        defaultCharacterId = null,
        modifier = modifier,
        startPadding = 16.dp,
        endPadding = 20.dp,
        nameWidth = 44.dp,
        middleContent = {
            if (showCompletionStatus) {
                Text(
                    text = member.status.label(),
                    color = member.status.color(),
                    style = OnulDoTypography.caption3Bold
                )
            }
        }
    ) {
        Text(
            text = member.displayAmount.toSignedPointText(),
            modifier = Modifier.width(130.dp),
            color = resultColor,
            style = OnulDoTypography.caption2Bold,
            textAlign = TextAlign.End
        )
    }
}

private data class SettlementContent(
    val title: String,
    val description: String,
    @param:DrawableRes val characterRes: Int,
    val characterWidth: Dp,
    val characterHeight: Dp,
    val characterBackground: Color,
    val depositLabelColor: Color,
    val displayLabel: String,
    val showCompletionStatus: Boolean
)

private fun PartySettlementResult.content() = when (status) {
    PartySettlementStatus.AllSuccess -> SettlementContent(
        title = "전원 성공!",
        description = "파티 전원이 챌린지를 완주했어요",
        characterRes = R.drawable.party_settlement_all_success_icon,
        characterWidth = 73.dp,
        characterHeight = 104.dp,
        characterBackground = Persimmon.copy(alpha = 0.15f),
        depositLabelColor = DarkBrown,
        displayLabel = "성과 보너스",
        showCompletionStatus = false
    )

    PartySettlementStatus.PartialSuccess -> SettlementContent(
        title = "${completedMemberCount}명이 완주했어요",
        description = "미완주 파티원의 도전금이 완주자에게 배분됐어요",
        characterRes = R.drawable.party_settlement_partial_success_icon,
        characterWidth = 116.dp,
        characterHeight = 116.dp,
        characterBackground = Persimmon.copy(alpha = 0.15f),
        depositLabelColor = DarkBrown,
        displayLabel = "도전 분배금",
        showCompletionStatus = true
    )

    PartySettlementStatus.AllFailed -> SettlementContent(
        title = "아쉽게 실패했어요",
        description = "이번엔 아무도 목표를 채우지 못했어요",
        characterRes = R.drawable.party_settlement_all_failed_icon,
        characterWidth = 89.dp,
        characterHeight = 105.dp,
        characterBackground = DarkBrown.copy(alpha = 0.08f),
        depositLabelColor = DarkBrown,
        displayLabel = "차감",
        showCompletionStatus = false
    )
}

private data class SettlementDisplayContent(val label: String, val color: Color)

/**
 * myDisplayAmount·displayAmount는 결과 유형이 아니라 서버가 준 부호로 표시한다.
 * 서버 응답에 금액의 성격(도전금 환급 / 미완주자 분배금 / 성과 보너스)을 구분하는
 * 필드가 없어, 양수를 특정 유형으로 단정하지 않고 중립적인 라벨을 사용한다.
 */
private fun Int.displayContent() = when {
    this > 0 -> SettlementDisplayContent("받은 금액", Green)
    this < 0 -> SettlementDisplayContent("차감", SettlementLossRed)
    else -> SettlementDisplayContent("정산 금액", DarkBrown50)
}

private fun PartySettlementMemberStatus.label() = when (this) {
    PartySettlementMemberStatus.Ongoing -> "진행 중"
    PartySettlementMemberStatus.Success -> "완주"
    PartySettlementMemberStatus.Fail -> "미완주"
    PartySettlementMemberStatus.Canceled -> "취소"
}

private fun PartySettlementMemberStatus.color() = when (this) {
    PartySettlementMemberStatus.Success -> Green
    PartySettlementMemberStatus.Fail -> SettlementLossRed
    PartySettlementMemberStatus.Ongoing,
    PartySettlementMemberStatus.Canceled -> DarkBrown50
}

private val pointFormatter = NumberFormat.getNumberInstance(Locale.KOREA)

private fun Int.toPointText(): String = "${pointFormatter.format(this)}P"

private fun Int.toSignedPointText(): String = when {
    this > 0 -> "+${pointFormatter.format(this)}P"
    else -> "${pointFormatter.format(this)}P"
}
