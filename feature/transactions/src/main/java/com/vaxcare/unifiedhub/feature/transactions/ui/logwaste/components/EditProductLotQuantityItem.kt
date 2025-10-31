package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.ui.component.button.ElevatedButton
import com.vaxcare.unifiedhub.core.ui.component.button.ElevatedIconButton
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.EditQuantityCell
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.EditQuantityUi
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.ProductInfo
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.ProductInfoUi
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.SubtitleLine
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
fun EditProductLotQuantityItem(editProductLotQuantityUi: EditProductLotQuantityUi, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(
                    vertical = measurement.spacing.small
                ).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (editProductLotQuantityUi.productInfoUi.isDeleted) {
                Arrangement.SpaceBetween
            } else {
                Arrangement.Start
            }
        ) {
            ProductInfo(
                info = editProductLotQuantityUi.productInfoUi,
                modifier = Modifier
                    .width(312.dp)
                    .padding(horizontal = measurement.spacing.small)
            )
            if (!editProductLotQuantityUi.productInfoUi.isDeleted) {
                EditQuantityCell(
                    editProductLotQuantityUi.editQuantityUi,
                    modifier = Modifier
                        .widthIn(min = 312.dp, max = 392.dp)
                )
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    ElevatedIconButton(
                        onClick = editProductLotQuantityUi.onDeleteClick,
                        iconDrawRes = DesignSystemR.drawable.ic_delete,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    )
                }
            } else {
                ElevatedButton(
                    onClick = editProductLotQuantityUi.onUndoClick,
                    text = stringResource(R.string.undo),
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                )
            }
        }
        HorizontalDivider(thickness = 2.dp, color = VaxCareTheme.color.outline.threeHundred)
    }
}

@Deprecated("Per developer consensus, use of the 'intermediate UI data class' pattern is no longer supported.")
@Immutable
data class EditProductLotQuantityUi(
    val productInfoUi: ProductInfoUi,
    val editQuantityUi: EditQuantityUi,
    val onDeleteClick: () -> Unit,
    val onUndoClick: () -> Unit
)

@Preview(showBackground = true, widthDp = 1000)
@Composable
private fun PreviewEditProductLotQuantityItem() {
    LazyColumn {
        items(2) {
            EditProductLotQuantityItem(
                editProductLotQuantityUi = EditProductLotQuantityUi(
                    productInfoUi = ProductInfoUi(
                        presentation = Presentation.PREFILLED_SYRINGE,
                        mainTextBold = "IPV",
                        mainTextRegular = "IPOL",
                        subtitleLines = listOf(
                            SubtitleLine(
                                text = "LOT# GH839082A"
                            )
                        ),
                        isDeleted = false
                    ),
                    editQuantityUi = EditQuantityUi(
                        quantity = 5809,
                        onDecrementClick = {},
                        onIncrementClick = {},
                        onInputNumberClick = {},
                        decrementEnabled = true,
                        incrementEnabled = true,
                        enabled = true,
                        onDecrementLongClick = {},
                        onIncrementLongClick = {}
                    ),
                    onDeleteClick = { },
                    onUndoClick = { }
                )
            )
        }
        item {
            EditProductLotQuantityItem(
                editProductLotQuantityUi = EditProductLotQuantityUi(
                    productInfoUi = ProductInfoUi(
                        presentation = Presentation.SINGLE_DOSE_VIAL,
                        mainTextBold = "IPV",
                        mainTextRegular = "IPOL",
                        subtitleLines = listOf(
                            SubtitleLine(
                                text = "LOT# GH839082A"
                            )
                        ),
                        isDeleted = true
                    ),
                    editQuantityUi = EditQuantityUi(
                        quantity = 5809,
                        onDecrementClick = {},
                        onIncrementClick = {},
                        onInputNumberClick = {},
                        decrementEnabled = true,
                        incrementEnabled = true,
                        enabled = true,
                        onDecrementLongClick = {},
                        onIncrementLongClick = {}
                    ),
                    onDeleteClick = { },
                    onUndoClick = { }
                )
            )
        }
    }
}
