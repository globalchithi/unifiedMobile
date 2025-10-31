package com.vaxcare.unifiedhub.core.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.R
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseTitleBar(
    modifier: Modifier = Modifier,
    title: String,
    @DrawableRes buttonIcon: Int,
    onButtonClick: () -> Unit,
) {
    BaseTitleBar(modifier, buttonIcon, onButtonClick) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                style = type.titleTypeStyle.titleMedium
            )
        }
    }
}

/**
 * A basic title bar with an [IconButton] followed by a [Text].
 *
 * @param modifier ? I hardly know 'er!
 * @param title The string resource ID of the title of the screen.
 * @param buttonIcon The drawable resource ID of the icon button.
 * @param onButtonClick What that button do?
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseTitleBar(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @DrawableRes buttonIcon: Int,
    onButtonClick: () -> Unit,
) {
    BaseTitleBar(
        modifier = modifier,
        title = stringResource(title),
        buttonIcon = buttonIcon,
        onButtonClick = onButtonClick
    )
}

/**
 * A basic title bar with an [IconButton]
 *
 * @param modifier Good ol' Modifier...
 * @param buttonIcon The drawable resource ID of the icon button
 * @param onButtonClick You want an action?
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseTitleBar(
    modifier: Modifier = Modifier,
    @DrawableRes buttonIcon: Int,
    onButtonClick: () -> Unit,
) {
    BaseTitleBar(modifier, buttonIcon, onButtonClick) { }
}

/**
 * A basic title bar with an [IconButton] followed by a [Composable].
 *
 * @param modifier Would you like to modify this?
 * @param title Any composable... go crazy!
 * @param buttonIcon The drawable resource ID of the icon button.
 * @param onButtonClick Click the button and this will happen!
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseTitleBar(
    modifier: Modifier,
    buttonIcon: Int,
    onButtonClick: () -> Unit,
    title: @Composable () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        title = title,
        navigationIcon = {
            IconButton(
                modifier = Modifier.testTag(TestTags.TopBar.CLOSE_BUTTON),
                onClick = onButtonClick
            ) {
                Icon(
                    painter = painterResource(buttonIcon),
                    contentDescription = "back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = VaxCareTheme.color.surface.surfaceContainer
        )
    )
}

@FullDevicePreview
@Composable
private fun BaseTitleBarPreview() {
    VaxCareTheme {
        BaseTitleBar(
            title = R.string.base_title_bar_preview_title,
            buttonIcon = DesignSystemResource.drawable.ic_close,
            onButtonClick = {},
        )
    }
}
