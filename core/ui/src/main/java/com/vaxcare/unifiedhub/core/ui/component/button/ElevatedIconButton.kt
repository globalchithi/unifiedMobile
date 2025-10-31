package com.vaxcare.unifiedhub.core.ui.component.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.R
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color

/**
 * Contains the default values used by `ElevatedIconButton`.
 * This allows for centralizing the theming and ensuring consistency
 * across the application.
 */
object ElevatedIconButtonDefaults {
    /**
     * The default size (diameter) for the button.
     */
    val Size = 48.dp

    /**
     * The default shadow elevation for the button.
     */
    val shadowElevation: Dp
        @Composable
        get() = VaxCareTheme.measurement.elevationRange.one.blur

    /**
     * Creates the [ButtonColors] that represent the default colors for this button,
     * using the application's theme values.
     */
    @Composable
    fun elevatedButtonColors(): ButtonColors =
        ButtonDefaults.elevatedButtonColors(
            containerColor = color.container.primaryContainer,
            contentColor = color.onContainer.onContainerPrimary,
            disabledContainerColor = color.container.primaryPress,
            disabledContentColor = color.onContainer.disabled
        )
}

/**
 * A convenience overload of [ElevatedIconButton] that accepts a drawable resource
 * for quick and common usage.
 *
 * This component builds upon the base implementation, which was created to add
 * `onLongClick` support, a feature missing from the standard Material 3 `ElevatedButton`.
 *
 * @param onClick The lambda to be executed when the button is clicked.
 * @param iconDrawRes The drawable resource ID for the icon.
 * @param modifier The [Modifier] to be applied to this button.
 * @param enabled Controls the enabled state of the button. When `false`, this button will not
 * be interactive and will be displayed with disabled colors.
 * @param size The diameter of the circular button.
 * @param onLongClick The optional lambda to be executed on a long click.
 * @param contentDescription Text used by accessibility services to describe the icon.
 * @param colors [ButtonColors] that resolves the colors for the different states of the button.
 */
@Composable
fun ElevatedIconButton(
    onClick: () -> Unit,
    @DrawableRes iconDrawRes: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = ElevatedIconButtonDefaults.Size,
    onLongClick: (() -> Unit)? = null,
    contentDescription: String? = null,
    colors: ButtonColors = ElevatedIconButtonDefaults.elevatedButtonColors()
) {
    ElevatedIconButton(
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        modifier = modifier,
        size = size,
        colors = colors,
        content = {
            Icon(
                painter = painterResource(iconDrawRes),
                contentDescription = contentDescription
            )
        }
    )
}

/**
 * A custom circular, elevated button that supports both `onClick` and `onLongClick` gestures.
 *
 * **This component was created because the standard Material 3 `ElevatedButton` does not
 * natively support long-press events.** It allows for nesting custom `@Composable` content
 * and provides optional haptic feedback on long press, filling a gap in the standard library.
 *
 * @param onClick The lambda to be executed when the button is clicked.
 * @param modifier The [Modifier] to be applied to this button.
 * @param size The diameter of the circular button.
 * @param onLongClick The optional lambda to be executed on a long click.
 * @param enabled Controls the enabled state of the button. When `false`, this button will not
 * be interactive and will be displayed with disabled colors.
 * @param colors [ButtonColors] that resolves the colors for the different states of the button.
 * @param content The `@Composable` content to be displayed inside the button.
 */
@Composable
fun ElevatedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = ElevatedIconButtonDefaults.Size,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: ButtonColors = ElevatedIconButtonDefaults.elevatedButtonColors(),
    content: @Composable () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = if (enabled) colors.containerColor else colors.disabledContainerColor,
        contentColor = if (enabled) colors.contentColor else colors.disabledContentColor,
        shadowElevation = ElevatedIconButtonDefaults.shadowElevation,
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .combinedClickable(
                    enabled = enabled,
                    onClick = onClick,
                    onLongClick = onLongClick?.let { longClickLambda ->
                        {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            longClickLambda()
                        }
                    },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple()
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewElevatedIconButton() {
    ElevatedIconButton(
        onClick = {},
        onLongClick = {},
        iconDrawRes = R.drawable.ic_plus
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewDisabledElevatedIconButton() {
    ElevatedIconButton(
        onClick = {},
        iconDrawRes = R.drawable.ic_minus,
        enabled = false
    )
}
