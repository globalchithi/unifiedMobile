package com.vaxcare.unifiedhub.core.ui.compose.preview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme

@Composable
fun PreviewContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit,) {
    VaxCareTheme {
        Surface(
            modifier = modifier,
            color = VaxCareTheme.color.surface.surface
        ) {
            content()
        }
    }
}

/**
 * For use only in Compose previews. Wraps [content] block in the app theme and a [Surface] that
 * fills the screen.
 *
 */
@Composable
fun FullscreenPreview(modifier: Modifier = Modifier, content: @Composable () -> Unit,) {
    PreviewContainer(modifier.fillMaxSize(), content)
}
