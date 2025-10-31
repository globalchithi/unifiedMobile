package com.vaxcare.unifiedhub.core.ui.component.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.R
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.ui.compose.LocalStock

@Composable
fun TonalButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    @DrawableRes trailingIconRes: Int? = null,
    @DrawableRes leadingIconRes: Int? = null,
    highlight: Boolean = false,
    enabled: Boolean = true,
) {
    Button(
        modifier = modifier.sizeIn(minWidth = 104.dp, minHeight = 64.dp),
        onClick = onClick,
        elevation = if (!highlight) ButtonDefaults.buttonElevation(4.dp) else ButtonDefaults.buttonElevation(),
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonColors(
            containerColor = if (!highlight) {
                VaxCareTheme.color.container.primaryContainer
            } else {
                LocalStock.current.colors.containerLight
            },
            contentColor = VaxCareTheme.color.onContainer.onContainerPrimary,
            disabledContainerColor = VaxCareTheme.color.container.disabled,
            disabledContentColor = VaxCareTheme.color.onContainer.disabled
        ),
    ) {
        Box(
            Modifier.fillMaxWidth(),
        ) {
            trailingIconRes?.let {
                Icon(
                    modifier = Modifier.align(Alignment.CenterStart),
                    painter = painterResource(it),
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center,
                text = text,
                style = if (!highlight) {
                    VaxCareTheme.type.bodyTypeStyle.body3
                } else {
                    VaxCareTheme.type.bodyTypeStyle.body3Bold
                }
            )
            leadingIconRes?.let {
                Icon(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    painter = painterResource(it),
                    contentDescription = null
                )
            }
        }
    }
}

@Preview
@Composable
private fun SecondaryButtonPreview() {
    TonalButton(
        modifier = Modifier.widthIn(min = 1000.dp),
        onClick = {},
        trailingIconRes = R.drawable.ic_search,
        leadingIconRes = R.drawable.ic_search,
        text = "Example Button",
    )
}

@Preview
@Composable
private fun HIghlightedSecondaryButtonPreview() {
    TonalButton(
        onClick = {},
        text = "Example Button",
        highlight = true,
    )
}

@Preview
@Composable
private fun DisabledSecondaryButtonPreview() {
    TonalButton(
        onClick = {},
        leadingIconRes = R.drawable.ic_search,
        text = "Example Button",
        enabled = false
    )
}
