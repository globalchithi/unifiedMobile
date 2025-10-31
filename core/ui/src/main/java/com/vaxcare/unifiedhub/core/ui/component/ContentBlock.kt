package com.vaxcare.unifiedhub.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.R
import com.vaxcare.unifiedhub.core.designsystem.component.ContentBlockTrailingContent.BUTTON
import com.vaxcare.unifiedhub.core.designsystem.component.ContentBlockTrailingContent.ICON
import com.vaxcare.unifiedhub.core.designsystem.component.ContentBlockTrailingContent.NONE
import com.vaxcare.unifiedhub.core.designsystem.component.ContentBlockTrailingContent.TEXT
import com.vaxcare.unifiedhub.core.designsystem.component.ContentBlockTrailingContent.TEXT_AND_ICON
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.ui.component.button.OutlineButton

const val ADD_CUSTOM_TEXT_HINT = "Add Custom Text"

@Composable
fun ContentBlock(
    modifier: Modifier = Modifier,
    headerText: String? = null,
    bodyText: String? = null,
) {
    ContentBlock(
        modifier = modifier,
        headerText = headerText,
        bodyText = bodyText,
        trailing = NONE
    )
}

@Composable
fun ContentBlockTextTrailing(
    trailingText: String,
    modifier: Modifier = Modifier,
    headerText: String? = null,
    bodyText: String? = null
) {
    ContentBlock(
        modifier = modifier,
        headerText = headerText,
        bodyText = bodyText,
        trailing = TEXT,
        trailingText = trailingText
    )
}

@Composable
fun ContentBlockIconTrailing(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    headerText: String? = null,
    bodyText: String? = null,
) {
    ContentBlock(
        modifier = modifier,
        headerText = headerText,
        bodyText = bodyText,
        trailing = ICON,
        onClick = onClick
    )
}

@Composable
fun ContentBlockTextAndIconTrailing(
    trailingText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    headerText: String? = null,
    bodyText: String? = null,
) {
    ContentBlock(
        modifier = modifier,
        headerText = headerText,
        bodyText = bodyText,
        trailing = TEXT_AND_ICON,
        trailingText = trailingText,
        onClick = onClick
    )
}

@Composable
fun ContentBlockButtonTrailing(
    buttonText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    headerText: String? = null,
    bodyText: String? = null,
) {
    ContentBlock(
        modifier = modifier,
        headerText = headerText,
        bodyText = bodyText,
        trailing = BUTTON,
        buttonText = buttonText,
        onClick = onClick
    )
}

@Composable
private fun ContentBlock(
    modifier: Modifier = Modifier,
    headerText: String? = null,
    bodyText: String? = null,
    trailing: ContentBlockTrailingContent,
    trailingText: String? = null,
    buttonText: String? = null,
    showDivider: Boolean = true,
    onClick: () -> Unit = { }
) {
    Column(
        Modifier
            .background(VaxCareTheme.color.container.primaryContainer)
            .fillMaxWidth()
            .clickable(
                enabled = when (trailing) {
                    TEXT, NONE -> false
                    TEXT_AND_ICON, ICON, BUTTON -> true
                },
                onClick = { onClick() }
            ),
        verticalArrangement = Arrangement.spacedBy(VaxCareTheme.measurement.spacing.xSmall)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = VaxCareTheme.measurement.spacing.small)
                    .weight(0.5f),
                verticalArrangement = Arrangement.spacedBy(VaxCareTheme.measurement.spacing.xSmall)
            ) {
                headerText?.let {
                    Text(
                        style = VaxCareTheme.type.bodyTypeStyle.body4Bold,
                        text = it
                    )
                }
                bodyText?.let {
                    Text(
                        style = VaxCareTheme.type.bodyTypeStyle.body4,
                        text = it
                    )
                }
            }
            Column(
                modifier = Modifier.weight(0.5f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
            ) {
                when (trailing) {
                    TEXT -> Text(
                        style = VaxCareTheme.type.bodyTypeStyle.body4,
                        text = trailingText?.let { trailingText }
                            ?: ADD_CUSTOM_TEXT_HINT
                    )

                    TEXT_AND_ICON -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                style = VaxCareTheme.type.bodyTypeStyle.body4,
                                text = trailingText?.let { trailingText } ?: ADD_CUSTOM_TEXT_HINT
                            )
                            Icon(
                                modifier = modifier.padding(16.dp),
                                painter = painterResource(R.drawable.ic_chevron_right),
                                contentDescription = "Continue"
                            )
                        }
                    }

                    BUTTON -> {
                        OutlineButton(
                            text = buttonText ?: ADD_CUSTOM_TEXT_HINT,
                            onClick = { onClick() }
                        )
                    }

                    ICON -> {
                        Icon(
                            modifier = modifier.padding(16.dp),
                            painter = painterResource(R.drawable.ic_chevron_right),
                            contentDescription = "Continue"
                        )
                    }

                    NONE -> {}
                }
            }
        }
        if (showDivider) {
            HorizontalDivider(thickness = 2.dp, color = VaxCareTheme.color.outline.threeHundred)
        }
    }
}

enum class ContentBlockTrailingContent {
    TEXT,
    TEXT_AND_ICON,
    ICON,
    BUTTON,
    NONE
}

@Preview(showBackground = true, device = Devices.PIXEL_TABLET)
@Composable
fun PreviewContentBlockWithTrailingButton() {
    VaxCareTheme {
        ContentBlock(
            showDivider = true,
            headerText = "Header",
            bodyText = "Body - The Serial Number allows VaxCare to track the location of this Hub.",
            trailing = ContentBlockTrailingContent.BUTTON
        ) { }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_TABLET)
@Composable
fun PreviewContentBlockWithTrailingText() {
    VaxCareTheme {
        ContentBlock(
            showDivider = true,
            headerText = "Header",
            bodyText = "Body - The Serial Number allows VaxCare to track the location of this Hub.",
            trailing = ContentBlockTrailingContent.TEXT
        ) { }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_TABLET)
@Composable
fun PreviewContentBlockWithTrailingTextAndIcon() {
    VaxCareTheme {
        ContentBlock(
            showDivider = true,
            headerText = "Header",
            bodyText = "Body - The Serial Number allows VaxCare to track the location of this Hub.",
            trailing = ContentBlockTrailingContent.TEXT_AND_ICON
        ) { }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_TABLET)
@Composable
fun PreviewContentBlockWithTrailingIcon() {
    VaxCareTheme {
        ContentBlock(
            showDivider = true,
            headerText = "Header",
            bodyText = "Body - The Serial Number allows VaxCare to track the location of this Hub.",
            trailing = ContentBlockTrailingContent.ICON
        ) { }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_TABLET)
@Composable
fun PreviewContentBlockWithNoTrailing() {
    VaxCareTheme {
        ContentBlock(
            showDivider = true,
            headerText = "Header",
            bodyText = "Body - The Serial Number allows VaxCare to track the location of this Hub.",
            trailing = ContentBlockTrailingContent.NONE
        ) { }
    }
}
