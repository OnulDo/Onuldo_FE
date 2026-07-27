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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.onuldo_fe.data.party.dummy.PartySettlementDummyData
import com.example.onuldo_fe.model.party.PartySettlementMember
import com.example.onuldo_fe.model.party.PartySettlementMemberStatus
import com.example.onuldo_fe.model.party.PartySettlementResult
import com.example.onuldo_fe.model.party.PartySettlementStatus
import com.example.onuldo_fe.repository.party.toModel
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.component.party.PartyMemberCard
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.Green
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
    val content = result.status.content()

    Column(modifier.fillMaxSize().background(SourCream)) {
        Box(Modifier.fillMaxWidth().height(56.dp)) {
            OnulDoBackButton(
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp),
                onClick = onBack
            )
            Text(
                text = "파티 정산 결과",
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 44.dp),
                color = BlackBrown,
                fontFamily = Pretendard,
                fontSize = 17.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp)
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
                        PartySettlementCharacter()
                    }
                }
                Spacer(Modifier.height(7.dp))
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
                Spacer(Modifier.height(47.dp))
                SettlementSectionTitle("내 정산 결과")
                Spacer(Modifier.height(6.dp))
                SettlementSummaryCard(
                    refundAmount = result.refundAmount.toPointText(),
                    adjustmentLabel = content.adjustmentLabel,
                    adjustmentAmount = result.adjustmentAmount.toSignedPointText(),
                    adjustmentColor = content.adjustmentColor,
                    refundLabelColor = content.refundLabelColor,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(12.dp))
                SettlementSectionTitle("파티원 결과")
                Spacer(Modifier.height(6.dp))
            }

            items(result.members, key = { it.memberId }) { member ->
                PartySettlementMemberCard(
                    member = member,
                    showCompletionStatus = result.status == PartySettlementStatus.PartialSuccess,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().height(138.dp).background(SourCream),
            contentAlignment = Alignment.TopCenter
        ) {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 40.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Persimmon, contentColor = SourCream)
            ) {
                Text(
                    text = "확인",
                    fontFamily = Pretendard,
                    fontSize = 17.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SettlementSectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 24.dp),
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
    refundAmount: String,
    adjustmentLabel: String,
    adjustmentAmount: String,
    adjustmentColor: Color,
    refundLabelColor: Color,
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
        SettlementAmount("도전금 환급", refundAmount, refundLabelColor, BlackBrown, Modifier.weight(1f))
        SettlementAmount(adjustmentLabel, adjustmentAmount, adjustmentColor, adjustmentColor, Modifier.weight(1f))
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
    val resultColor = if (member.adjustmentAmount < 0) SettlementLossRed else Green

    PartyMemberCard(
        name = member.name,
        profileImageUrl = member.profileImageUrl,
        defaultCharacterId = member.defaultCharacterId,
        modifier = modifier,
        startPadding = 16.dp,
        endPadding = 20.dp,
        nameWidth = 44.dp,
        middleContent = {
            if (showCompletionStatus) {
                Text(
                    text = if (member.status == PartySettlementMemberStatus.Completed) "완주" else "미완주",
                    color = if (member.status == PartySettlementMemberStatus.Completed) Green else SettlementLossRed,
                    fontFamily = Pretendard,
                    fontSize = 11.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) {
        Text(
            text = member.adjustmentAmount.toSignedPointText(),
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
    val adjustmentLabel: String,
    val adjustmentColor: Color,
    val refundLabelColor: Color
)

private fun PartySettlementStatus.content() = when (this) {
    PartySettlementStatus.AllSuccess -> SettlementContent(
        characterBackground = Persimmon.copy(alpha = 0.15f),
        adjustmentLabel = "성과 보너스",
        adjustmentColor = Green,
        refundLabelColor = DarkBrown50
    )

    PartySettlementStatus.PartialSuccess -> SettlementContent(
        characterBackground = Persimmon.copy(alpha = 0.15f),
        adjustmentLabel = "성과 보너스",
        adjustmentColor = Green,
        refundLabelColor = DarkBrown
    )

    PartySettlementStatus.AllFailed -> SettlementContent(
        characterBackground = DarkBrown.copy(alpha = 0.08f),
        adjustmentLabel = "차감",
        adjustmentColor = SettlementLossRed,
        refundLabelColor = DarkBrown
    )
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
