package com.vaxcare.unifiedhub.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.ui.component.button.OutlineButton
import com.vaxcare.unifiedhub.core.ui.component.button.PrimaryButton
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview

/**
 * A foundational dialog component that hosts any custom content.
 *
 * It provides the basic non-dismissible dialog window and a themed surface.
 * The content is completely flexible.
 *
 * @param onDismissRequest Called when the user requests to dismiss the dialog. NOTE: This is only triggered by explicit actions you define inside your content (e.g., a close button), as dismissOnBackPress and dismissOnClickOutside are disabled.
 * @param modifier The modifier to be applied to the dialog's content surface.
 * @param content The composable content to be displayed inside the dialog.
 */
@Composable
fun VCContentDialog(
    onDismissRequest: () -> Unit,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false // Important for custom content sizing
        )
    ) {
        Surface(
            shape = shape,
            color = VaxCareTheme.color.container.primaryContainer,
            content = content,
            modifier = modifier,
        )
    }
}

/**
 * A flexible, AlertDialog-based component for structured dialogs.
 *
 * This composable provides the standard dialog structure (title, text, buttons).
 * It is not dismissible via back press or by clicking outside the dialog bounds.
 *
 * @param onDismissRequest Called when the user requests to dismiss the dialog.
 * @param title The title of the dialog.
 * @param text The main content of the dialog.
 * @param primaryButton The primary action button for the dialog.
 * @param secondaryButton The secondary action button for the dialog.
 * @param modifier The modifier to be applied to the dialog.
 */
@Composable
fun VCDialog(
    onDismissRequest: () -> Unit,
    title: @Composable (() -> Unit)?,
    text: @Composable (() -> Unit)?,
    primaryButton: @Composable (() -> Unit)?,
    secondaryButton: @Composable (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        text = text,
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(VaxCareTheme.measurement.spacing.medium),
                horizontalArrangement = Arrangement.End
            ) {
                secondaryButton?.invoke()
                primaryButton?.invoke()
            }
        },
        containerColor = VaxCareTheme.color.container.primaryContainer,
        modifier = modifier.width(width = 640.dp),
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}

/**
 * A basic dialog with a title, text, and one or two buttons.
 *
 * This is a convenience composable that builds on top of [VCDialog]
 * for common dialog use cases. It is not dismissible via back press or by
 * clicking outside the dialog bounds.
 *
 * @param onDismissRequest Called when the user requests to dismiss the dialog.
 * @param primaryButtonConfig The configuration for the primary action button.
 * @param secondaryButtonConfig The configuration for the secondary action button.
 * @param title The title of the dialog.
 * @param text The main content of the dialog.
 * @param modifier The modifier to be applied to the dialog.
 */
@Composable
fun VCBasicDialog(
    onDismissRequest: () -> Unit,
    primaryButtonConfig: ButtonConfig,
    title: String?,
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    secondaryButtonConfig: ButtonConfig? = null
) {
    VCDialog(
        onDismissRequest = onDismissRequest,
        title = title?.let {
            {
                Text(
                    text = it,
                    style = VaxCareTheme.type.bodyTypeStyle.body3Bold
                )
            }
        },
        text = { Text(text = text, style = VaxCareTheme.type.bodyTypeStyle.body3) },
        primaryButton = {
            PrimaryButton(
                onClick = primaryButtonConfig.onClick,
                text = primaryButtonConfig.text
            )
        },
        secondaryButton = secondaryButtonConfig?.let { config ->
            {
                OutlineButton(
                    onClick = config.onClick,
                    text = config.text,
                    modifier = Modifier.padding(end = VaxCareTheme.measurement.spacing.small)
                )
            }
        },
        modifier = modifier
    )
}

/**
 * Convenience overload for [VCBasicDialog] that accepts a plain String for the text.
 */
@Composable
fun VCBasicDialog(
    onDismissRequest: () -> Unit,
    primaryButtonConfig: ButtonConfig,
    title: String?,
    text: String?,
    modifier: Modifier = Modifier,
    secondaryButtonConfig: ButtonConfig? = null
) {
    VCBasicDialog(
        onDismissRequest = onDismissRequest,
        primaryButtonConfig = primaryButtonConfig,
        secondaryButtonConfig = secondaryButtonConfig,
        title = title,
        text = buildAnnotatedString { append(text.orEmpty()) },
        modifier = modifier
    )
}

/**
 * Data class to represent the configuration for a dialog button.
 *
 * @property text The text to be displayed on the button.
 * @property onClick The lambda to be executed when the button is clicked.
 */
data class ButtonConfig(
    val text: String,
    val onClick: () -> Unit,
    val testTag: String? = null
)

// --- PREVIEWS ---
@FullDevicePreview
@Composable
private fun VCBasicDialog_Preview_TwoButtons() {
    VaxCareTheme {
        VCBasicDialog(
            onDismissRequest = {},
            title = "Confirm Action",
            text = "Are you sure you want to perform this action? It cannot be undone.",
            primaryButtonConfig = ButtonConfig("Confirm", {}),
            secondaryButtonConfig = ButtonConfig("Cancel", {})
        )
    }
}

@FullDevicePreview
@Composable
private fun VCBasicDialog_Preview_OneButton() {
    VaxCareTheme {
        VCBasicDialog(
            onDismissRequest = {},
            title = "Success",
            text = "Your vaccine count has been successfully updated.",
            primaryButtonConfig = ButtonConfig("OK", {})
        )
    }
}

@FullDevicePreview
@Composable
private fun VCContentDialog_Preview_CustomContent() {
    var text by remember { mutableStateOf("") }
    VaxCareTheme {
        VCContentDialog(
            onDismissRequest = {},
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(VaxCareTheme.measurement.spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Edit Vaccine Name",
                    style = VaxCareTheme.type.bodyTypeStyle.body2Bold,
                    modifier = Modifier.padding(bottom = VaxCareTheme.measurement.spacing.medium)
                )

                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = "New Vaccine Name",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(VaxCareTheme.measurement.spacing.large))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlineButton(onClick = {}, text = "Cancel")
                    Spacer(Modifier.width(VaxCareTheme.measurement.spacing.small))
                    PrimaryButton(onClick = {}, text = "Save")
                }
            }
        }
    }
}
