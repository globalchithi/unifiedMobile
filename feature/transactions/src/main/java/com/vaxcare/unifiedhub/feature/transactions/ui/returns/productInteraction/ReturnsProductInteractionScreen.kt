package com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.data.extension.toShorthand
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.Icons
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.core.ui.component.ButtonConfig
import com.vaxcare.unifiedhub.core.ui.component.VCBasicDialog
import com.vaxcare.unifiedhub.core.ui.component.VCScaffold
import com.vaxcare.unifiedhub.core.ui.component.button.ElevatedButton
import com.vaxcare.unifiedhub.core.ui.component.button.ElevatedIconButton
import com.vaxcare.unifiedhub.core.ui.component.button.VCFloatingActionButton
import com.vaxcare.unifiedhub.core.ui.component.keypad.KeypadDialog
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.EditQuantityCell
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.ProductCell
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.ext.verticalFadingEdge
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.common.components.LandscapeScannerSection
import com.vaxcare.unifiedhub.feature.transactions.ui.common.components.PortraitScannerSection
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.ProductLotUi
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionDialog.DiscardChanges
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionDialog.Keypad
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionDialog.WrongProduct
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionEvent.NavigateBack
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionEvent.NavigateToLotSearch
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.CloseScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.ConfirmDiscardChanges
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.DeleteLot
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.DismissDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.GoForward
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.OpenKeypad
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.ScanLot
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.SearchLot
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.UndoDelete
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.UpdateLotCount
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason.ReturnReasonUi
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

private const val PRODUCT_COLUMN_WIDTH_PORTRAIT = 312
private const val QUANT_COLUMN_WIDTH_PORTRAIT = 312
private const val PRODUCT_COLUMN_WIDTH_LANDSCAPE = 312
private const val QUANT_COLUMN_WIDTH_LANDSCAPE = 392

@Composable
fun ReturnsProductInteractionScreen(
    stock: StockUi,
    onNavigateBack: () -> Unit,
    onLotSearchClick: (Int) -> Unit,
    onNextClick: () -> Unit,
    viewModel: ReturnsProductInteractionViewModel = hiltViewModel()
) {
    BaseMviScreen<ReturnsProductInteractionState, ReturnsProductInteractionEvent, ReturnsProductInteractionIntent>(
        viewModel = viewModel,
        onEvent = {
            when (it) {
                is NavigateToLotSearch -> onLotSearchClick(it.sourceId)

                NavigateBack -> onNavigateBack()

                ReturnsProductInteractionEvent.NextScreen -> onNextClick()
            }
        }
    ) { state, handleIntent ->
        val onCloseDialogClick = { handleIntent(DismissDialog) }
        when (state.activeDialog) {
            DiscardChanges -> {
                VCBasicDialog(
                    onDismissRequest = onCloseDialogClick,
                    text = stringResource(R.string.discard_changes_body),
                    title = stringResource(R.string.discard_changes_title),
                    primaryButtonConfig = ButtonConfig(
                        text = stringResource(DesignSystemR.string.yes),
                        onClick = {
                            handleIntent(ConfirmDiscardChanges)
                        }
                    ),
                    secondaryButtonConfig = ButtonConfig(
                        text = stringResource(DesignSystemR.string.no),
                        onClick = onCloseDialogClick
                    )
                )
            }

            is WrongProduct -> {
                VCBasicDialog(
                    onDismissRequest = onCloseDialogClick,
                    title = stringResource(R.string.invalid_scan_title),
                    text = state.activeDialog.errorMessage,
                    primaryButtonConfig = ButtonConfig(
                        text = stringResource(DesignSystemR.string.ok),
                        onClick = onCloseDialogClick
                    )
                )
            }

            is Keypad -> {
                var inputKey by remember { mutableStateOf("") }
                KeypadDialog(
                    dialogTitle = stringResource(R.string.enter_quantity),
                    input = inputKey,
                    onCloseClick = onCloseDialogClick,
                    onClearClick = { inputKey = "" },
                    onDigitClick = {
                        if (inputKey.length < 3) {
                            inputKey += it
                        }
                    },
                    onDeleteClick = { inputKey = inputKey.dropLast(1) },
                    onSubmit = {
                        handleIntent(
                            ReturnsProductInteractionIntent.SubmitKeypadInput(
                                lotNumber = state.activeDialog.lotName,
                                count = inputKey.toInt()
                            )
                        )
                    }
                )
            }
        }

        ProvideStock(stock) {
            ReturnProductInteractionContent(
                state = state,
                stock = stock,
                onNavigateBack = {
                    handleIntent(CloseScreen)
                },
                onSearchClick = {
                    handleIntent(SearchLot)
                },
                onManualEntryClick = {
                    handleIntent(OpenKeypad(it))
                },
                onConfirmInventoryClick = {
                    handleIntent(GoForward)
                },
                onCountChanged = { lotNumber, delta ->
                    handleIntent(UpdateLotCount(lotNumber, delta))
                },
                onDeleteClick = {
                    handleIntent(DeleteLot(it))
                },
                onUndoClick = {
                    handleIntent(UndoDelete(it))
                },
                onBarcodeScanned = {
                    handleIntent(ScanLot(it))
                }
            )
        }
    }
}

@Composable
fun ReturnProductInteractionContent(
    state: ReturnsProductInteractionState,
    stock: StockUi,
    onNavigateBack: () -> Unit,
    onSearchClick: () -> Unit,
    onManualEntryClick: (String) -> Unit,
    onConfirmInventoryClick: () -> Unit,
    onCountChanged: (String, Int) -> Unit,
    onDeleteClick: (String) -> Unit,
    onUndoClick: (String) -> Unit,
    onBarcodeScanned: (ParsedBarcode) -> Unit,
    modifier: Modifier = Modifier,
) {
    VCScaffold(
        modifier = modifier,
        topBar = {
            BaseTitleBar(
                modifier = Modifier,
                title = R.string.add_public_lot_interaction_title,
                buttonIcon = DesignSystemR.drawable.ic_chevron_left,
                onButtonClick = onNavigateBack
            )
        },
        fab = {
            VCFloatingActionButton(
                onClick = onConfirmInventoryClick,
                iconPainter = painterResource(DesignSystemR.drawable.ic_check),
                enabled = state.lots.isNotEmpty()
            )
        },
    ) {
        when (LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                ReturnProductInteractionLandscape(
                    lots = state.lots,
                    stock = stock,
                    reason = state.reason,
                    totalCount = state.total,
                    isScannerActive = state.isScannerActive,
                    error = state.error,
                    onSearchClick = onSearchClick,
                    onManualEntryClick = onManualEntryClick,
                    onCountChanged = onCountChanged,
                    onDeleteClick = onDeleteClick,
                    onUndoClick = onUndoClick,
                    onBarcodeScanned = onBarcodeScanned
                )
            }

            else -> {
                ReturnProductInteractionPortrait(
                    lots = state.lots,
                    stock = stock,
                    reason = state.reason,
                    totalCount = state.total,
                    isScannerActive = state.isScannerActive,
                    error = state.error,
                    onSearchClick = onSearchClick,
                    onManualEntryClick = onManualEntryClick,
                    onCountChanged = onCountChanged,
                    onDeleteClick = onDeleteClick,
                    onUndoClick = onUndoClick,
                    onBarcodeScanned = onBarcodeScanned
                )
            }
        }
    }
}

@Composable
private fun ReturnProductInteractionLandscape(
    lots: List<ProductLotUi>,
    stock: StockUi,
    reason: ReturnReasonUi?,
    totalCount: Int,
    isScannerActive: Boolean,
    error: ReturnsProductInteractionError?,
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit,
    onManualEntryClick: (String) -> Unit,
    onCountChanged: (String, Int) -> Unit,
    onDeleteClick: (String) -> Unit,
    onUndoClick: (String) -> Unit,
    onBarcodeScanned: (ParsedBarcode) -> Unit
) {
    Row(
        modifier
            .padding(top = measurement.spacing.small)
            .padding(horizontal = 40.dp)
    ) {
        Column(modifier = Modifier.width(784.dp)) {
            AnimatedVisibility(reason == ReturnReasonUi.EXPIRED) {
                ExpiringProductsText(
                    Modifier
                        .padding(bottom = measurement.spacing.small)
                        .padding(start = 24.dp)
                )
            }

            LotSheet(
                isLandscape = true,
                lots = lots,
                modifier = Modifier.weight(1f),
                onQuantityClick = onManualEntryClick,
                onCountChanged = onCountChanged,
                onDeleteClick = onDeleteClick,
                onUndoClick = onUndoClick
            )

            ReturnSummaryFooter(
                isLandscape = true,
                reason = reason,
                totalCount = totalCount,
                stock = stock,
            )
        }

        LandscapeScannerSection(
            headerText = stringResource(R.string.lot_interaction_scan_each_product_lot),
            scannerActive = isScannerActive,
            invalidScan = error == ReturnsProductInteractionError.BadBarcodeScan,
            onBarcodeScanned = onBarcodeScanned,
            onLotSearchClick = onSearchClick,
            modifier = Modifier.padding(start = measurement.spacing.small)
        )
    }
}

@Composable
private fun ReturnProductInteractionPortrait(
    lots: List<ProductLotUi>,
    stock: StockUi,
    reason: ReturnReasonUi?,
    totalCount: Int,
    isScannerActive: Boolean,
    error: ReturnsProductInteractionError?,
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit,
    onManualEntryClick: (String) -> Unit,
    onCountChanged: (String, Int) -> Unit,
    onDeleteClick: (String) -> Unit,
    onUndoClick: (String) -> Unit,
    onBarcodeScanned: (ParsedBarcode) -> Unit,
) {
    Column(modifier) {
        PortraitScannerSection(
            headerText = stringResource(R.string.lot_interaction_scan_each_product_lot),
            scannerActive = isScannerActive,
            onBarcodeScanned = onBarcodeScanned,
            onLotSearchClick = onSearchClick,
            invalidScan = error == ReturnsProductInteractionError.BadBarcodeScan,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = measurement.spacing.small)
        )

        AnimatedVisibility(reason == ReturnReasonUi.EXPIRED) {
            ExpiringProductsText(
                Modifier
                    .padding(vertical = measurement.spacing.small)
                    .padding(start = 64.dp)
            )
        }

        Column(Modifier.padding(horizontal = measurement.spacing.small)) {
            LotSheet(
                isLandscape = false,
                lots = lots,
                modifier = Modifier.weight(1f),
                onQuantityClick = onManualEntryClick,
                onCountChanged = onCountChanged,
                onDeleteClick = onDeleteClick,
                onUndoClick = onUndoClick
            )

            ReturnSummaryFooter(
                stock = stock,
                isLandscape = false,
                reason = reason,
                totalCount = totalCount
            )
        }
    }
}

@Composable
fun ExpiringProductsText(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.rt_product_expiring_products),
        style = type.bodyTypeStyle.body4,
        modifier = modifier
    )
}

@Composable
fun LotSheet(
    isLandscape: Boolean,
    lots: List<ProductLotUi>,
    modifier: Modifier = Modifier,
    onQuantityClick: (String) -> Unit,
    onCountChanged: (String, Int) -> Unit,
    onDeleteClick: (String) -> Unit,
    onUndoClick: (String) -> Unit,
) {
    Surface(
        color = color.container.primaryContainer,
        shape = RoundedCornerShape(measurement.radius.cardMedium),
        modifier = modifier
    ) {
        Column {
            LotSheetHeader(isLandscape = isLandscape)

            val lazyListState = rememberLazyListState()
            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(bottom = 88.dp),
                modifier = Modifier
                    .testTag(TestTags.Returns.ProductInteraction.PRODUCT_SHEET_CONTAINER)
                    .fillMaxHeight()
                    .verticalFadingEdge(lazyListState, 32.dp)
            ) {
                items(
                    items = lots,
                    key = { item -> item.lotNumber }
                ) { lot ->

                    Box(
                        contentAlignment = Alignment.BottomStart,
                        modifier = Modifier.animateItem()
                    ) {
                        LotSheetItem(
                            item = lot,
                            isLandscape = isLandscape,
                            onQuantityClick = {
                                onQuantityClick(lot.lotNumber)
                            },
                            onCountChanged = { delta ->
                                onCountChanged(lot.lotNumber, delta)
                            },
                            onDeleteClick = {
                                onDeleteClick(lot.lotNumber)
                            },
                            onUndoClick = {
                                onUndoClick(lot.lotNumber)
                            }
                        )
                        HorizontalDivider(
                            color = color.outline.twoHundred,
                            thickness = 2.dp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LotSheetHeader(isLandscape: Boolean, modifier: Modifier = Modifier) {
    val (lotWidth, quantityWidth) = if (isLandscape) {
        PRODUCT_COLUMN_WIDTH_LANDSCAPE to QUANT_COLUMN_WIDTH_LANDSCAPE
    } else {
        PRODUCT_COLUMN_WIDTH_PORTRAIT to QUANT_COLUMN_WIDTH_PORTRAIT
    }
    Box(contentAlignment = Alignment.BottomStart) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .padding(top = 8.dp)
                .height(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(lotWidth.dp)
                    .padding(start = 48.dp)
            ) {
                Text(
                    text = stringResource(DesignSystemR.string.product).uppercase(),
                    style = type.bodyTypeStyle.body6Bold
                )
            }
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.width(quantityWidth.dp),
            ) {
                Text(
                    text = stringResource(DesignSystemR.string.quantity).uppercase(),
                    style = type.bodyTypeStyle.body6Bold
                )
            }
        }
        HorizontalDivider(
            color = color.outline.twoHundred,
            thickness = 2.dp
        )
    }
}

@Composable
private fun LotSheetItem(
    item: ProductLotUi,
    isLandscape: Boolean,
    onQuantityClick: () -> Unit,
    onCountChanged: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    onUndoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (productWidth, quantityWidth) = if (isLandscape) {
        PRODUCT_COLUMN_WIDTH_LANDSCAPE to QUANT_COLUMN_WIDTH_LANDSCAPE
    } else {
        PRODUCT_COLUMN_WIDTH_PORTRAIT to QUANT_COLUMN_WIDTH_PORTRAIT
    }

    AnimatedContent(
        targetState = item.isDeleted,
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { isDeleted ->

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .testTag(TestTags.Returns.ProductInteraction.productSheetRow(item.lotNumber))
                .fillMaxWidth()
                .height(112.dp)
                .background(color.container.primaryContainer)
        ) {
            with(item) {
                ProductCell(
                    titleRow = {
                        ProductTitleLine(
                            leadingIcon = Icons.presentationIcon(presentation),
                            title = productInfoText(antigen, prettyName)
                        )
                    },
                    bottomContent = {
                        Column {
                            ProductCellText(
                                text = getLotInfo(),
                                modifier = Modifier.padding(bottom = measurement.spacing.xSmall)
                            )

                            LotExpirationText(
                                isExpired = isExpired,
                                expiration = expiration
                            )
                        }
                    },
                    strikeText = isDeleted,
                    modifier = Modifier
                        .width(productWidth.dp)
                        .padding(start = measurement.spacing.small)
                )
            }

            if (!isDeleted) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.width(quantityWidth.dp)
                ) {
                    EditQuantityCell(
                        quantity = item.quantity,
                        onDecrementClick = { onCountChanged(-1) },
                        onDecrementLongClick = { onCountChanged(-5) },
                        onIncrementClick = { onCountChanged(1) },
                        onIncrementLongClick = { onCountChanged(5) },
                        onInputNumberClick = onQuantityClick,
                        decrementEnabled = item.quantity > 1,
                    )
                }
            }

            if (isDeleted) {
                ElevatedButton(
                    onClick = onUndoClick,
                    text = "Undo",
                    modifier = Modifier
                        .testTag(TestTags.ProductSheet.UNDO_BTN)
                        .padding(end = measurement.spacing.small),
                )
            } else {
                ElevatedIconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .testTag(TestTags.ProductSheet.DELETE_BTN)
                        .padding(horizontal = measurement.spacing.small)
                ) {
                    Icon(
                        painter = painterResource(DesignSystemR.drawable.ic_delete),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReturnSummaryFooter(
    isLandscape: Boolean,
    reason: ReturnReasonUi?,
    totalCount: Int,
    stock: StockUi,
    modifier: Modifier = Modifier,
) {
    val bottomPadding = with(measurement.spacing) {
        if (isLandscape) small else large
    }
    Surface(
        color = color.container.primaryContainer,
        shape = RoundedCornerShape(measurement.radius.cardMedium),
        modifier = modifier
            .testTag(TestTags.Returns.ProductInteraction.FOOTER_CONTAINER)
            .padding(
                top = measurement.spacing.xSmall,
                bottom = bottomPadding
            ).height(104.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = measurement.spacing.large)
                .padding(vertical = measurement.spacing.small)
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.reason),
                    style = type.bodyTypeStyle.label,
                    modifier = Modifier.padding(bottom = measurement.spacing.xSmall)
                )

                Text(
                    text = reason?.getFullText(stock) ?: "",
                    style = type.bodyTypeStyle.body3,
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(end = 184.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.total),
                        style = type.bodyTypeStyle.label,
                        modifier = Modifier.padding(bottom = measurement.spacing.xSmall)
                    )

                    AnimatedContent(
                        targetState = totalCount,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInVertically { -it } togetherWith slideOutVertically { it }
                            } else {
                                slideInVertically { it } togetherWith slideOutVertically { -it }
                            }
                        }
                    ) { total ->
                        Text(
                            text = total.toShorthand(),
                            style = type.headerTypeStyle.headlineMediumBold
                        )
                    }
                }
            }
        }
    }
}

@FullDevicePreview
@Composable
private fun Default() {
    ReturnProductInteractionContent(
        state = ReturnsProductInteractionSampleData.Default,
        stock = StockUi.VFC,
        onNavigateBack = {},
        onSearchClick = {},
        onManualEntryClick = {},
        onConfirmInventoryClick = {},
        onCountChanged = { _, _ -> },
        onDeleteClick = {},
        onUndoClick = {},
        onBarcodeScanned = {}
    )
}
