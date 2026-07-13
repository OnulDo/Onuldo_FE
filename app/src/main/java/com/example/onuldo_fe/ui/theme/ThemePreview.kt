package com.example.onuldo_fe.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
private fun ThemePreview() {
    OnulDo_FETheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Header1",
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "Subtitle1",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Body1",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Body2 ExtraBold",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Body2",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Body3",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Caption1",
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = "Caption2",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Primary - Persimmon",
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Secondary - DarkBrown",
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}