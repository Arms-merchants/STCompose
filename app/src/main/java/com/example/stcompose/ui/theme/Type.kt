package com.example.stcompose.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.stcompose.R


val nunitoSansFamily = FontFamily(
    Font(R.font.nunitosans_light, FontWeight.Light),
    Font(R.font.nunitosans_semibold, FontWeight.SemiBold),
    Font(R.font.nunitosans_bold, FontWeight.Bold)
)

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        //fontFamily = FontFamily.Default,
        fontFamily = nunitoSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)


val h1 = TextStyle(
    fontSize = 18.sp, fontFamily = nunitoSansFamily, fontWeight = FontWeight.Bold
)

val h2 = TextStyle(
    fontSize = 14.sp,
    fontFamily = nunitoSansFamily,
    letterSpacing = 0.15.sp,
    fontWeight = FontWeight.Bold
)

val subtitle1 = TextStyle(
    fontSize = 16.sp, fontFamily = nunitoSansFamily, fontWeight = FontWeight.Light
)

val body1 = TextStyle(
    fontSize = 14.sp, fontFamily = nunitoSansFamily, fontWeight = FontWeight.Light
)

val body2 = TextStyle(
    fontSize = 12.sp, fontFamily = nunitoSansFamily, fontWeight = FontWeight.Light
)

val button = TextStyle(
    fontSize = 14.sp,
    fontFamily = nunitoSansFamily,
    letterSpacing = 1.sp,
    fontWeight = FontWeight.SemiBold
)

val caption = TextStyle(
    fontSize = 12.sp,
    fontFamily = nunitoSansFamily,
    fontWeight = FontWeight.SemiBold
)

