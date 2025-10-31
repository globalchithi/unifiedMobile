package com.vaxcare.unifiedhub.feature.home.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.component.MenuItemCard
import com.vaxcare.unifiedhub.core.ui.component.VCContentDialog
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.home.R
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
internal fun StockMenuDialog(
    onDismiss: () -> Unit,
    stockOptions: List<StockUi>,
    activeStock: StockUi,
    onStockSelected: (StockUi) -> Unit,
    modifier: Modifier = Modifier
) {
    VCContentDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(measurement.radius.cardLarge),
        modifier = modifier.width(408.dp)
    ) {
        Column(Modifier.padding(measurement.spacing.medium)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.select_inventory),
                    style = type.bodyTypeStyle.body4Bold.copy(
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.padding(start = measurement.spacing.small)
                )

                IconButton(onDismiss) {
                    Icon(
                        painter = painterResource(DesignSystemR.drawable.ic_close),
                        contentDescription = null,
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(measurement.spacing.small),
                modifier = Modifier.padding(top = measurement.spacing.medium)
            ) {
                val contentPadding = PaddingValues(
                    horizontal = measurement.spacing.large,
                    vertical = if (stockOptions.size > 3 &&
                        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                    ) {
                        measurement.spacing.medium
                    } else {
                        measurement.spacing.large
                    }
                )

                stockOptions.forEach { stock ->
                    val text = "${stringResource(DesignSystemR.string.vaccines)} ${stock.prettyName}"
                    if (stock == activeStock) {
                        MenuItemCard(
                            text = text,
                            icon = {
                                Image(
                                    painter = painterResource(stock.icon),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .background(
                                            color = stock.colors.containerLight,
                                            shape = CircleShape
                                        ).padding(4.dp)
                                )
                            },
                            contentPadding = contentPadding,
                            containerColor = stock.colors.containerLight,
                            shadowElevation = 0.dp
                        )
                    } else {
                        MenuItemCard(
                            text = text,
                            icon = {
                                Image(
                                    painter = painterResource(stock.icon),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .background(
                                            color = stock.colors.containerLight,
                                            shape = CircleShape
                                        ).padding(4.dp)
                                )
                            },
                            onClick = {
                                onStockSelected(stock)
                            },
                            contentPadding = contentPadding,
                            containerColor = color.container.primaryContainer,
                        )
                    }
                }
            }
        }
    }
}
