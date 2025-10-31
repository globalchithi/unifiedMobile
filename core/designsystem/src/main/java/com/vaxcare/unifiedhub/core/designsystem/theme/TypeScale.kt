package com.vaxcare.unifiedhub.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle

data class VaxCareDisplayTypeStyle(
    val display1: TextStyle,
    val display2: TextStyle,
    val display3: TextStyle,
    val display4: TextStyle
)

fun defaultVaxCareDisplayTypeStyle(
    display1: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyTitle,
        fontWeight = FontWeights.WeightRegular,
        fontSize = FontSizes.S1100,
        lineHeight = LineHeights.L1000
    ),
    display2: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyTitle,
        fontWeight = FontWeights.WeightRegular,
        fontSize = FontSizes.S1000,
        lineHeight = LineHeights.L900
    ),
    display3: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyTitle,
        fontWeight = FontWeights.WeightRegular,
        fontSize = FontSizes.S900,
        lineHeight = LineHeights.L850
    ),
    display4: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyTitle,
        fontWeight = FontWeights.WeightRegular,
        fontSize = FontSizes.S800,
        lineHeight = LineHeights.L850
    )
): VaxCareDisplayTypeStyle =
    VaxCareDisplayTypeStyle(
        display1 = display1,
        display2 = display2,
        display3 = display3,
        display4 = display4
    )

data class VaxCareHeaderTypeStyle(
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val headlineMediumBold: TextStyle,
    val headlineSmall: TextStyle
)

fun defaultVaxCareHeaderTypeStyle(
    headlineLarge: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightRegular,
        fontSize = FontSizes.S1000,
        lineHeight = LineHeights.L900
    ),
    headlineMedium: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightRegular,
        fontSize = FontSizes.S700,
        lineHeight = LineHeights.L600
    ),
    headlineMediumBold: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightSemiBold,
        fontSize = FontSizes.S700,
        lineHeight = LineHeights.L600
    ),
    headlineSmall: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightSemiBold,
        fontSize = FontSizes.S500,
        lineHeight = LineHeights.L250
    )
): VaxCareHeaderTypeStyle =
    VaxCareHeaderTypeStyle(
        headlineLarge = headlineLarge,
        headlineMedium = headlineMedium,
        headlineMediumBold = headlineMediumBold,
        headlineSmall = headlineSmall
    )

data class VaxCareBodyTypeStyle(
    val body1: TextStyle,
    val body1Bold: TextStyle,
    val body2: TextStyle,
    val body2Bold: TextStyle,
    val body3: TextStyle,
    val body3Bold: TextStyle,
    val body4: TextStyle,
    val body4Bold: TextStyle,
    val body5: TextStyle,
    val body5Bold: TextStyle,
    val body5Italic: TextStyle,
    val body6: TextStyle,
    val body6Bold: TextStyle,
    val label: TextStyle,
)

fun defaultVaxCareBodyTypeStyle(
    body1: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightRegular,
        fontSize = FontSizes.S800,
        lineHeight = LineHeights.L700
    ),
    body1Bold: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightSemiBold,
        fontSize = FontSizes.S800,
        lineHeight = LineHeights.L700
    ),
    body2: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightRegular,
        fontSize = FontSizes.S600,
        lineHeight = LineHeights.L500
    ),
    body2Bold: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightSemiBold,
        fontSize = FontSizes.S600,
        lineHeight = LineHeights.L500
    ),
    body3: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightRegular,
        fontSize = FontSizes.S500,
        lineHeight = LineHeights.L400
    ),
    body3Bold: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightSemiBold,
        fontSize = FontSizes.S500,
        lineHeight = LineHeights.L400
    ),
    body4: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightRegular,
        fontSize = FontSizes.S400,
        lineHeight = LineHeights.L300
    ),
    body4Bold: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightSemiBold,
        fontSize = FontSizes.S400,
        lineHeight = LineHeights.L300
    ),
    body5: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightRegular,
        fontSize = FontSizes.S300,
        lineHeight = LineHeights.L250
    ),
    body5Bold: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightSemiBold,
        fontSize = FontSizes.S300,
        lineHeight = LineHeights.L250
    ),
    body5Italic: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightRegularItalic.first,
        fontStyle = FontWeights.WeightRegularItalic.second,
        fontSize = FontSizes.S300,
        lineHeight = LineHeights.L300
    ),
    body6: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightRegular,
        fontSize = FontSizes.S200,
        lineHeight = LineHeights.L250
    ),
    body6Bold: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightSemiBold,
        fontSize = FontSizes.S200,
        lineHeight = LineHeights.L250
    ),
    label: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightSemiBold,
        fontSize = FontSizes.S100,
        lineHeight = LineHeights.L200
    )
): VaxCareBodyTypeStyle =
    VaxCareBodyTypeStyle(
        body1 = body1,
        body1Bold = body1Bold,
        body2 = body2,
        body2Bold = body2Bold,
        body3 = body3,
        body3Bold = body3Bold,
        body4 = body4,
        body4Bold = body4Bold,
        body5 = body5,
        body5Bold = body5Bold,
        body5Italic = body5Italic,
        body6 = body6,
        body6Bold = body6Bold,
        label = label
    )

data class VaxCareTitleTypeStyle(
    val titleMedium: TextStyle
)

fun defaultVaxCareTitleTypeStyle(
    titleMedium: TextStyle = TextStyle(
        fontFamily = FontFamilies.FamilyBody,
        fontWeight = FontWeights.WeightSemiBold,
        fontSize = FontSizes.S500,
        lineHeight = LineHeights.L250
    )
): VaxCareTitleTypeStyle =
    VaxCareTitleTypeStyle(
        titleMedium = titleMedium
    )

@Immutable
data class VaxCareTypeScale(
    val displayTypeStyle: VaxCareDisplayTypeStyle,
    val headerTypeStyle: VaxCareHeaderTypeStyle,
    val bodyTypeStyle: VaxCareBodyTypeStyle,
    val titleTypeStyle: VaxCareTitleTypeStyle
)

fun defaultVaxCareTypeScale(
    displayTypeStyle: VaxCareDisplayTypeStyle = defaultVaxCareDisplayTypeStyle(),
    headerTypeStyle: VaxCareHeaderTypeStyle = defaultVaxCareHeaderTypeStyle(),
    bodyTypeStyle: VaxCareBodyTypeStyle = defaultVaxCareBodyTypeStyle(),
    titleTypeStyle: VaxCareTitleTypeStyle = defaultVaxCareTitleTypeStyle()
): VaxCareTypeScale =
    VaxCareTypeScale(
        displayTypeStyle = displayTypeStyle,
        headerTypeStyle = headerTypeStyle,
        bodyTypeStyle = bodyTypeStyle,
        titleTypeStyle = titleTypeStyle
    )
