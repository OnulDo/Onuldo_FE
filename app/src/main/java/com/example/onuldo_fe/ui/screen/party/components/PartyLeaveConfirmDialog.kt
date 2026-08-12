package com.example.onuldo_fe.ui.screen.party.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Pretendard
import com.example.onuldo_fe.ui.theme.SourCream

/**
 * 파티 대기방에서 뒤로가기를 선택했을 때 파티 탈퇴 여부를 확인하는 모달
 * 실제 탈퇴 요청은 사용자가 [나가기]를 선택한 경우에만 호출
 */
@Composable
fun PartyLeaveConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val spacing = LocalSpacing.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .width(280.dp)
                .height(180.dp),
            shape = RoundedCornerShape(14.dp),
            color = SourCream
        ) {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(132.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(spacing.spacing28))
                    Text(
                        text = "파티를 나가시겠습니까?",
                        modifier = Modifier.fillMaxWidth(),
                        color = BlackBrown,
                        fontFamily = Pretendard,
                        fontSize = 17.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(spacing.spacing12))
                    Text(
                        text = "정말 파티를 나가시겠습니까?",
                        modifier = Modifier.fillMaxWidth(),
                        color = DarkBrown70,
                        fontFamily = Pretendard,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }

                HorizontalDivider(thickness = 1.dp, color = DarkBrown20)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(47.dp)
                ) {
                    PartyLeaveDialogButton(
                        text = "취소",
                        color = DarkBrown50,
                        fontSize = 15,
                        lineHeight = 18,
                        fontWeight = FontWeight.Medium,
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    VerticalDivider(thickness = 1.dp, color = DarkBrown20)
                    PartyLeaveDialogButton(
                        text = "나가기",
                        color = Persimmon,
                        fontSize = 14,
                        lineHeight = 22,
                        fontWeight = FontWeight.Bold,
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PartyLeaveDialogButton(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    fontSize: Int,
    lineHeight: Int,
    fontWeight: FontWeight,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontFamily = Pretendard,
            fontSize = fontSize.sp,
            lineHeight = lineHeight.sp,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartyLeaveConfirmDialogPreview() {
    OnulDo_FETheme {
        PartyLeaveConfirmDialog(onDismiss = {}, onConfirm = {})
    }
}
