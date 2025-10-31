package com.vaxcare.unifiedhub.core.ui.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.compose.LocalStock

private const val LARGE_FAB_SIZE = 192
private const val LARGE_FAB_ICON_SIZE = 80

@Composable
fun VCFloatingActionButton(
    onClick: () -> Unit,
    iconPainter: Painter,
    modifier: Modifier = Modifier,
    containerColor: Color = LocalStock.current.colors.container,
    enabled: Boolean = true,
) {
    val buttonColor = if (enabled) {
        containerColor
    } else {
        color.container.disabled
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(LARGE_FAB_SIZE.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .background(buttonColor)
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = null,
            tint = color.onContainer.primaryInverse,
            modifier = Modifier
                .testTag(TestTags.LARGE_FAB)
                .size(LARGE_FAB_ICON_SIZE.dp)
        )
    }
}
