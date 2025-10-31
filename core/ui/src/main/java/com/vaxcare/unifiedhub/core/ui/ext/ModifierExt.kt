package com.vaxcare.unifiedhub.core.ui.ext

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp

/**
 * Draw a fading edge on the bottom of a [LazyColumn]. The edge disappears if scrolling is not
 * possible, because either the content is too small, or the container is already scrolled as far
 * as is possible.
 *
 * @param lazyListState The state of the [LazyColumn]
 * @param height The visible height of the fading edge
 */
fun Modifier.verticalFadingEdge(lazyListState: LazyListState, height: Dp) =
    this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()

            if (lazyListState.canScrollForward) {
                val endY = size.height
                val brush = Brush.verticalGradient(
                    colors = listOf(Color.Black, Color.Transparent),
                    startY = endY - height.toPx(),
                    endY = endY
                )
                drawRect(brush = brush, blendMode = BlendMode.DstIn)
            }
        }
