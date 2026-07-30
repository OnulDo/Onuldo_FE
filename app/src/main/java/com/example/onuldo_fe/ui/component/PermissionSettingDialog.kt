package com.example.onuldo_fe.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown20
import com.example.onuldo_fe.ui.theme.DarkBrown50
import com.example.onuldo_fe.ui.theme.LocalSpacing
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.SourCream

enum class PermissionDialogType(
    val title: String,
    val description : String
) {
    CAMERA(
        title = "카메라 권한이 필요해요",
        description = "챌린지 인증을 위해 카메라 권한이\n" + "필요해요. 설정에서 허용해주세요."
    ),
    NOTIFICATION(
        title = "알림 권한이 필요해요",
        description = "푸시알림 발송을 위해 알림 권한이\n" + "필요해요. 설정에서 허용해주세요."
    )
}
@Composable
fun PermissionSettingDialog(
    type: PermissionDialogType,
    onDismiss: () -> Unit,
    onMoveToSettings: () -> Unit
) {
    val spacing = LocalSpacing.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.width(280.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Persimmon20),
            color = SourCream
        ) {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = spacing.spacing28,
                            vertical = spacing.spacing28
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = type.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = BlackBrown
                    )

                    Spacer(Modifier.height(spacing.spacing12))

                    Text(
                        text = type.description,
                        style = MaterialTheme.typography.labelLarge,
                        color = DarkBrown50,
                        textAlign = TextAlign.Center
                    )
                }

                HorizontalDivider(color = DarkBrown20)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "취소",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkBrown50
                        )
                    }

                    VerticalDivider(color = Persimmon20)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(onClick = onMoveToSettings),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "설정으로 이동",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Persimmon
                        )
                    }
                }
            }
        }
    }
}