package com.vaxcare.unifiedhub.core.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.vaxcare.unifiedhub.core.designsystem.R
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme

const val PASSWORD_INPUT_FIELD_TEXT_FIELD = "passwordInputField_textField"

@Composable
fun PasswordInputField(
    initialLabel: String,
    modifier: Modifier = Modifier,
    value: String,
    isEnabled: Boolean,
    onClearClick: () -> Unit,
    onPasswordChange: (String) -> Unit,
    onKeyboardDone: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = VaxCareTheme.color
    val typeScale = VaxCareTheme.type
    DisableSelection {
        BasicTextField(
            value = value,
            onValueChange = onPasswordChange,
            modifier = modifier.testTag(PASSWORD_INPUT_FIELD_TEXT_FIELD),
            readOnly = !isEnabled,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onKeyboardDone() }),
            visualTransformation = PasswordVisualTransformation(mask = Char(42)),
            interactionSource = interactionSource,
            textStyle = typeScale.bodyTypeStyle.body1Bold.copy(
                textAlign = TextAlign.Center,
                letterSpacing = 20.sp,
                lineHeight = 1.em,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.LastLineBottom
                )
            ),
            cursorBrush = SolidColor(Transparent),
        ) {
            Row(
                modifier = Modifier.height(80.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = initialLabel,
                        style = typeScale.bodyTypeStyle.body3,
                        color = colors.onContainer.onContainerPrimary,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Column {
                        // Need this to give the asterisk a "padding" so it is aligned
                        Spacer(Modifier.height(18.dp))
                        it()
                    }
                    IconButton(onClick = onClearClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            tint = colors.onContainer.disabled,
                            contentDescription = "clear"
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun PasswordInputFieldPreview() {
    VaxCareTheme {
        Box(Modifier.fillMaxSize()) {
            PasswordInputField(
                initialLabel = "Enter Password for 012345*",
                value = "1234**",
                isEnabled = true,
                onPasswordChange = {},
                onKeyboardDone = {},
                onClearClick = {}
            )
        }
    }
}
