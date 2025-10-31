package com.vaxcare.unifiedhub.core.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.R
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.ui.component.TextFieldState.DISABLED
import com.vaxcare.unifiedhub.core.ui.component.TextFieldState.ENABLED
import com.vaxcare.unifiedhub.core.ui.component.TextFieldState.ERROR
import com.vaxcare.unifiedhub.core.ui.component.TextFieldState.FOCUS

enum class TextFieldState { ENABLED, FOCUS, DISABLED, ERROR }

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    state: TextFieldState = ENABLED,
    placeholder: String? = null,
    @DrawableRes leadingDrawRes: Int? = null,
    @DrawableRes trailingDrawRes: Int? = null,
    supportingText: String? = null,
    optional: String? = null,
    readOnly: Boolean = false,
    onClick: () -> Unit = {},
    trailingIconClick: () -> Unit = { }
) {
    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = when (state) {
            FOCUS -> VaxCareTheme.color.outline.fourHundred
            ERROR -> VaxCareTheme.color.outline.error
            else -> VaxCareTheme.color.outline.threeHundred
        },
        unfocusedContainerColor = VaxCareTheme.color.container.primaryContainer,
        errorContainerColor = VaxCareTheme.color.container.primaryContainer,
        focusedContainerColor = VaxCareTheme.color.container.primaryContainer,
        disabledContainerColor = VaxCareTheme.color.container.primaryContainer,
        unfocusedBorderColor = VaxCareTheme.color.outline.fourHundred,
        disabledBorderColor = VaxCareTheme.color.outline.threeHundred,
        errorBorderColor = VaxCareTheme.color.outline.error,
        focusedLabelColor = VaxCareTheme.color.onContainer.onContainerPrimary,
        unfocusedLabelColor = VaxCareTheme.color.onContainer.disabled,
        disabledLabelColor = VaxCareTheme.color.onContainer.disabled,
        errorLabelColor = VaxCareTheme.color.onContainer.error,
        disabledTextColor = VaxCareTheme.color.onContainer.disabled
    )

    val shape = RoundedCornerShape(16.dp)
    val focusRequester = remember { FocusRequester() }

    Column(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label.uppercase(),
                color = if (state == DISABLED) {
                    VaxCareTheme.color.onContainer.disabled
                } else {
                    VaxCareTheme.color.onContainer.onContainerPrimary
                },
                style = VaxCareTheme.type.bodyTypeStyle.label
            )
            if (!optional.isNullOrEmpty()) {
                Text(
                    text = optional,
                    color = if (state == DISABLED) {
                        VaxCareTheme.color.onContainer.disabled
                    } else {
                        VaxCareTheme.color.onContainer.onContainerPrimary
                    },
                    style = VaxCareTheme.type.bodyTypeStyle.body6
                )
            }
        }

        val leadingIconComposable: (@Composable (() -> Unit))? =
            leadingDrawRes?.let { res ->
                { Icon(painterResource(res), contentDescription = null) }
            }

        val trailingIconComposable: (@Composable (() -> Unit))? =
            trailingDrawRes?.let { res ->
                {
                    IconButton(
                        onClick = trailingIconClick,
                        enabled = state != DISABLED,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = VaxCareTheme.color.onContainer.onContainerPrimary
                        )
                    ) {
                        Icon(painterResource(res), contentDescription = null)
                    }
                }
            }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = value,
            onValueChange = onValueChange,
            shape = shape,
            textStyle = VaxCareTheme.type.bodyTypeStyle.body5,
            singleLine = true,
            enabled = state != DISABLED,
            isError = state == ERROR,
            readOnly = readOnly,
            leadingIcon = leadingIconComposable,
            trailingIcon = trailingIconComposable,
            placeholder = {
                placeholder?.let { Text(it) }
            },
            colors = colors,
            interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        onClick()
                    }
                }
            }
        )

        supportingText?.let {
            val style = VaxCareTheme.type.bodyTypeStyle.body6
            val color = when (state) {
                DISABLED -> VaxCareTheme.color.onContainer.disabled
                ERROR -> MaterialTheme.colorScheme.error
                else -> VaxCareTheme.color.onContainer.onContainerPrimary
            }
            Text(
                text = it,
                style = style,
                color = color,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEnabledTextField() {
    TextField(
        value = "Value",
        onValueChange = { _ -> },
        label = "label",
        placeholder = "Placeholder",
        optional = "Optional",
        leadingDrawRes = R.drawable.ic_search,
        state = ENABLED,
        supportingText = "Support text",
        trailingDrawRes = R.drawable.ic_close
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewFocusTextField() {
    TextField(
        value = "",
        onValueChange = { _ -> },
        label = "label",
        readOnly = true,
        placeholder = "Placeholder",
        optional = "Optional",
        state = FOCUS,
        supportingText = "Support text"
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewDisabledTextField() {
    TextField(
        value = "",
        onValueChange = { _ -> },
        label = "label",
        placeholder = "Placeholder",
        optional = "Optional",
        leadingDrawRes = R.drawable.ic_search,
        state = DISABLED,
        supportingText = "Support text",
        trailingDrawRes = R.drawable.ic_close
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewErrorTextField() {
    TextField(
        value = "",
        onValueChange = { _ -> },
        label = "label",
        placeholder = "Placeholder",
        optional = "Optional",
        leadingDrawRes = R.drawable.ic_search,
        state = ERROR,
        supportingText = "Support text",
        trailingDrawRes = R.drawable.ic_close
    )
}
