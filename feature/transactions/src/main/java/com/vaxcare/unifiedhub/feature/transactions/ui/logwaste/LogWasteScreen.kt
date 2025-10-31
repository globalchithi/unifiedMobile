package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.core.ui.component.ButtonConfig
import com.vaxcare.unifiedhub.core.ui.component.VCBasicDialog
import com.vaxcare.unifiedhub.core.ui.component.VCScaffold
import com.vaxcare.unifiedhub.core.ui.component.button.VCFloatingActionButton
import com.vaxcare.unifiedhub.core.ui.component.keypad.KeypadDialog
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.EditQuantityUi
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.ProductInfoUi
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.SubtitleLine
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.common.components.LandscapeScannerSection
import com.vaxcare.unifiedhub.feature.transactions.ui.common.components.PortraitScannerSection
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.LogWasteIntent.BarcodeScanned
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.LogWasteIntent.LotQuantityEntered
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.LogWasteIntent.SearchLot
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.components.EditProductLotQuantityUi
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.components.ProductsSection
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.components.ReasonAndTotalFooter
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
fun LogWasteScreen(
    navigateToSummary: () -> Unit,
    navigateSearchLot: () -> Unit,
    navigateBack: () -> Unit,
    viewModel: LogWasteViewModel = hiltViewModel()
) {
    BaseMviScreen<LogWasteState, LogWasteEvent, LogWasteIntent>(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                LogWasteEvent.GoBack -> navigateBack()
                LogWasteEvent.GoToLotSearch -> navigateSearchLot()
                LogWasteEvent.GoToSummary -> navigateToSummary()
            }
        }
    ) { state, sendIntent ->
        ProvideStock(state.stockUi) {
            LogWasteContent(state, sendIntent)
        }

        when (state.activeDialog) {
            LogWasteDialog.DiscardChanges -> {
                VCBasicDialog(
                    onDismissRequest = { sendIntent(LogWasteIntent.CloseDialog) },
                    text = stringResource(R.string.discard_changes_body),
                    title = stringResource(R.string.discard_changes_title),
                    primaryButtonConfig = ButtonConfig(
                        text = stringResource(DesignSystemR.string.yes),
                        onClick = { sendIntent(LogWasteIntent.DiscardChangesConfirmed) }
                    ),
                    secondaryButtonConfig = ButtonConfig(
                        text = stringResource(DesignSystemR.string.no),
                        onClick = { sendIntent(LogWasteIntent.CloseDialog) }
                    )
                )
            }

            is LogWasteDialog.EnterQuantity -> {
                var inputKey by remember { mutableStateOf("") }
                KeypadDialog(
                    dialogTitle = stringResource(R.string.enter_quantity),
                    input = inputKey,
                    onCloseClick = { sendIntent(LogWasteIntent.CloseDialog) },
                    onClearClick = { inputKey = "" },
                    onDigitClick = {
                        if (inputKey.length < 8) {
                            inputKey += it
                        }
                    },
                    onDeleteClick = { inputKey = inputKey.dropLast(1) },
                    onSubmit = {
                        sendIntent(
                            LotQuantityEntered(
                                lotNumber = state.activeDialog.lotNumber,
                                quantity = inputKey.toInt()
                            )
                        )
                    }
                )
            }

            is LogWasteDialog.WrongProduct -> {
                VCBasicDialog(
                    onDismissRequest = { sendIntent(LogWasteIntent.CloseDialog) },
                    title = stringResource(R.string.invalid_scan_title),
                    text = state.activeDialog.errorMessage,
                    primaryButtonConfig = ButtonConfig(
                        text = stringResource(DesignSystemR.string.ok),
                        onClick = { sendIntent(LogWasteIntent.CloseDialog) }
                    )
                )
            }

            LogWasteDialog.ExpiredProduct -> {
                VCBasicDialog(
                    onDismissRequest = { sendIntent(LogWasteIntent.CloseDialog) },
                    title = stringResource(R.string.expired_product_title),
                    text = stringResource(R.string.expired_product_body),
                    primaryButtonConfig = ButtonConfig(
                        text = stringResource(DesignSystemR.string.ok),
                        onClick = { sendIntent(LogWasteIntent.CloseDialog) }
                    )
                )
            }

            null -> {
                // Show nothing
            }
        }
    }
}

@Composable
fun LogWasteContent(state: LogWasteState, sendIntent: (logWasteIntent: LogWasteIntent) -> Unit) {
    VCScaffold(
        topBar = {
            BaseTitleBar(
                title = stringResource(R.string.log_waste),
                buttonIcon = DesignSystemR.drawable.ic_arrow_back,
                onButtonClick = { sendIntent(LogWasteIntent.NavigateBackClicked) }
            )
        },
        fab = {
            VCFloatingActionButton(
                onClick = { sendIntent(LogWasteIntent.ConfirmWastedProducts) },
                iconPainter = painterResource(DesignSystemR.drawable.ic_arrow_forward),
                enabled = state.total > 0
            )
        }
    ) {
        when (LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> PortraitLogWasteBody(state, sendIntent)
            else -> LandscapeLogWasteBody(state, sendIntent)
        }
    }
}

@Composable
fun PortraitLogWasteBody(
    state: LogWasteState,
    sendIntent: (logWasteIntent: LogWasteIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Spacer(Modifier.height(measurement.spacing.small))
        PortraitScannerSection(
            headerText = stringResource(R.string.scan_each_product),
            scannerActive = !state.isLoading && state.activeDialog == null,
            onBarcodeScanned = { barcode -> sendIntent(BarcodeScanned(barcode)) },
            onLotSearchClick = { sendIntent(SearchLot) },
            invalidScan = state.isInvalidProductScanned
        )
        Spacer(Modifier.height(measurement.spacing.small))
        ProductsSection(
            listOfEditProductLotQuantityUi = state.wastedProductsUi,
            modifier = Modifier
                .padding(horizontal = 17.dp)
                .weight(1f)
        )
        Spacer(Modifier.height(measurement.spacing.xSmall))
        ReasonAndTotalFooter(
            reason = state.reason,
            stockName = state.stockUi.prettyName,
            total = state.total,
            modifier = Modifier
                .padding(horizontal = 17.dp)
                .fillMaxWidth()
        )
        Spacer(Modifier.height(measurement.spacing.large))
    }
}

@Composable
fun LandscapeLogWasteBody(
    state: LogWasteState,
    sendIntent: (logWasteIntent: LogWasteIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(
            start = measurement.spacing.large,
            end = measurement.spacing.small,
            top = measurement.spacing.small,
            bottom = 18.dp
        )
    ) {
        Column(modifier = Modifier.weight(0.70f)) {
            ProductsSection(
                listOfEditProductLotQuantityUi = state.wastedProductsUi,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.height(measurement.spacing.xSmall))
            ReasonAndTotalFooter(
                reason = state.reason,
                stockName = state.stockUi.prettyName,
                total = state.total,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.width(measurement.spacing.small))
        Column(modifier = Modifier.weight(0.30f)) {
            LandscapeScannerSection(
                headerText = stringResource(R.string.scan_each_product),
                scannerActive = !state.isLoading && state.activeDialog == null,
                onBarcodeScanned = { barcode -> sendIntent(BarcodeScanned(barcode)) },
                onLotSearchClick = { sendIntent(SearchLot) }
            )
        }
    }
}

private object LogWasteStatePreviewData {
    private fun createSampleUiItem(
        lotNumber: String,
        antigen: String,
        productName: String,
        amount: Int,
        isDeleted: Boolean = false
    ) = EditProductLotQuantityUi(
        productInfoUi = ProductInfoUi(
            presentation = Presentation.PREFILLED_SYRINGE,
            mainTextBold = antigen,
            mainTextRegular = productName,
            subtitleLines = listOf(
                SubtitleLine(text = "LOT# $lotNumber")
            ),
            isDeleted = isDeleted
        ),
        editQuantityUi = EditQuantityUi(
            amount,
            {},
            {},
            {},
            {},
            {},
            decrementEnabled = amount > 1,
            enabled = !isDeleted
        ),
        onDeleteClick = {},
        onUndoClick = {}
    )

    val sampleWastedProductsUi = listOf(
        createSampleUiItem("GH839082A", "IPV", "(IPOL)", 1),
        createSampleUiItem("MNYNG93", "Hep A", "(Havrix)", 2),
        createSampleUiItem("DFALKSD91", "DTap", "(Daptacel)", 5)
    )

    val emptyState = LogWasteState(
        isLoading = false,
        wastedProductsUi = emptyList(),
        total = 0,
        stockUi = StockUi.VFC,
        reason = LogWasteReason.PREPPED_AND_NOT_ADMINISTERED,
        activeDialog = null
    )

    val withItemsState = LogWasteState(
        isLoading = false,
        wastedProductsUi = sampleWastedProductsUi,
        stockUi = StockUi.THREE_SEVENTEEN,
        total = sampleWastedProductsUi.sumOf { it.editQuantityUi.quantity },
        reason = LogWasteReason.PREPPED_AND_NOT_ADMINISTERED,
        activeDialog = null
    )

    val withDeletedItemState = LogWasteState(
        isLoading = false,
        wastedProductsUi =
            sampleWastedProductsUi + createSampleUiItem("KMTHYNG95", "DTap", "(Daptacel)", 2, isDeleted = true),
        stockUi = StockUi.STATE,
        total = sampleWastedProductsUi.sumOf { it.editQuantityUi.quantity },
        reason = LogWasteReason.PREPPED_AND_NOT_ADMINISTERED,
        activeDialog = null
    )
}

@FullDevicePreview
@Composable
private fun PreviewLogWasteContent_Empty() {
    VaxCareTheme {
        LogWasteContent(
            state = LogWasteStatePreviewData.emptyState,
            sendIntent = {}
        )
    }
}

@FullDevicePreview
@Composable
private fun PreviewLogWasteContent_WithItems() {
    VaxCareTheme {
        LogWasteContent(
            state = LogWasteStatePreviewData.withItemsState,
            sendIntent = {}
        )
    }
}

@FullDevicePreview
@Composable
private fun PreviewLogWasteContent_WithDeletedItem() {
    VaxCareTheme {
        LogWasteContent(
            state = LogWasteStatePreviewData.withDeletedItemState,
            sendIntent = {}
        )
    }
}
