package com.vaxcare.unifiedhub.core.ui.component.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme

@Composable
fun ElevatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    androidx.compose.material3.ElevatedButton(
        onClick = onClick,
        modifier = modifier.sizeIn(minWidth = 104.dp, minHeight = 48.dp),
        enabled = enabled,
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = VaxCareTheme.measurement.elevationRange.one.blur
        ),
        colors = with(VaxCareTheme.color) {
            ButtonDefaults.elevatedButtonColors(
                containerColor = container.primaryContainer,
                contentColor = onContainer.onContainerPrimary,
                disabledContainerColor = container.disabled,
                disabledContentColor = onContainer.primaryInverse
            )
        },
        contentPadding = contentPadding,
        content = content,
    )
}

@Composable
fun ElevatedButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    ElevatedButton(onClick, modifier, enabled) {
        Text(
            text = text,
            style = VaxCareTheme.type.bodyTypeStyle.body5Bold
        )
    }
}
