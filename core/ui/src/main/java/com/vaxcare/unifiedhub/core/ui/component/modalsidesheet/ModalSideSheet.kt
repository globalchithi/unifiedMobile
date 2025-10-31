package com.vaxcare.unifiedhub.core.ui.component.modalsidesheet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class SheetEdge { Start, End }

@Composable
fun ModalSideSheet(
    modifier: Modifier = Modifier,
    sheetState: ModalSideSheetState = rememberModalSideSheetState(),
    edge: SheetEdge = SheetEdge.Start,
    sheetWidth: Dp = 360.dp,
    shape: Shape = RoundedCornerShape(VaxCareTheme.measurement.radius.cardMedium),
    scrimColor: Color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f),
    onDismissRequest: () -> Unit = {},
    sheetContent: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val hiddenPx = with(density) { (sheetWidth + 24.dp).roundToPx().toFloat() }
        .let { if (edge == SheetEdge.Start) -it else it }

    LaunchedEffect(hiddenPx) { sheetState.updateAnchors(hiddenPx) }

    BackHandler(sheetState.isVisible) {
        scope.launch { sheetState.hide() }
    }

    Box(modifier.fillMaxSize()) {
        content()

        // Scrim
        if (sheetState.isVisible) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(scrimColor)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            onDismissRequest()
                            scope.launch { sheetState.hide() }
                        }
                    }
            )
        }

        val offsetX by sheetState.offsetX()

        Box(
            modifier = Modifier
                .padding(24.dp)
                .align(if (edge == SheetEdge.Start) Alignment.CenterStart else Alignment.CenterEnd)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(edge) {
                    var velocity = 0f
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch { sheetState.settle(velocity) }
                        }
                    ) { _, dragAmount ->
                        velocity = dragAmount
                        scope.launch { sheetState.snapBy(dragAmount) }
                    }
                },
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(sheetWidth),
                tonalElevation = 3.dp,
                shape = if (edge == SheetEdge.Start) shape else shape.mirror(),
                color = VaxCareTheme.color.surface.surfaceBright
            ) {
                Column(content = sheetContent)
            }
        }
    }
}

private fun Shape.mirror() =
    when (this) {
        is RoundedCornerShape -> RoundedCornerShape(
            topStart = topEnd,
            topEnd = topStart,
            bottomStart = bottomEnd,
            bottomEnd = bottomStart
        )

        else -> this
    }

@FullDevicePreview
@Composable
private fun StartModalSideSheetPreview() {
    val state = rememberModalSideSheetState(initialValue = ModalSideSheetValue.Expanded)
    VaxCareTheme {
        ModalSideSheet(
            edge = SheetEdge.Start,
            sheetState = state,
            sheetContent = { Text("Sheet", Modifier.padding(24.dp)) }
        ) { Text("Main content", Modifier.padding(24.dp)) }
    }
}

@FullDevicePreview
@Composable
private fun EndModalSideSheetPreview() {
    val state = rememberModalSideSheetState(initialValue = ModalSideSheetValue.Expanded)
    VaxCareTheme {
        ModalSideSheet(
            edge = SheetEdge.End,
            sheetState = state,
            sheetContent = { Text("Sheet", Modifier.padding(24.dp)) }
        ) { Text("Main content", Modifier.padding(24.dp)) }
    }
}
