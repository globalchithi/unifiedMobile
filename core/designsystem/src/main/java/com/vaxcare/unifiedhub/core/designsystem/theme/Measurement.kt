package com.vaxcare.unifiedhub.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

data class Radius(
    val button: Dp,
    val fabSmall: Dp,
    val fabMedium: Dp,
    val fabLarge: Dp,
    val cardSmall: Dp,
    val cardMedium: Dp,
    val cardLarge: Dp,
    val chip: Dp,
    val input: Dp,
    val sheetHard: Dp,
    val sheetMedium: Dp,
    val sheetSoft: Dp,
    val snackBar: Dp,
    val full: Dp
)

fun defaultRadius(
    button: Dp = RadiusUnit.R400,
    fabSmall: Dp = RadiusUnit.R400,
    fabMedium: Dp = RadiusUnit.R400,
    fabLarge: Dp = RadiusUnit.R500,
    cardSmall: Dp = RadiusUnit.R100,
    cardMedium: Dp = RadiusUnit.R200,
    cardLarge: Dp = RadiusUnit.R300,
    chip: Dp = RadiusUnit.R100,
    input: Dp = RadiusUnit.R400,
    sheetHard: Dp = RadiusUnit.R100,
    sheetMedium: Dp = RadiusUnit.R300,
    sheetSoft: Dp = RadiusUnit.R400,
    snackBar: Dp = RadiusUnit.R100,
    full: Dp = RadiusUnit.R500
): Radius =
    Radius(
        button = button,
        fabSmall = fabSmall,
        fabMedium = fabMedium,
        fabLarge = fabLarge,
        cardSmall = cardSmall,
        cardMedium = cardMedium,
        cardLarge = cardLarge,
        chip = chip,
        input = input,
        sheetHard = sheetHard,
        sheetMedium = sheetMedium,
        sheetSoft = sheetSoft,
        snackBar = snackBar,
        full = full
    )

data class Spacing(
    val xSmall: Dp,
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val xLarge: Dp,
    val sideMarginMobile: Dp,
    val buttonLg: Dp,
    val buttonSmallY: Dp,
    val topBar: Dp,
    val topBarSmallY: Dp,
    val topBarMediumY: Dp,
    val topBarLargeY: Dp,
    val topBarXLargeY: Dp
)

fun defaultSpacing(
    xSmall: Dp = Spacings.S300,
    small: Dp = Spacings.S500,
    medium: Dp = Spacings.S600,
    large: Dp = Spacings.S800,
    xLarge: Dp = Spacings.S1200,
    sideMarginMobile: Dp = Spacings.S800,
    buttonLg: Dp = Spacings.S600,
    buttonSmallY: Dp = Spacings.S400,
    topBar: Dp = Spacings.S200,
    topBarSmallY: Dp = Spacings.S600,
    topBarMediumY: Dp = Spacings.S800,
    topBarLargeY: Dp = Spacings.S1200,
    topBarXLargeY: Dp = Spacings.S1500
): Spacing =
    Spacing(
        xSmall = xSmall,
        small = small,
        medium = medium,
        large = large,
        xLarge = xLarge,
        sideMarginMobile = sideMarginMobile,
        buttonLg = buttonLg,
        buttonSmallY = buttonSmallY,
        topBar = topBar,
        topBarSmallY = topBarSmallY,
        topBarMediumY = topBarMediumY,
        topBarLargeY = topBarLargeY,
        topBarXLargeY = topBarXLargeY
    )

data class Size(
    val button: Dp,
    val input: Dp,
    val mobileFull: Dp,
    val dialogMobile: Dp,
    val dialogHubLogin: Dp,
    val dialogHub: Dp,
    val listItemMobile: Dp,
    val buttonsWizard: Dp,
    val cardLarge: Dp,
    val cardFull: Dp
)

fun defaultSize(
    button: Dp = Spacings.S1700,
    input: Dp = Spacings.S1900,
    mobileFull: Dp = Spacings.S2000,
    dialogMobile: Dp = Widths.W100,
    dialogHubLogin: Dp = Widths.W400,
    dialogHub: Dp = Widths.W500,
    listItemMobile: Dp = Widths.W200,
    buttonsWizard: Dp = Widths.W300,
    cardLarge: Dp = Widths.W600,
    cardFull: Dp = Widths.W800
): Size =
    Size(
        button = button,
        input = input,
        mobileFull = mobileFull,
        dialogMobile = dialogMobile,
        dialogHubLogin = dialogHubLogin,
        dialogHub = dialogHub,
        listItemMobile = listItemMobile,
        buttonsWizard = buttonsWizard,
        cardLarge = cardLarge,
        cardFull = cardFull
    )

data class Elevation(
    val positionX: Dp,
    val positionY: Dp,
    val blur: Dp,
    val spread: Dp
)

data class ElevationRange(
    override val one: Elevation,
    override val two: Elevation,
    override val three: Elevation,
    override val four: Elevation
) : ElevationOne,
    ElevationTwo,
    ElevationThree,
    ElevationFour

fun defaultElevationRange(
    one: Elevation = Elevation(
        positionX = DropShadowPositions.SM,
        positionY = DropShadowPositions.MD,
        blur = DropShadowBlurs.SM,
        spread = DropShadowSpreads.SM
    ),
    two: Elevation = Elevation(
        positionX = DropShadowPositions.SM,
        positionY = DropShadowPositions.MD,
        blur = DropShadowBlurs.SM,
        spread = DropShadowSpreads.MD
    ),
    three: Elevation = Elevation(
        positionX = DropShadowPositions.MD,
        positionY = DropShadowPositions.LG,
        blur = DropShadowBlurs.MD,
        spread = DropShadowSpreads.MD
    ),
    four: Elevation = Elevation(
        positionX = DropShadowPositions.MD,
        positionY = DropShadowPositions.XLG,
        blur = DropShadowBlurs.LG,
        spread = DropShadowSpreads.LG
    )
): ElevationRange =
    ElevationRange(
        one = one,
        two = two,
        three = three,
        four = four
    )

interface ElevationOne {
    val one: Elevation
}

interface ElevationTwo {
    val two: Elevation
}

interface ElevationThree {
    val three: Elevation
}

interface ElevationFour {
    val four: Elevation
}

@Immutable
data class VaxCareMeasurement(
    val elevationRange: ElevationRange,
    val radius: Radius,
    val size: Size,
    val spacing: Spacing
)

fun defaultVaxCareMeasurement(
    elevationRange: ElevationRange = defaultElevationRange(),
    radius: Radius = defaultRadius(),
    size: Size = defaultSize(),
    spacing: Spacing = defaultSpacing()
): VaxCareMeasurement =
    VaxCareMeasurement(
        elevationRange = elevationRange,
        radius = radius,
        size = size,
        spacing = spacing
    )
