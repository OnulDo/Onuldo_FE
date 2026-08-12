package com.example.onuldo_fe.ui.theme
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
        resId = R.font.pretendard_medium,
        weight = FontWeight.Medium
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

object OnulDoTypography {

    // Display
    val displayBold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    )

    // Title
    val titleBold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    )

    // Subtitle
    val subtitle1Bold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 30.sp,
    )

    // Header
    val header1ExtraBold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 40.sp,
    )

    val header1Bold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 40.sp,
    )

    // Body
    val body1Bold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 40.sp,
    )

    val body2Bold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
    )

    val body3Bold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 20.sp,
    )

    val body3ExtraBold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp,
        lineHeight = 20.sp,
    )

    val body4Medium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    )

    val body5Bold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 22.sp,
    )

    val body5Medium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 22.sp,
    )

    val body5Regular = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
    )

    val body6Bold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 22.sp,
    )

    // Caption 1
    val caption1Medium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 20.sp,
    )

    val caption1Regular = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp,
    )

    // Caption 2
    val caption2Medium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 20.sp,
    )

    val caption2Regular = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 20.sp,
    )

    // Caption 3
    val caption3Medium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 18.sp,
    )

    val caption3Bold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 18.sp,
    )

    val caption3Regular = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 18.sp,
    )

    // Caption 4
    val caption4Medium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 16.sp,
    )

    val caption4Regular = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 20.sp,
    )

    val caption4Bold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        lineHeight = 16.sp,
    )
}
