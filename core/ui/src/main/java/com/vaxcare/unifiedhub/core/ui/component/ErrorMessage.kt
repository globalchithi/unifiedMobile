package com.vaxcare.unifiedhub.core.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme

@Composable
fun ErrorMessage(
    isError: Boolean,
    @StringRes text: Int,
    textStyle: TextStyle = VaxCareTheme.type.bodyTypeStyle.body4,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = isError,
        label = "Error text",
    ) {
        Text(
            text = stringResource(text),
            style = textStyle,
            color = VaxCareTheme.color.onContainer.error,
        )
    }
}
