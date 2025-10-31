package com.vaxcare.unifiedhub.feature.home.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.R
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.component.button.ElevatedIconButton
import com.vaxcare.unifiedhub.core.ui.compose.LocalStock
import com.vaxcare.unifiedhub.core.ui.model.StockUi

@Composable
fun StockSelectorOneLine(
    selectedStock: StockUi,
    showStockSelectionButton: Boolean,
    onStockClick: () -> Unit,
    textStyle: TextStyle = type.displayTypeStyle.display2,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(measurement.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            bottom = measurement.spacing.medium,
        )
    ) {
        VaccinesText(
            textStyle = textStyle,
        )

        StockText(
            text = selectedStock.prettyName,
            textStyle = textStyle,
            modifier = Modifier.padding(end = 4.dp)
        )

        if (showStockSelectionButton) {
            ElevatedIconButton(
                onClick = onStockClick,
                iconDrawRes = R.drawable.ic_chevron_down
            )
        }
    }
}

@Composable
fun StockSelectorTwoLine(
    selectedStock: StockUi,
    showStockSelectionButton: Boolean,
    onStockClick: () -> Unit,
    textStyle: TextStyle = type.displayTypeStyle.display2,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            start = measurement.spacing.small,
            bottom = measurement.spacing.medium
        )
    ) {
        VaccinesText(
            textStyle = textStyle,
            Modifier.padding(end = measurement.spacing.small)
        )

        if (showStockSelectionButton) {
            ElevatedIconButton(
                onClick = onStockClick,
                iconDrawRes = R.drawable.ic_chevron_down
            )
        }
    }

    StockText(
        text = selectedStock.prettyName,
        textStyle = textStyle,
        modifier = Modifier.padding(
            start = measurement.spacing.small,
            bottom = 48.dp
        )
    )
}

@Composable
fun VaccinesText(textStyle: TextStyle, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.vaccines),
        style = textStyle,
        modifier = modifier
    )
}

@Composable
fun StockText(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = LocalStock.current.colors.container,
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) {
        Text(
            text = text,
            style = textStyle,
            fontStyle = FontStyle.Italic,
            color = it,
            modifier = modifier
        )
    }
}
