package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.R
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.EditQuantityUi
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.ProductInfoUi
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.SubtitleLine
import com.vaxcare.unifiedhub.core.ui.ext.verticalFadingEdge

@Composable
fun ProductsSection(listOfEditProductLotQuantityUi: List<EditProductLotQuantityUi>, modifier: Modifier = Modifier) {
    val lazyListState = rememberLazyListState()
    Surface(
        shape = RoundedCornerShape(measurement.radius.cardMedium),
        color = color.container.primaryContainer,
        modifier = modifier
    ) {
        Column {
            Row(
                Modifier.height(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.product).uppercase(),
                    style = type.bodyTypeStyle.body6Bold,
                    modifier = Modifier
                        .padding(
                            vertical = measurement.spacing.xSmall,
                            horizontal = 48.dp
                        ).width(216.dp)
                )

                Text(
                    stringResource(R.string.quantity).uppercase(),
                    textAlign = TextAlign.Center,
                    style = type.bodyTypeStyle.body6Bold,
                    modifier = Modifier
                        .padding(
                            vertical = measurement.spacing.xSmall,
                            horizontal = measurement.spacing.small
                        ).width(280.dp),
                )
            }
            HorizontalDivider(thickness = 2.dp, color = color.outline.threeHundred)
            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(bottom = 88.dp),
                modifier = Modifier
                    .verticalFadingEdge(lazyListState = lazyListState, 32.dp)
            ) {
                items(count = listOfEditProductLotQuantityUi.size, key = { index ->
                    // the text that includes the lot number (should be unique)
                    listOfEditProductLotQuantityUi[index]
                        .productInfoUi.subtitleLines
                        .first()
                        .text
                }) { index ->
                    EditProductLotQuantityItem(listOfEditProductLotQuantityUi[index])
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 704, heightDp = 736)
@Composable
private fun PreviewProductSection() {
    VaxCareTheme {
        ProductsSection(
            listOfEditProductLotQuantityUi = listOf(
                EditProductLotQuantityUi(
                    productInfoUi = ProductInfoUi(
                        presentation = Presentation.SINGLE_DOSE_VIAL,
                        mainTextBold = "IPV",
                        mainTextRegular = "IPOL",
                        subtitleLines = listOf(
                            SubtitleLine(text = "LOT# GH839082A")
                        ),
                        isDeleted = false
                    ),
                    editQuantityUi = EditQuantityUi(
                        quantity = 2552,
                        onDecrementClick = {},
                        onIncrementClick = {},
                        onInputNumberClick = {},
                        decrementEnabled = true,
                        incrementEnabled = true,
                        enabled = true,
                        onDecrementLongClick = {},
                        onIncrementLongClick = {},
                    ),
                    onDeleteClick = {},
                    onUndoClick = {}
                ),
                EditProductLotQuantityUi(
                    productInfoUi = ProductInfoUi(
                        presentation = Presentation.SINGLE_DOSE_VIAL,
                        mainTextBold = "Hep A",
                        mainTextRegular = "Havrix",
                        subtitleLines = listOf(
                            SubtitleLine(text = "LOT# MNYNG93")
                        ),
                        isDeleted = false
                    ),
                    editQuantityUi = EditQuantityUi(
                        quantity = 1,
                        onDecrementClick = {},
                        onIncrementClick = {},
                        onInputNumberClick = {},
                        decrementEnabled = false,
                        incrementEnabled = true,
                        enabled = true,
                        onDecrementLongClick = {},
                        onIncrementLongClick = {},
                    ),
                    onDeleteClick = {},
                    onUndoClick = {}
                ),
                EditProductLotQuantityUi(
                    productInfoUi = ProductInfoUi(
                        presentation = Presentation.SINGLE_DOSE_VIAL,
                        mainTextBold = "Hep A",
                        mainTextRegular = "Havrix",
                        subtitleLines = listOf(
                            SubtitleLine(text = "LOT# MNYNG393")
                        ),
                        isDeleted = true
                    ),
                    editQuantityUi = EditQuantityUi(
                        quantity = 1,
                        onDecrementClick = {},
                        onIncrementClick = {},
                        onInputNumberClick = {},
                        decrementEnabled = false,
                        incrementEnabled = false,
                        enabled = false,
                        onDecrementLongClick = {},
                        onIncrementLongClick = {},
                    ),
                    onDeleteClick = {},
                    onUndoClick = {}
                )
            )
        )
    }
}
