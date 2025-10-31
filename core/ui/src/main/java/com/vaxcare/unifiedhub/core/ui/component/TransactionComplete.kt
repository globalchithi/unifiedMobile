package com.vaxcare.unifiedhub.core.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.compose.LocalStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullscreenPreview

@Composable
fun TransactionComplete(
    title: String,
    description: String,
    contentTitle: String,
    modifier: Modifier = Modifier,
    contentPanelHeight: Dp = 640.dp,
    bottomPanel: (@Composable () -> Unit)? = null,
    summaryContent: @Composable () -> Unit,
) {
    val orientation = LocalConfiguration.current.orientation

    Box(
        modifier = modifier.background(LocalStock.current.colors.container)
    ) {
        when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                TransactionCompletePortrait(
                    title = title,
                    description = description,
                    contentTitle = contentTitle,
                    contentPanelHeight = contentPanelHeight,
                    bottomPanel = bottomPanel,
                    summaryContent = summaryContent
                )
            }

            Configuration.ORIENTATION_LANDSCAPE -> {
                TransactionCompleteLandscape(
                    title = title,
                    description = description,
                    contentTitle = contentTitle,
                    contentPanelHeight = contentPanelHeight,
                    bottomPanel = bottomPanel,
                    summaryContent = summaryContent
                )
            }
        }
    }
}

@Composable
private fun TransactionCompletePortrait(
    title: String,
    description: String,
    contentTitle: String,
    contentPanelHeight: Dp,
    modifier: Modifier = Modifier,
    bottomPanel: (@Composable () -> Unit)?,
    summaryContent: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier.fillMaxHeight()
    ) {
        Title(
            title = title,
            modifier = Modifier
                .padding(horizontal = 48.dp)
                .padding(bottom = measurement.spacing.small)
        )

        Description(
            description = description,
            modifier = Modifier
                .padding(horizontal = 48.dp)
                .padding(bottom = measurement.spacing.xLarge)
        )

        ContentPanel(
            title = contentTitle,
            content = summaryContent,
            modifier = Modifier
                .height(contentPanelHeight)
                .fillMaxWidth()
                .padding(horizontal = measurement.spacing.small),
        )

        if (bottomPanel != null) {
            Box(modifier = Modifier.padding(top = 72.dp, bottom = 32.dp)) {
                bottomPanel()
            }
        } else {
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TransactionCompleteLandscape(
    title: String,
    description: String,
    contentTitle: String,
    contentPanelHeight: Dp,
    modifier: Modifier = Modifier,
    bottomPanel: (@Composable () -> Unit)?,
    summaryContent: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxHeight()
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .height(640.dp)
                .weight(1f)
                .padding(end = 44.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Title(
                    title = title,
                    modifier = Modifier
                        .width(430.dp)
                        .padding(bottom = measurement.spacing.small)
                )

                Description(
                    description = description,
                    modifier = Modifier.width(430.dp)
                )
            }

            if (bottomPanel != null) {
                bottomPanel()
            }
        }

        ContentPanel(
            title = contentTitle,
            content = summaryContent,
            modifier = Modifier
                .size(width = 596.dp, height = contentPanelHeight)
                .padding(end = measurement.spacing.medium),
        )
    }
}

@Composable
private fun Title(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = type.displayTypeStyle.display1,
        color = color.onContainer.onContainerSecondary,
        modifier = modifier.padding(top = 21.dp)
    )
}

@Composable
private fun Description(description: String, modifier: Modifier = Modifier) {
    Text(
        text = description,
        style = type.bodyTypeStyle.body2,
        color = color.onContainer.onContainerSecondary,
        modifier = modifier
    )
}

@Composable
private fun ContentPanel(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(measurement.radius.cardMedium),
        color = color.container.primaryContainer,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(
                top = measurement.spacing.large,
                bottom = measurement.spacing.medium,
                start = measurement.spacing.small,
                end = measurement.spacing.small,
            )
        ) {
            Text(
                text = title,
                style = type.bodyTypeStyle.body2Bold,
                modifier = Modifier
                    .padding(
                        start = measurement.spacing.small,
                        end = measurement.spacing.small,
                        bottom = measurement.spacing.medium
                    )
            )

            content()
        }
    }
}

@Composable
fun TransactionComplete(
    title: String,
    description: AnnotatedString,
    contentTitle: String,
    modifier: Modifier = Modifier,
    contentPanelHeight: Dp = 640.dp,
    bottomPanel: (@Composable () -> Unit)? = null,
    summaryContent: @Composable () -> Unit,
) {
    val orientation = LocalConfiguration.current.orientation

    Box(
        modifier = modifier.background(LocalStock.current.colors.container)
    ) {
        when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                TransactionCompletePortrait(
                    title = title,
                    description = description,
                    contentTitle = contentTitle,
                    contentPanelHeight = contentPanelHeight,
                    bottomPanel = bottomPanel,
                    summaryContent = summaryContent
                )
            }

            Configuration.ORIENTATION_LANDSCAPE -> {
                TransactionCompleteLandscape(
                    title = title,
                    description = description,
                    contentTitle = contentTitle,
                    contentPanelHeight = contentPanelHeight,
                    bottomPanel = bottomPanel,
                    summaryContent = summaryContent
                )
            }
        }
    }
}

@Composable
private fun TransactionCompletePortrait(
    title: String,
    description: AnnotatedString,
    contentTitle: String,
    contentPanelHeight: Dp,
    modifier: Modifier = Modifier,
    bottomPanel: (@Composable () -> Unit)?,
    summaryContent: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier.fillMaxHeight()
    ) {
        Title(
            title = title,
            modifier = Modifier
                .padding(horizontal = 48.dp)
                .padding(bottom = measurement.spacing.small)
        )

        Description(
            description = description,
            modifier = Modifier
                .padding(horizontal = 48.dp)
                .padding(bottom = measurement.spacing.xLarge)
        )

        ContentPanel(
            title = contentTitle,
            content = summaryContent,
            modifier = Modifier
                .height(contentPanelHeight)
                .fillMaxWidth()
                .padding(horizontal = measurement.spacing.small),
        )

        if (bottomPanel != null) {
            Box(modifier = Modifier.padding(top = 72.dp, bottom = 32.dp)) {
                bottomPanel()
            }
        } else {
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TransactionCompleteLandscape(
    title: String,
    description: AnnotatedString,
    contentTitle: String,
    contentPanelHeight: Dp,
    modifier: Modifier = Modifier,
    bottomPanel: (@Composable () -> Unit)?,
    summaryContent: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxHeight()
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .height(640.dp)
                .weight(1f)
                .padding(end = 44.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Title(
                    title = title,
                    modifier = Modifier
                        .width(430.dp)
                        .padding(bottom = measurement.spacing.small)
                )

                Description(
                    description = description,
                    modifier = Modifier.width(430.dp)
                )
            }

            if (bottomPanel != null) {
                bottomPanel()
            }
        }

        ContentPanel(
            title = contentTitle,
            content = summaryContent,
            modifier = Modifier
                .size(width = 596.dp, height = contentPanelHeight)
                .padding(end = measurement.spacing.medium),
        )
    }
}

@Composable
private fun Description(description: AnnotatedString, modifier: Modifier = Modifier) {
    Text(
        text = description,
        style = type.bodyTypeStyle.body2,
        color = color.onContainer.onContainerSecondary,
        modifier = modifier
    )
}

@FullDevicePreview
@Composable
private fun Default() {
    FullscreenPreview {
        TransactionComplete(
            title = "Sample",
            description = "This is a sample transaction summary screen for preview purposes.",
            contentTitle = "June 9, 2025",
            summaryContent = {
                Text("Hello world")
            }
        )
    }
}
