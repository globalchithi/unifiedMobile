package com.vaxcare.unifiedhub.core.ui.component.button

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    PrimaryButton(onClick, modifier, enabled) {
        Text(
            style = VaxCareTheme.type.bodyTypeStyle.body5Bold,
            text = text
        )
    }
}

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.sizeIn(minWidth = 104.dp, minHeight = 48.dp),
        shape = CircleShape,
        colors = with(VaxCareTheme.color) {
            ButtonColors(
                containerColor = container.secondaryContainer,
                contentColor = onContainer.primaryInverse,
                disabledContainerColor = container.disabled,
                disabledContentColor = onContainer.disabled
            )
        },
        enabled = enabled,
        content = content,
    )
}

@Preview
@Composable
private fun PreviewEnabledVaxCarePrimaryButton() {
    PrimaryButton(text = "button", onClick = { })
}

@Preview
@Composable
private fun PreviewDisabledVaxCarePrimaryButton() {
    PrimaryButton(text = "button", enabled = false, onClick = { })
}
