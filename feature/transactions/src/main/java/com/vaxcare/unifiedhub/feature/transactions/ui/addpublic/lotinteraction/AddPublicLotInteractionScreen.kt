package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.lotinteraction

import android.content.res.Configuration
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.data.extension.toShorthand
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.ui.Icons
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
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.AddedLotInventoryUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUi
import com.vaxcare.unifiedhub.feature.transactions.ui.common.components.LandscapeScannerSection
import com.vaxcare.unifiedhub.feature.transactions.ui.common.components.PortraitScannerSection
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode
import kotlinx.coroutines.delay
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

private const val LOT_COLUMN_WIDTH_PORTRAIT = 312
private const val QUANT_COLUMN_WIDTH_PORTRAIT = 312
private const val LOT_COLUMN_WIDTH_LANDSCAPE = 312
private const val QUANT_COLUMN_WIDTH_LANDSCAPE = 392

@Composable
fun AddPublicLotInteractionScreen(
    onNavigateBack: () -> Unit,
    onLotSearchClick: (Int?, Int) -> Unit,
    viewModel: AddPublicLotInteractionViewModel = hiltViewModel(),
) {
    BaseMviScreen<AddPublicLotInteractionState, AddPublicLotInteractionEvent, AddPublicLotInteractionIntent>(
        viewModel = viewModel,
        onEvent = {
            when (it) {
                is AddPublicLotInteractionEvent.NavigateToLotSearch -> onLotSearchClick(
                    it.filterProductId,
                    it.sourceId
                )

                AddPublicLotInteractionEvent.NavigateBack -> onNavigateBack()
            }
        }
    ) { state, handleIntent ->
        val onCloseDialogClick = { handleIntent(AddPublicLotInteractionIntent.DismissDialog) }
        when (state.activeDialog) {
            AddPublicLotInteractionDialog.DiscardChanges -> {
                VCBasicDialog(
                    onDismissRequest = onCloseDialogClick,
                    text = stringResource(R.string.discard_changes_body),
                    title = stringResource(R.string.discard_changes_title),
                    primaryButtonConfig = ButtonConfig(
                        text = stringResource(DesignSystemR.string.yes),
                        onClick = {
                            handleIntent(AddPublicLotInteractionIntent.DiscardChanges)
                        }
                    ),
                    secondaryButtonConfig = ButtonConfig(
                        text = stringResource(DesignSystemR.string.no),
                        onClick = onCloseDialogClick
                    )
                )
            }

            AddPublicLotInteractionDialog.ExpiredDose -> {
                VCBasicDialog(
                    onDismissRequest = onCloseDialogClick,
                    title = stringResource(R.string.expired_product_title),
                    text = stringResource(R.string.expired_product_body),
                    primaryButtonConfig = ButtonConfig(
                        text = stringResource(DesignSystemR.string.ok),
                        onClick = onCloseDialogClick
                    )
                )
            }

            AddPublicLotInteractionDialog.MismatchedProduct -> {
                VCBasicDialog(
                    onDismissRequest = onCloseDialogClick,
                    title = stringResource(R.string.mismatched_product_title),
                    text = stringResource(R.string.mismatched_product_body),
                    primaryButtonConfig = ButtonConfig(
                        text = stringResource(DesignSystemR.string.ok),
                        onClick = onCloseDialogClick
                    )
                )
            }

            is AddPublicLotInteractionDialog.WrongProduct -> {
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

            is AddPublicLotInteractionDialog.Keypad -> {
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
                            AddPublicLotInteractionIntent.SubmitKeypadInput(
                                lotNumber = state.activeDialog.lotName,
                                count = inputKey.toInt()
                            )
                        )
                    }
                )
            }
        }

        ProvideStock(state.stockType) {
            AddPublicLotInteractionContent(
                state = state,
                onNavigateBack = {
                    handleIntent(AddPublicLotInteractionIntent.CloseScreen)
                },
                onSearchClick = {
                    handleIntent(AddPublicLotInteractionIntent.SearchLot)
                },
                onManualEntryClick = {
                    handleIntent(AddPublicLotInteractionIntent.OpenKeypad(it))
                },
                onConfirmInventoryClick = {
                    handleIntent(AddPublicLotInteractionIntent.Confirm)
                },
                onCountChanged = { lotNumber, delta ->
                    handleIntent(AddPublicLotInteractionIntent.UpdateLotCount(lotNumber, delta))
                },
                onDeleteClick = {
                    handleIntent(AddPublicLotInteractionIntent.DeleteLot(it))
                },
                onUndoClick = {
                    handleIntent(AddPublicLotInteractionIntent.UndoDelete(it))
                },
                onBarcodeScanned = {
                    handleIntent(AddPublicLotInteractionIntent.ScanLot(it))
                }
            )
        }
    }
}

@Composable
fun AddPublicLotInteractionContent(
    state: AddPublicLotInteractionState,
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
            )
        },
    ) {
        if (state.product == null) return@VCScaffold
        when (LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                AddPublicLotInteractionLandscape(
                    product = state.product,
                    highlightedLot = state.highlightedLot,
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
                AddPublicLotInteractionPortrait(
                    product = state.product,
                    highlightedLot = state.highlightedLot,
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
private fun AddPublicLotInteractionLandscape(
    product: ProductUi,
    highlightedLot: String?,
    isScannerActive: Boolean,
    error: AddPublicLotInteractionError?,
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
            LotInventorySheet(
                isLandscape = true,
                inventory = product.inventory,
                presentation = product.presentation,
                highlightedLot = highlightedLot,
                modifier = Modifier.weight(1f),
                onQuantityClick = onManualEntryClick,
                onCountChanged = onCountChanged,
                onDeleteClick = onDeleteClick,
                onUndoClick = onUndoClick
            )

            ProductSummaryFooter(
                antigen = product.antigen,
                prettyName = product.prettyName,
                cartonCount = product.cartonCount,
                totalCount = product.getTotal()
            )
        }

        LandscapeScannerSection(
            headerText = stringResource(R.string.lot_interaction_scan_each_product_lot),
            scannerActive = isScannerActive,
            invalidScan = error == AddPublicLotInteractionError.BadBarcodeScan,
            onBarcodeScanned = onBarcodeScanned,
            onLotSearchClick = onSearchClick,
            modifier = Modifier.padding(start = measurement.spacing.small)
        )
    }
}

@Composable
private fun AddPublicLotInteractionPortrait(
    product: ProductUi,
    highlightedLot: String?,
    isScannerActive: Boolean,
    error: AddPublicLotInteractionError?,
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit,
    onManualEntryClick: (String) -> Unit,
    onCountChanged: (String, Int) -> Unit,
    onDeleteClick: (String) -> Unit,
    onUndoClick: (String) -> Unit,
    onBarcodeScanned: (ParsedBarcode) -> Unit,
) {
    Column(modifier.width(732.dp)) {
        PortraitScannerSection(
            headerText = stringResource(R.string.lot_interaction_scan_each_product_lot),
            scannerActive = isScannerActive,
            onBarcodeScanned = onBarcodeScanned,
            onLotSearchClick = onSearchClick,
            invalidScan = error == AddPublicLotInteractionError.BadBarcodeScan,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = measurement.spacing.small,
                    bottom = measurement.spacing.large
                )
        )

        Column(
            modifier = Modifier
                .width(732.dp)
                .padding(horizontal = measurement.spacing.small)
        ) {
            LotInventorySheet(
                isLandscape = false,
                inventory = product.inventory,
                presentation = product.presentation,
                highlightedLot = highlightedLot,
                modifier = Modifier.weight(1f),
                onQuantityClick = onManualEntryClick,
                onCountChanged = onCountChanged,
                onDeleteClick = onDeleteClick,
                onUndoClick = onUndoClick
            )

            ProductSummaryFooter(
                antigen = product.antigen,
                prettyName = product.prettyName,
                cartonCount = product.cartonCount,
                totalCount = product.getTotal()
            )
        }
    }
}

@Composable
fun LotInventorySheet(
    isLandscape: Boolean,
    inventory: List<AddedLotInventoryUi>,
    presentation: Presentation,
    highlightedLot: String?,
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
        val selectedFlash = remember { Animatable(Color.Transparent) }
        val flashColor = color.container.secondaryContainer.copy(alpha = 0.3f)
        val lazyListState = rememberLazyListState()

        LaunchedEffect(highlightedLot) {
            inventory
                .indexOfFirst {
                    it.lotNumber == highlightedLot
                }.let {
                    if (it == -1) return@LaunchedEffect
                    lazyListState.animateScrollToItem(it)
                }

            // color flash
            selectedFlash.animateTo(flashColor)
            delay(300)
            selectedFlash.animateTo(Color.Transparent)
        }

        Column {
            LotSheetHeader(isLandscape = isLandscape)

            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(bottom = 88.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalFadingEdge(lazyListState = lazyListState, 32.dp)
            ) {
                items(
                    items = inventory,
                    key = { item -> item.lotNumber }
                ) { lot ->

                    val overlayColor = if (lot.lotNumber == highlightedLot) {
                        flashColor
                    } else {
                        Color.Transparent
                    }

                    Box(modifier = Modifier.background(overlayColor)) {
                        LotSheetItem(
                            item = lot,
                            isLandscape = isLandscape,
                            onQuantityClick = {
                                onQuantityClick(lot.lotNumber)
                            },
                            presentation = presentation,
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
                        HorizontalDivider(thickness = 1.dp, color = color.outline.threeHundred)
                    }
                }
            }
        }
    }
}

@Composable
private fun LotSheetHeader(isLandscape: Boolean, modifier: Modifier = Modifier) {
    val (lotWidth, quantityWidth) = if (isLandscape) {
        LOT_COLUMN_WIDTH_LANDSCAPE to QUANT_COLUMN_WIDTH_LANDSCAPE
    } else {
        LOT_COLUMN_WIDTH_PORTRAIT to QUANT_COLUMN_WIDTH_PORTRAIT
    }
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
                text = stringResource(R.string.lot_number).uppercase(),
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
}

@Composable
private fun LotSheetItem(
    item: AddedLotInventoryUi,
    isLandscape: Boolean,
    presentation: Presentation,
    onQuantityClick: () -> Unit,
    onCountChanged: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    onUndoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (lotWidth, quantityWidth) = if (isLandscape) {
        LOT_COLUMN_WIDTH_LANDSCAPE to QUANT_COLUMN_WIDTH_LANDSCAPE
    } else {
        LOT_COLUMN_WIDTH_PORTRAIT to QUANT_COLUMN_WIDTH_PORTRAIT
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(color.container.primaryContainer)
    ) {
        ProductCell(
            titleRow = {
                ProductTitleLine(
                    leadingIcon = Icons.presentationIcon(presentation),
                    title = item.lotNumber,
                )
            },
            bottomContent = {
                LotExpirationText(
                    isExpired = item.isExpired,
                    expiration = item.expiration
                )
            },
            strikeText = item.isDeleted,
            modifier = Modifier
                .width(lotWidth.dp)
                .padding(start = 16.dp)
        )

        if (!item.isDeleted) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.width(quantityWidth.dp)
            ) {
                EditQuantityCell(
                    quantity = item.count,
                    onDecrementClick = { onCountChanged(-1) },
                    onDecrementLongClick = { onCountChanged(-5) },
                    onIncrementClick = { onCountChanged(1) },
                    onIncrementLongClick = { onCountChanged(5) },
                    onInputNumberClick = onQuantityClick,
                    decrementEnabled = item.count > 1,
                )
            }
        }

        if (item.isDeleted) {
            ElevatedButton(
                onClick = onUndoClick,
                text = "Undo",
                modifier = Modifier.padding(end = measurement.spacing.small),
            )
        } else {
            ElevatedIconButton(
                onClick = onDeleteClick,
                modifier = Modifier.padding(horizontal = measurement.spacing.small)
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

@Composable
private fun ProductSummaryFooter(
    antigen: String,
    prettyName: String,
    cartonCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = color.container.primaryContainer,
        shape = RoundedCornerShape(measurement.radius.cardMedium),
        modifier = modifier.padding(
            top = measurement.spacing.xSmall,
            bottom = measurement.spacing.large
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = measurement.spacing.large)
                .padding(vertical = measurement.spacing.small)
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = antigen,
                    style = type.bodyTypeStyle.body3Bold
                )
                Text(
                    text = prettyName,
                    style = type.bodyTypeStyle.body3,
                    modifier = Modifier.padding(bottom = measurement.spacing.xSmall)
                )
                Text(
                    text = stringResource(
                        R.string.lot_interaction_per_carton_fmt,
                        cartonCount
                    ),
                    style = type.bodyTypeStyle.body5Italic
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
private fun AddPublicLotInteractionPreview() {
    AddPublicLotInteractionContent(
        state = AddPublicLotInteractionSampleData.Default.copy(
            highlightedLot = "TLSK498",
            error = AddPublicLotInteractionError.BadBarcodeScan
        ),
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
