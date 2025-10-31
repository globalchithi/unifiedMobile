package com.vaxcare.unifiedhub.core.designsystem.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.vaxcare.unifiedhub.core.designsystem.R

private val GraphicFontFamily = FontFamily(
    Font(R.font.graphik_extra_light, FontWeight.ExtraLight),
    Font(R.font.graphik_light, FontWeight.Light),
    Font(R.font.graphik_regular, FontWeight.Normal),
    Font(R.font.graphik_regular_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.graphik_medium, FontWeight.Medium),
    Font(R.font.graphik_semi_bold, FontWeight.SemiBold),
    Font(R.font.graphik_bold, FontWeight.Bold),
    Font(R.font.graphik_black, FontWeight.ExtraBold)
)

private val TiemposFontFamily = FontFamily(
    Font(R.font.tiempos_headline_regular, FontWeight.Normal),
    Font(R.font.tiempos_headline_regular_italic, FontWeight.Normal, FontStyle.Italic)
)

object FontFamilies {
    val FamilyTitle: FontFamily = TiemposFontFamily
    val FamilyBody: FontFamily = GraphicFontFamily
}

object FontWeights {
    val WeightRegular = FontWeight.Normal
    val WeightSemiBold = FontWeight.SemiBold
    val WeightRegularItalic = (FontWeight.Normal to FontStyle.Italic)
}

object FontSizes {
    val S100: TextUnit = 12.sp
    val S200: TextUnit = 14.sp
    val S300: TextUnit = 16.sp
    val S400: TextUnit = 18.sp
    val S500: TextUnit = 20.sp
    val S600: TextUnit = 24.sp
    val S700: TextUnit = 32.sp
    val S800: TextUnit = 48.sp
    val S900: TextUnit = 56.sp
    val S1000: TextUnit = 72.sp
    val S1100: TextUnit = 104.sp
    val S1200: TextUnit = 136.sp
    val S1300: TextUnit = 360.sp
}

object LineHeights {
    val L100: TextUnit = 8.sp
    val L200: TextUnit = 16.sp
    val L250: TextUnit = 20.sp
    val L300: TextUnit = 24.sp
    val L400: TextUnit = 28.sp
    val L500: TextUnit = 32.sp
    val L600: TextUnit = 40.sp
    val L700: TextUnit = 56.sp
    val L800: TextUnit = 64.sp
    val L850: TextUnit = 68.sp
    val L900: TextUnit = 88.sp
    val L1000: TextUnit = 128.sp
    val L1100: TextUnit = 168.sp
}
