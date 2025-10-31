package com.vaxcare.unifiedhub.core.ui.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme

@Composable
fun TextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.sizeIn(minWidth = 104.dp, minHeight = 48.dp),
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonColors(
            containerColor = Color.Transparent,
            contentColor = VaxCareTheme.color.onContainer.onContainerPrimary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = VaxCareTheme.color.onContainer.disabled
        ),
    ) {
        Text(text = text, style = VaxCareTheme.type.bodyTypeStyle.body5Bold)
    }
}

@Preview
@Composable
private fun TextButtonPreview() {
    TextButton(
        onClick = {},
        text = "Example Button",
        modifier = Modifier.background(VaxCareTheme.color.surface.surface)
    )
}

@Preview
@Composable
private fun DisabledTextButtonPreview() {
    TextButton(
        onClick = {},
        text = "Example Button",
        modifier = Modifier.background(VaxCareTheme.color.surface.surface),
        enabled = false
    )
}
