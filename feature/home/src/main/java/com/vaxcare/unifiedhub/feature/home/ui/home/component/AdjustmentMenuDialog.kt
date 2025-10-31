package com.vaxcare.unifiedhub.feature.home.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.component.MenuItemCard
import com.vaxcare.unifiedhub.core.ui.component.VCContentDialog
import com.vaxcare.unifiedhub.core.ui.compose.LocalStock
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.home.ui.home.model.AdjustmentListItemUi
import com.vaxcare.unifiedhub.core.designsystem.R as designR

@Composable
fun AdjustmentMenuDialog(
    items: List<AdjustmentListItemUi>,
    onDismiss: () -> Unit,
    onItemClick: (AdjustmentListItemUi) -> Unit
) {
    VCContentDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(measurement.radius.cardLarge),
        modifier = Modifier.width(408.dp)
    ) {
        Column(
            modifier = Modifier.padding(measurement.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(measurement.spacing.small)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    style = type.bodyTypeStyle.body1Bold.copy(fontSize = 22.sp),
                    text = "Adjust Inventory",
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            start = measurement.spacing.small,
                            bottom = 8.dp
                        ),
                )
                IconButton(onDismiss) {
                    Icon(
                        painter = painterResource(designR.drawable.ic_close),
                        contentDescription = "Close Dialog"
                    )
                }
            }

            items.forEach {
                MenuItemCard(
                    text = stringResource(it.titleResId),
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    LocalStock.current.colors.containerLight,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(it.iconResId),
                                contentDescription = "adjustmentIcon"
                            )
                        }
                    },
                    onClick = { onItemClick(it) },
                    containerColor = color.container.primaryContainer,
                    textStyle = type.bodyTypeStyle.body3Bold,
                    contentPadding = PaddingValues(measurement.spacing.large),
                )
            }
        }
    }
}

@FullDevicePreview
@Composable
private fun CenterMenuPreview() {
    VaxCareTheme {
        ProvideStock(StockUi.VFC) {
            AdjustmentMenuDialog(
                items = listOf(
                    AdjustmentListItemUi.Returns,
                    AdjustmentListItemUi.AddPublic,
                    AdjustmentListItemUi.Buyback,
                    AdjustmentListItemUi.LogWaste,
                    AdjustmentListItemUi.Transfer,
                ),
                onDismiss = {},
                onItemClick = {}
            )
        }
    }
}
