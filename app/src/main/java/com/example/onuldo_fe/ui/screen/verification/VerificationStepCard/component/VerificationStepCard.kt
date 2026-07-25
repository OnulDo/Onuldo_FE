package com.example.onuldo_fe.ui.screen.verification.VerificationStepCard.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.White

@Composable
fun VerificationStepCard(
    icon: Int,
    title: String,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(top = 8.dp),
        shape = RoundedCornerShape(14.dp),
        color = White,
        border = BorderStroke(
            width = 1.dp,
            color = DarkBrown40,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
            )

            Spacer(modifier = Modifier.width(9.dp))

            Text(
                text = title,
                color = BlackBrown,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}
