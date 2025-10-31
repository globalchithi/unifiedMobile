package com.vaxcare.unifiedhub.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type

@Composable
fun MenuItemCard(
    text: String,
    icon: @Composable () -> Unit,
    containerColor: Color,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = measurement.spacing.medium,
        vertical = measurement.spacing.large,
    ),
    shadowElevation: Dp = 3.dp,
    textStyle: TextStyle = type.bodyTypeStyle.body2Bold,
    onClick: (() -> Unit)? = null,
) {
    if (onClick != null) {
        Surface(
            shape = RoundedCornerShape(measurement.radius.cardMedium),
            color = containerColor,
            shadowElevation = shadowElevation,
            onClick = onClick,
            modifier = modifier
        ) {
            MenuItemCardContent(
                text = text,
                icon = icon,
                textStyle = textStyle,
                contentPadding = contentPadding
            )
        }
    } else {
        Surface(
            shape = RoundedCornerShape(measurement.radius.cardMedium),
            color = containerColor,
            shadowElevation = shadowElevation,
            modifier = modifier
        ) {
            MenuItemCardContent(
                text = text,
                icon = icon,
                textStyle = textStyle,
                contentPadding = contentPadding
            )
        }
    }
}

@Composable
private fun MenuItemCardContent(
    text: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = measurement.spacing.medium,
        vertical = measurement.spacing.large,
    ),
    textStyle: TextStyle = type.bodyTypeStyle.body2Bold
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        Text(
            text = text,
            style = textStyle,
            modifier = Modifier.padding(end = measurement.spacing.large)
        )

        icon()
    }
}
