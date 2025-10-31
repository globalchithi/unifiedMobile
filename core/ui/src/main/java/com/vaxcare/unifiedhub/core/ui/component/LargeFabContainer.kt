package com.vaxcare.unifiedhub.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement

private const val LARGE_FAB_X_OFFSET = 28

@Composable
fun LargeFabContainer(
    modifier: Modifier = Modifier,
    fab: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()

        Box(
            Modifier
                .graphicsLayer {
                    translationX = LARGE_FAB_X_OFFSET.dp.toPx()
                }.padding(bottom = measurement.spacing.large)
                .align(Alignment.BottomEnd)
        ) {
            fab()
        }
    }
}
