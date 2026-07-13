package com.example.onuldo_fe.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
private fun ColorItem(
    name: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color)
            .padding(12.dp)
    ) {
        Text(text = name)
    }
}

@Preview(showBackground = true)
@Composable
private fun ColorPreview() {
    Column {
        ColorItem("Persimmon", Persimmon)
        ColorItem("Persimmon80", Persimmon80)
        ColorItem("Persimmon50", Persimmon50)
        ColorItem("Persimmon20", Persimmon20)
        ColorItem("Persimmon10", Persimmon10)

        ColorItem("DarkBrown", DarkBrown)
        ColorItem("DarkBrown80", DarkBrown80)
        ColorItem("DarkBrown50", DarkBrown50)
        ColorItem("DarkBrown30", DarkBrown30)

        ColorItem("Green", Green)
        ColorItem("LightGreen20", Green2)
        ColorItem("LightGreen15", Green3)

        ColorItem("Red", Red)
        ColorItem("LightRed20", Red2)
        ColorItem("LightRed15", Red3)
    }
}