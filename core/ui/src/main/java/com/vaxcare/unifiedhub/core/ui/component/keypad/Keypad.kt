package com.vaxcare.unifiedhub.core.ui.component.keypad

import android.media.AudioManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.RippleDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vaxcare.unifiedhub.core.designsystem.theme.RadiusUnit
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.component.keypad.KeypadTokens.keypadRows
import com.vaxcare.unifiedhub.core.ui.compose.LocalAudioManager
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullscreenPreview
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
fun KeypadDialog(
    modifier: Modifier = Modifier,
    dialogTitle: String,
    input: String,
    onCloseClick: () -> Unit,
    onDigitClick: (Char) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit,
    onSubmit: () -> Unit,
    isConfirmEnabled: Boolean = true,
) {
    val audioManager = LocalAudioManager.current

    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
        onDismissRequest = onCloseClick,
    ) {
        Surface(
            color = color.container.primaryContainer,
            shape = RoundedCornerShape(20.dp),
            modifier = modifier
                .size(width = 560.dp, height = 648.dp)
                .testTag(TestTags.KeyPad.CONTAINER)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = measurement.spacing.large)
            ) {
                KeypadTopBar(
                    title = dialogTitle,
                    onCloseClick = onCloseClick,
                )
                KeypadInputField(
                    value = input,
                    onClearClick = onClearClick
                )
                Keypad(
                    isDeleteEnabled = input.isNotEmpty(),
                    isConfirmEnabled = input.isNotEmpty() && isConfirmEnabled,
                    onPlaySound = { audioManager?.playKeyPadSound() },
                    onDigitClick = onDigitClick,
                    onDeleteClick = onDeleteClick,
                    onConfirmClick = onSubmit
                )
            }
        }
    }
}

@Composable
private fun KeypadTopBar(
    modifier: Modifier = Modifier,
    title: String,
    onCloseClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = measurement.spacing.medium)
            .padding(bottom = measurement.spacing.xLarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier
                .padding(end = measurement.spacing.medium)
                .size(40.dp)
                .testTag(TestTags.KeyPad.CLOSE_BUTTON),
            onClick = onCloseClick
        ) {
            Icon(
                painter = painterResource(id = DesignSystemR.drawable.ic_close),
                tint = color.onContainer.disabled,
                contentDescription = "Close keypad for $title",
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = title,
            style = type.bodyTypeStyle.body4Bold,
        )
    }
}

@Composable
fun Keypad(
    isDeleteEnabled: Boolean,
    isConfirmEnabled: Boolean,
    isKeypadEnabled: Boolean = true,
    onPlaySound: () -> Unit,
    onDigitClick: (Char) -> Unit,
    onDeleteClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    Column {
        keypadRows.forEach { row ->
            Row {
                row.forEach {
                    when (it.first) {
                        'd' -> {
                            val tint = with(color.onContainer) {
                                if (isDeleteEnabled) onContainerPrimary else disabled
                            }

                            KeypadButton(
                                modifier = Modifier
                                    .testTag(TestTags.KeyPad.BACKSPACE_BUTTON),
                                icon = {
                                    Icon(
                                        painter = painterResource(it.second),
                                        contentDescription = "Backspace",
                                        tint = tint,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                },
                                enabled = isDeleteEnabled,
                                onClick = {
                                    onPlaySound()
                                    onDeleteClick()
                                }
                            )
                        }

                        'c' -> {
                            val tint = with(color.onContainer) {
                                if (isConfirmEnabled) stockPrivate else disabled
                            }

                            KeypadButton(
                                modifier = Modifier
                                    .testTag(TestTags.KeyPad.CONFIRM_BUTTON),
                                icon = {
                                    Icon(
                                        painter = painterResource(it.second),
                                        contentDescription = "Confirm",
                                        tint = tint,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                },
                                enabled = isConfirmEnabled,
                                onClick = {
                                    onPlaySound()
                                    onConfirmClick()
                                },
                            )
                        }

                        else -> {
                            val tint = with(color.onContainer) {
                                if (isKeypadEnabled) onContainerPrimary else disabled
                            }

                            KeypadButton(
                                modifier = Modifier.testTag(TestTags.KeyPad.digitButton(it.first)),
                                icon = {
                                    Icon(
                                        painter = painterResource(it.second),
                                        contentDescription = it.first.toString(),
                                        tint = tint,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                },
                                enabled = isKeypadEnabled,
                                onClick = {
                                    onPlaySound()
                                    onDigitClick(it.first)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeypadButton(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val rippleConfig = RippleConfiguration(
        color = color.outline.sixHundred,
        rippleAlpha = RippleAlpha(
            draggedAlpha = RippleDefaults.RippleAlpha.draggedAlpha,
            focusedAlpha = RippleDefaults.RippleAlpha.focusedAlpha,
            hoveredAlpha = RippleDefaults.RippleAlpha.hoveredAlpha,
            pressedAlpha = 1f
        )
    )

    CompositionLocalProvider(LocalRippleConfiguration provides rippleConfig) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                // size: 40 + padding: 56. This way, the padding is clickable
                .size(96.dp)
                .clickable(
                    interactionSource = interactionSource,
                    enabled = enabled,
                    onClick = onClick,
                    indication = null
                ),
        ) {
            // draw the opaque ripple first
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(RadiusUnit.R100))
                    .indication(
                        interactionSource = interactionSource,
                        indication = ripple(color = color.outline.sixHundred)
                    ),
            ) {}

            // draw button content on top of the ripple
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                icon()
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(width = 28.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(color.outline.sixHundred)
                )
            }
        }
    }
}

@Composable
fun KeypadInputField(
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation? = null,
    textStyle: TextStyle = type.headerTypeStyle.headlineMedium,
    value: String,
    onClearClick: () -> Unit
) {
    val transformedValue = remember(value) {
        visualTransformation?.filter(AnnotatedString(value))?.text
    } ?: AnnotatedString(value)
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(bottom = measurement.spacing.large)
            .size(height = 40.dp, width = 232.dp),
    ) {
        Text(
            text = transformedValue,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = textStyle,
            fontWeight = FontWeight.SemiBold
        )
        IconButton(
            modifier = Modifier
                .testTag(TestTags.KeyPad.CLEAR_BUTTON)
                .size(40.dp),
            enabled = transformedValue.isNotEmpty(),
            onClick = onClearClick,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = color.onContainer.link,
                disabledContentColor = color.onContainer.disabled
            )
        ) {
            Icon(
                painter = painterResource(DesignSystemR.drawable.ic_close),
                contentDescription = "Close",
                tint = color.onContainer.disabled,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

fun AudioManager.playKeyPadSound() {
    this.playSoundEffect(AudioManager.FX_KEY_CLICK)
}

@FullDevicePreview
@Composable
private fun DefaultKeypadPreview() {
    FullscreenPreview {
        KeypadDialog(
            dialogTitle = "Type them numbers",
            input = "12345",
            onCloseClick = {},
            onDigitClick = {},
            onDeleteClick = {},
            onClearClick = {},
            onSubmit = {},
        )
    }
}

@FullDevicePreview
@Composable
private fun EmptyKeypadPreview() {
    FullscreenPreview {
        KeypadDialog(
            dialogTitle = "Empty Keypad",
            input = "",
            onCloseClick = {},
            onDigitClick = {},
            onDeleteClick = {},
            onClearClick = {},
            onSubmit = {},
        )
    }
}
