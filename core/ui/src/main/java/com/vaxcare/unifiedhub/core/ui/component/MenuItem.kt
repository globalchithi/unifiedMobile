package com.vaxcare.unifiedhub.core.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.R
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme

@Composable
private fun BaseMenuItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    hasDivider: Boolean = false,
    supportingText: String? = null,
    leadingElement: (@Composable () -> Unit)? = null,
    trailingElement: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(VaxCareTheme.color.container.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .padding(
                    vertical = VaxCareTheme.measurement.spacing.small,
                    horizontal = VaxCareTheme.measurement.spacing.medium
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VaxCareTheme.measurement.spacing.xSmall)
        ) {
            trailingElement?.invoke()
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = if (!selected) {
                        VaxCareTheme.type.bodyTypeStyle.body4
                    } else {
                        VaxCareTheme.type.bodyTypeStyle.body4Bold
                    }
                )
                supportingText?.let {
                    Text(
                        text = it,
                        style = VaxCareTheme.type.bodyTypeStyle.body6
                    )
                }
            }
            leadingElement?.invoke()
        }
        if (hasDivider) {
            HorizontalDivider(
                thickness = 2.dp,
                color = VaxCareTheme.color.outline.threeHundred
            )
        }
    }
}

@Composable
fun SingleSelectionMenuItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes trailingIconDrawRes: Int? = null,
    selected: Boolean = false,
    hasDivider: Boolean = false,
    supportingText: String? = null,
) {
    BaseMenuItem(
        title = title,
        onClick = onClick,
        modifier = modifier,
        selected = selected,
        hasDivider = hasDivider,
        supportingText = supportingText,
        leadingElement = {
            if (selected) {
                Icon(
                    painterResource(R.drawable.ic_check),
                    contentDescription = null
                )
            }
        },
        trailingElement = {
            trailingIconDrawRes?.let {
                Icon(
                    painter = painterResource(it),
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
fun MultiSelectionMenuItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingElement: (@Composable () -> Unit)? = null,
    selected: Boolean = false,
    hasDivider: Boolean = false,
    supportingText: String? = null,
) {
    BaseMenuItem(
        title = title,
        onClick = onClick,
        modifier = modifier,
        selected = selected,
        hasDivider = hasDivider,
        supportingText = supportingText,
        leadingElement = {
            val drawableRes =
                if (selected) {
                    R.drawable.ic_check_box_outline
                } else {
                    R.drawable.ic_check_box_outline_blank
                }
            Icon(
                painterResource(drawableRes),
                contentDescription = null
            )
        },
        trailingElement = {
            trailingElement?.invoke()
        }
    )
}

// region BaseMenuItem
@Preview(showBackground = true)
@Composable
private fun PreviewBaseMenuItemWithTrailingElement() {
    BaseMenuItem(
        trailingElement = { Icon(Icons.Default.Favorite, contentDescription = "Menu item") },
        title = "Menu Item",
        onClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewBaseMenuItemWithLeadingElement() {
    BaseMenuItem(
        leadingElement = { Icon(Icons.Default.Favorite, contentDescription = "Supportign text") },
        title = "Menu Item",
        onClick = {},
    ) {
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewBaseMenuItemWithTralingAndLeadingElements() {
    BaseMenuItem(
        title = "Menu Item",
        onClick = {},
        trailingElement = { Icon(Icons.Default.Favorite, contentDescription = "Menu item") },
        leadingElement = { Icon(Icons.Default.Favorite, contentDescription = "Supportign text") },
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewBaseMenuItemWithSupportingTextAndDivider() {
    BaseMenuItem(
        title = "Menu Item",
        supportingText = "Supporting Text",
        onClick = {},
    ) {
    }
}

// endregion
// region SingleSelectionMenuItem
@Preview(showBackground = true)
@Composable
private fun PreviewSingleSelectionMenuItem() {
    SingleSelectionMenuItem(
        title = "Single Selection Menu Item",
        onClick = { },
        hasDivider = true,
        trailingIconDrawRes = R.drawable.ic_presentation_syringe
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSelectedSingleSelectionMenuItem() {
    SingleSelectionMenuItem(
        title = "Selected Single Selection Menu Item",
        onClick = { },
        selected = true,
        hasDivider = true,
        trailingIconDrawRes = R.drawable.ic_presentation_syringe
    )
}

// endregion
// region MultiSelectionMenuItem
@Preview(showBackground = true)
@Composable
private fun PreviewMultiSelectionMenuItem() {
    MultiSelectionMenuItem(
        title = "Multi Selection Menu Item",
        onClick = { },
        hasDivider = true,
        trailingElement = {
            Icon(
                painter = painterResource(R.drawable.ic_presentation_syringe),
                contentDescription = "Menu item"
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSelectedMultiSelectionMenuItem() {
    MultiSelectionMenuItem(
        title = "Multi Single Selection Menu Item",
        onClick = { },
        selected = true,
        hasDivider = true,
        trailingElement = {
            Icon(
                painter = painterResource(R.drawable.ic_presentation_syringe),
                contentDescription = "Menu item"
            )
        }
    )
}
// endregion
