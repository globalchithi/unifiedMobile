package com.vaxcare.unifiedhub.core.ui.component.productSheet.cell

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.R
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.component.button.ElevatedIconButton

@Composable
@Deprecated("Instead, use `EditQuantityCell` overload that does not use `EditQuantityUi`.")
fun EditQuantityCell(editQuantityUi: EditQuantityUi, modifier: Modifier = Modifier,) {
    with(editQuantityUi) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ElevatedIconButton(
                    onClick = onDecrementClick,
                    onLongClick = onDecrementLongClick,
                    iconDrawRes = R.drawable.ic_minus,
                    enabled = enabled && decrementEnabled
                )
                InputNumber(
                    value = quantity,
                    enabled = enabled,
                    onClick = onInputNumberClick
                )
                ElevatedIconButton(
                    onClick = onIncrementClick,
                    onLongClick = onIncrementLongClick,
                    iconDrawRes = R.drawable.ic_plus,
                    enabled = enabled && incrementEnabled
                )
            }
        }
    }
}

@Composable
fun EditQuantityCell(
    quantity: Int,
    modifier: Modifier = Modifier,
    decrementEnabled: Boolean = true,
    incrementEnabled: Boolean = true,
    enabled: Boolean = true,
    onDecrementClick: () -> Unit,
    onDecrementLongClick: () -> Unit,
    onIncrementClick: () -> Unit,
    onIncrementLongClick: () -> Unit,
    onInputNumberClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ElevatedIconButton(
                onClick = onDecrementClick,
                onLongClick = onDecrementLongClick,
                iconDrawRes = R.drawable.ic_minus,
                enabled = enabled && decrementEnabled,
                modifier = Modifier.testTag(TestTags.ProductSheet.QuantityCell.MINUS_BTN)
            )
            InputNumber(
                value = quantity,
                enabled = enabled,
                onClick = onInputNumberClick,
                modifier = Modifier.testTag(TestTags.ProductSheet.QuantityCell.INPUT_NUMBER)
            )
            ElevatedIconButton(
                onClick = onIncrementClick,
                onLongClick = onIncrementLongClick,
                iconDrawRes = R.drawable.ic_plus,
                enabled = enabled && incrementEnabled,
                modifier = Modifier.testTag(TestTags.ProductSheet.QuantityCell.PLUS_BTN)
            )
        }
    }
}

@Composable
private fun InputNumber(
    value: Int,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = measurement.spacing.xSmall)
            .border(
                width = 1.dp,
                color = if (enabled) color.outline.fourHundred else color.outline.threeHundred,
                shape = RoundedCornerShape(measurement.radius.chip)
            ).size(width = 80.dp, height = 60.dp)
            .clip(RoundedCornerShape(measurement.radius.chip))
            .background(
                color = color.container.primaryContainer,
            ).clickable(onClick = onClick)
            .padding(horizontal = measurement.spacing.xSmall),
        contentAlignment = Alignment.CenterEnd
    ) {
        AnimatedContent(
            targetState = value,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically { -it } togetherWith slideOutVertically { it }
                } else {
                    slideInVertically { it } togetherWith slideOutVertically { -it }
                }
            }
        ) { confirm ->
            Text(
                text = (confirm).toString(),
                style = type.bodyTypeStyle.body2Bold,
                textAlign = TextAlign.End,
                color = if (enabled) color.onContainer.onContainerPrimary else color.onContainer.disabled
            )
        }
    }
}

@Deprecated("Per developer consensus, use of the 'intermediate UI data class' pattern is no longer supported.")
data class EditQuantityUi(
    val quantity: Int,
    val onDecrementClick: () -> Unit,
    val onDecrementLongClick: () -> Unit,
    val onIncrementClick: () -> Unit,
    val onIncrementLongClick: () -> Unit,
    val onInputNumberClick: () -> Unit,
    val decrementEnabled: Boolean = true,
    val incrementEnabled: Boolean = true,
    val enabled: Boolean = true
)

@Preview(showBackground = true)
@Composable
private fun PreviewEditQuantity() {
    VaxCareTheme {
        EditQuantityCell(
            EditQuantityUi(
                quantity = 5,
                onDecrementClick = {},
                onIncrementClick = {},
                onInputNumberClick = {},
                onDecrementLongClick = {},
                onIncrementLongClick = {},
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDecrementDisabledEditQuantity() {
    VaxCareTheme {
        EditQuantityCell(
            EditQuantityUi(
                quantity = 5,
                onDecrementClick = {},
                onIncrementClick = {},
                onInputNumberClick = {},
                decrementEnabled = false,
                onDecrementLongClick = {},
                onIncrementLongClick = {}
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewIncrementDisabledEditQuantity() {
    VaxCareTheme {
        EditQuantityCell(
            EditQuantityUi(
                quantity = 5,
                onDecrementClick = {},
                onIncrementClick = {},
                onInputNumberClick = {},
                incrementEnabled = false,
                onDecrementLongClick = {},
                onIncrementLongClick = {}
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDisabledEditQuantity() {
    VaxCareTheme {
        EditQuantityCell(
            EditQuantityUi(
                quantity = 5,
                onDecrementClick = {},
                onIncrementClick = {},
                onInputNumberClick = {},
                enabled = false,
                onDecrementLongClick = {},
                onIncrementLongClick = {}
            )
        )
    }
}
