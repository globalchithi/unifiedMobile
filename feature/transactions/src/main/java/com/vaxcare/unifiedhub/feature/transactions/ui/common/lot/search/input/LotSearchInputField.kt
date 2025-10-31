package com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.search.input

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.R
import com.vaxcare.unifiedhub.core.designsystem.theme.Spacings
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme

const val LOT_SEARCH_INPUT_FIELD_TEXT_FIELD = "lotSearchInputField_textField"

@Composable
fun VaxCareTheme.LotSearchInputField(
    initialLabel: String,
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    isEnabled: Boolean,
    onTextChanged: (TextFieldValue) -> Unit,
    onClearClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = value,
        onValueChange = onTextChanged,
        modifier = modifier.testTag(LOT_SEARCH_INPUT_FIELD_TEXT_FIELD),
        readOnly = !isEnabled,
        visualTransformation = {
            TransformedText(
                it.toUpperCase(),
                offsetMapping = OffsetMapping.Identity
            )
        },
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus(true)
            }
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            capitalization = KeyboardCapitalization.Characters
        ),
        interactionSource = interactionSource,
        textStyle = type.bodyTypeStyle.body4,
        cursorBrush = SolidColor(Transparent),
    ) {
        Row(
            modifier = Modifier
                .size(width = measurement.size.buttonsWizard, height = 56.dp)
                .background(
                    color = color.container.primaryContainer,
                    shape = RoundedCornerShape(Spacings.S700)
                ).padding(Spacings.S200),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (value.text.isEmpty()) {
                Text(
                    modifier = Modifier.padding(start = 48.dp),
                    text = initialLabel,
                    style = type.bodyTypeStyle.body3,
                    color = color.onContainer.onContainerPrimary,
                    fontStyle = FontStyle.Italic
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.size(48.dp))
                    it()
                }
            }

            IconButton(
                onClick = onClearClick,
                enabled = value.text.isNotEmpty()
            ) {
                Icon(
                    painter = painterResource(
                        if (value.text.isNotEmpty()) {
                            R.drawable.ic_close
                        } else {
                            R.drawable.ic_search
                        }
                    ),
                    tint = color.onContainer.onContainerPrimary,
                    contentDescription = "search"
                )
            }
        }
    }
}
