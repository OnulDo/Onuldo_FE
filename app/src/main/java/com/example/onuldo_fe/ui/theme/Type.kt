package com.example.onuldo_fe.ui.theme

import android.R.attr.fontFamily
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R

val Pretendard = FontFamily(
    Font(
        resId = R.font.pretendard_regular,
        weight = FontWeight.Normal
    ),
    Font(
        resId = R.font.pretendard_bold,
        weight = FontWeight.Bold
    ),
    Font(
        resId = R.font.pretendard_extrabold,
        weight = FontWeight.ExtraBold
    )
)
val Typography = Typography(
    //Header1 - 28sp ExtraBold
    headlineLarge = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp
    ),

    //Subtitle1 - 26sp Bold
    headlineMedium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp
    ),

    // Body1 - 22sp Bold
    titleLarge = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),

    // Body2-ExtraBold - 20sp ExtraBold
    titleMedium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp
    ),

    //11sp Bold
    titleSmall = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 0.44.sp
    ),

    // Body2 - 17sp Bold
    bodyLarge = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp
    ),

    // Body3 - 14sp Bold
    bodyMedium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 22.sp
    ),

    // Body4 - 12sp Regular
    bodySmall = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    ),

    // Caption1 - 13sp Regular
    labelLarge = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp
    ),

    // Caption2 - 12sp Regular
    labelMedium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),

    //11sp Regular
    labelSmall = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp
    )

)