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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.data.party.dummy.PartySettlementDummyData
import com.example.onuldo_fe.model.party.PartySettlementMember
import com.example.onuldo_fe.model.party.PartySettlementMemberStatus
import com.example.onuldo_fe.model.party.PartySettlementResult
import com.example.onuldo_fe.model.party.PartySettlementStatus
import com.example.onuldo_fe.repository.party.toModel
import com.example.onuldo_fe.ui.screen.party.components.PartyMemberCard
import com.example.onuldo_fe.ui.screen.party.components.PartyTopBar
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.Green
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
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
    val content = result.status.content()
    // 서버 금액의 부호가 정산의 성격을 결정하며 resultType과는 독립적이다.
    val displayContent = result.displayAmount.displayContent()

    Column(modifier.fillMaxSize().background(SourCream)) {
        PartyTopBar(title = "파티 정산 결과", onBack = onBack)

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(bottom = spacing.spacing16)
        ) {
            item {
                // TODO 디자인 시스템에 47dp 토큰이 추가되면 LocalSpacing으로 교체
                Spacer(Modifier.height(47.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(135.dp)
                            .background(content.characterBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        PartySettlementCharacter()
                    }
                }
                Spacer(Modifier.height(spacing.spacing12))
                Text(
                    text = result.title,
                    modifier = Modifier.fillMaxWidth(),
                    color = BlackBrown,
                    fontFamily = Pretendard,
                    fontSize = 22.sp,
                    lineHeight = 40.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = result.description,
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkBrown,
                    fontFamily = Pretendard,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(spacing.spacing50))
                SettlementSectionTitle("내 정산 결과")
                Spacer(Modifier.height(spacing.spacing8))
                SettlementSummaryCard(
                    depositAmount = result.depositAmount.toPointText(),
                    displayLabel = displayContent.label,
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
                    showCompletionStatus = result.status == PartySettlementStatus.PartialSuccess,
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
                    // TODO 디자인 시스템에 40dp 토큰이 추가되면 LocalSpacing으로 교체
                    .padding(top = 40.dp),
                height = 52.dp,
                fontSize = 17.sp,
                lineHeight = 20.sp
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
        fontFamily = Pretendard,
        fontSize = 12.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun PartySettlementCharacter() {
    Image(
        painter = painterResource(R.drawable.party_settlement_success_icon),
        contentDescription = null,
        modifier = Modifier.size(width = 73.dp, height = 104.dp),
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
            fontFamily = Pretendard,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = amount,
            // TODO 디자인 시스템에 3dp 토큰이 추가되면 LocalSpacing으로 교체
            modifier = Modifier.padding(top = 3.dp),
            color = amountColor,
            fontFamily = Pretendard,
            fontSize = 17.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold
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
                    fontFamily = Pretendard,
                    fontSize = 11.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) {
        Text(
            text = member.displayAmount.toSignedPointText(),
            modifier = Modifier.width(130.dp),
            color = resultColor,
            fontFamily = Pretendard,
            fontSize = 12.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}

private data class SettlementContent(
    val characterBackground: Color,
    val depositLabelColor: Color
)

private fun PartySettlementStatus.content() = when (this) {
    PartySettlementStatus.AllSuccess -> SettlementContent(
        characterBackground = Persimmon.copy(alpha = 0.15f),
        depositLabelColor = DarkBrown50
    )

    PartySettlementStatus.PartialSuccess -> SettlementContent(
        characterBackground = Persimmon.copy(alpha = 0.15f),
        depositLabelColor = DarkBrown
    )

    PartySettlementStatus.AllFailed -> SettlementContent(
        characterBackground = DarkBrown.copy(alpha = 0.08f),
        depositLabelColor = DarkBrown
    )
}

private data class SettlementDisplayContent(val label: String, val color: Color)

/** myDisplayAmount·displayAmount는 결과 유형이 아니라 서버가 준 부호로 표시한다. */
private fun Int.displayContent() = when {
    this > 0 -> SettlementDisplayContent("성과 보너스", Green)
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

@Preview(name = "정산 - 전원 성공", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartySettlementAllSuccessPreview() {
    OnulDo_FETheme {
        PartySettlementScreen(onBack = {}, result = PartySettlementDummyData.allSuccess.toModel())
    }
}

@Preview(name = "정산 - 일부 성공", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartySettlementPartialSuccessPreview() {
    OnulDo_FETheme {
        PartySettlementScreen(onBack = {}, result = PartySettlementDummyData.partialSuccess.toModel())
    }
}

@Preview(name = "정산 - 전원 실패", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartySettlementAllFailedPreview() {
    OnulDo_FETheme {
        PartySettlementScreen(onBack = {}, result = PartySettlementDummyData.allFailed.toModel())
    }
}
