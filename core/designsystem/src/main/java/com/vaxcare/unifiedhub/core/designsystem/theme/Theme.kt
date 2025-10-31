package com.vaxcare.unifiedhub.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalVaxCareColorSchema = staticCompositionLocalOf { lightVaxCareColorSchema() }
val LocalVaxCareTypeScale = staticCompositionLocalOf { defaultVaxCareTypeScale() }
val LocalVaxCareMeasurement = staticCompositionLocalOf { defaultVaxCareMeasurement() }

@Composable
fun VaxCareTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalVaxCareColorSchema provides lightVaxCareColorSchema(),
        LocalVaxCareTypeScale provides defaultVaxCareTypeScale(),
        LocalVaxCareMeasurement provides defaultVaxCareMeasurement()
    ) {
        MaterialTheme(
            content = content
        )
    }
}

object VaxCareTheme {
    val color: VaxCareColorSchema
        @Composable
        get() = LocalVaxCareColorSchema.current
    val type: VaxCareTypeScale
        @Composable
        get() = LocalVaxCareTypeScale.current
    val measurement: VaxCareMeasurement
        @Composable
        get() = LocalVaxCareMeasurement.current
}
