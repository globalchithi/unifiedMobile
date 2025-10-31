package com.vaxcare.unifiedhub.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class SurfaceColorSchema(
    val surface: Color,
    val inverseOnSurface: Color,
    val surfaceBright: Color,
    val surfaceContainer: Color,
    val inverseContainer: Color,
    val surfaceInverseSecondary: Color,
    val surfaceInverseVfc: Color,
    val surfaceInverseState: Color,
    val surfaceInverseThreeSevenTeen: Color
)

fun lightVaxCareSurfaceColorScheme(
    surface: Color = NeutralColors.N200,
    inverseOnSurface: Color = NeutralColors.N200,
    surfaceBright: Color = NeutralColors.N100,
    surfaceContainer: Color = NeutralColors.N100,
    inverseContainer: Color = PurpleColors.P500,
    surfaceInverseSecondary: Color = CoralColors.C500,
    surfaceInverseVfc: Color = GreenColors.G600,
    surfaceInverseState: Color = MagentaColors.M500,
    surfaceInverseThreeSevenTeen: Color = BlueColors.B500
): SurfaceColorSchema =
    SurfaceColorSchema(
        surface = surface,
        inverseOnSurface = inverseOnSurface,
        surfaceBright = surfaceBright,
        surfaceContainer = surfaceContainer,
        inverseContainer = inverseContainer,
        surfaceInverseSecondary = surfaceInverseSecondary,
        surfaceInverseVfc = surfaceInverseVfc,
        surfaceInverseState = surfaceInverseState,
        surfaceInverseThreeSevenTeen = surfaceInverseThreeSevenTeen
    )

@Immutable
data class OutlineColorSchema(
    val hundred: Color,
    val twoHundred: Color,
    val threeHundred: Color,
    val fourHundred: Color,
    val fiveHundred: Color,
    val sixHundred: Color,
    val error: Color
)

fun lightVaxCareOutlineColorSchema(
    hundred: Color = NeutralColors.N100,
    twoHundred: Color = NeutralColors.N200,
    threeHundred: Color = NeutralColors.N300,
    fourHundred: Color = NeutralColors.N400,
    fiveHundred: Color = NeutralColors.N700,
    sixHundred: Color = BlueColors.B200,
    error: Color = ErrorColors.E600
): OutlineColorSchema =
    OutlineColorSchema(
        hundred = hundred,
        twoHundred = twoHundred,
        threeHundred = threeHundred,
        fourHundred = fourHundred,
        fiveHundred = fiveHundred,
        sixHundred = sixHundred,
        error = error
    )

@Immutable
data class ContainerColorSchema(
    val primaryContainer: Color,
    val secondaryContainer: Color,
    val tertiaryContainer: Color,
    val primaryPress: Color,
    val secondaryPress: Color,
    val tertiaryPress: Color,
    val neutralPress: Color,
    val disabled: Color,
    val outlinePress: Color,
    val stockVfcPress: Color,
    val stockStatePress: Color,
    val stockThreeSevenTeenPress: Color,
    val errorContainer: Color,
    val warningContainer: Color,
    val successContainer: Color,
    val neutralContainer: Color,
    val stockVfcContainer: Color,
    val stockStateContainer: Color,
    val stockThreeSevenTeenContainer: Color,
    val secondaryContainerLight: Color,
    val inversePrimary: Color,
    val secondaryContainerMedium: Color,
    val tertiaryContainerLight: Color,
    val tertiaryContainerMedium: Color,
    val stockVfcContainerLight: Color,
    val stockThreeSevenTeenContainerLight: Color,
    val stockStateContainerLight: Color
)

fun lightVaxCareContainerColorSchema(
    primaryContainer: Color = NeutralColors.N100,
    secondaryContainer: Color = PurpleColors.P500,
    tertiaryContainer: Color = CoralColors.C500,
    primaryPress: Color = NeutralColors.N400,
    secondaryPress: Color = PurpleColors.P600,
    tertiaryPress: Color = CoralColors.C600,
    neutralPress: Color = NeutralColors.N300,
    disabled: Color = NeutralColors.N400,
    outlinePress: Color = NeutralColors.N100_30,
    stockVfcPress: Color = GreenColors.G700,
    stockStatePress: Color = MagentaColors.M600,
    stockThreeSevenTeenPress: Color = BlueColors.B600,
    errorContainer: Color = ErrorColors.E100,
    warningContainer: Color = YellowColors.Y200,
    successContainer: Color = GreenColors.G200,
    neutralContainer: Color = NeutralColors.N200,
    stockVfcContainer: Color = GreenColors.G600,
    stockStateContainer: Color = MagentaColors.M500,
    stockThreeSevenTeenContainer: Color = BlueColors.B500,
    secondaryContainerLight: Color = PurpleColors.P200,
    inversePrimary: Color = PurpleColors.P100,
    secondaryContainerMedium: Color = PurpleColors.P300,
    tertiaryContainerLight: Color = CoralColors.C100,
    tertiaryContainerMedium: Color = CoralColors.C300,
    stockVfcContainerLight: Color = GreenColors.G100,
    stockThreeSevenTeenContainerLight: Color = BlueColors.B100,
    stockStateContainerLight: Color = MagentaColors.M100
): ContainerColorSchema =
    ContainerColorSchema(
        primaryContainer = primaryContainer,
        secondaryContainer = secondaryContainer,
        tertiaryContainer = tertiaryContainer,
        primaryPress = primaryPress,
        secondaryPress = secondaryPress,
        tertiaryPress = tertiaryPress,
        neutralPress = neutralPress,
        disabled = disabled,
        outlinePress = outlinePress,
        stockVfcPress = stockVfcPress,
        stockStatePress = stockStatePress,
        stockThreeSevenTeenPress = stockThreeSevenTeenPress,
        errorContainer = errorContainer,
        warningContainer = warningContainer,
        successContainer = successContainer,
        neutralContainer = neutralContainer,
        stockVfcContainer = stockVfcContainer,
        stockStateContainer = stockStateContainer,
        stockThreeSevenTeenContainer = stockThreeSevenTeenContainer,
        secondaryContainerLight = secondaryContainerLight,
        inversePrimary = inversePrimary,
        secondaryContainerMedium = secondaryContainerMedium,
        tertiaryContainerLight = tertiaryContainerLight,
        tertiaryContainerMedium = tertiaryContainerMedium,
        stockVfcContainerLight = stockVfcContainerLight,
        stockThreeSevenTeenContainerLight = stockThreeSevenTeenContainerLight,
        stockStateContainerLight = stockStateContainerLight
    )

@Immutable
data class OnContainerColorSchema(
    val onContainerPrimary: Color,
    val onContainerSecondary: Color,
    val secondaryPress: Color,
    val primaryInverse: Color,
    val disabled: Color,
    val link: Color,
    val linkPress: Color,
    val linkInverse: Color,
    val info: Color,
    val error: Color,
    val warning: Color,
    val success: Color,
    val stockPrivate: Color,
    val stockVfc: Color,
    val stockState: Color,
    val stockThreeSevenTeen: Color
)

fun lightVaxCareOnContainerColorSchema(
    onContainerPrimary: Color = NeutralColors.N700,
    onContainerSecondary: Color = NeutralColors.N100,
    secondaryPress: Color = PurpleColors.P400_10,
    primaryInverse: Color = NeutralColors.N100,
    disabled: Color = NeutralColors.N500,
    link: Color = NeutralColors.N700,
    linkPress: Color = NeutralColors.N700,
    linkInverse: Color = NeutralColors.N100,
    info: Color = NeutralColors.N600,
    error: Color = ErrorColors.E600,
    warning: Color = YellowColors.Y600,
    success: Color = GreenColors.G600,
    stockPrivate: Color = PurpleColors.P400,
    stockVfc: Color = GreenColors.G600,
    stockState: Color = MagentaColors.M500,
    stockThreeSevenTeen: Color = BlueColors.B600
): OnContainerColorSchema =
    OnContainerColorSchema(
        onContainerPrimary = onContainerPrimary,
        onContainerSecondary = onContainerSecondary,
        secondaryPress = secondaryPress,
        primaryInverse = primaryInverse,
        disabled = disabled,
        link = link,
        linkPress = linkPress,
        linkInverse = linkInverse,
        info = info,
        error = error,
        warning = warning,
        success = success,
        stockPrivate = stockPrivate,
        stockVfc = stockVfc,
        stockState = stockState,
        stockThreeSevenTeen = stockThreeSevenTeen
    )

@Immutable
data class VaxCareColorSchema(
    val surface: SurfaceColorSchema,
    val outline: OutlineColorSchema,
    val container: ContainerColorSchema,
    val onContainer: OnContainerColorSchema
)

fun lightVaxCareColorSchema(
    vaxCareSurfaceColorSchema: SurfaceColorSchema = lightVaxCareSurfaceColorScheme(),
    vaxCareOutlineColorSchema: OutlineColorSchema = lightVaxCareOutlineColorSchema(),
    vaxCareContainerColorSchema: ContainerColorSchema = lightVaxCareContainerColorSchema(),
    vaxCareOnContainerColorSchema: OnContainerColorSchema = lightVaxCareOnContainerColorSchema()
): VaxCareColorSchema =
    VaxCareColorSchema(
        surface = vaxCareSurfaceColorSchema,
        outline = vaxCareOutlineColorSchema,
        container = vaxCareContainerColorSchema,
        onContainer = vaxCareOnContainerColorSchema
    )
