package com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction

import android.content.res.Configuration
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.model.product.Presentation
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
import com.vaxcare.unifiedhub.core.ui.compose.LocalStock
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.common.components.LandscapeScannerSection
import com.vaxcare.unifiedhub.feature.transactions.ui.common.components.PortraitScannerSection
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model.LotInventoryUi
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode
import kotlinx.coroutines.delay
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

private const val LOT_COLUMN_WIDTH_PORTRAIT = 312
private const val QUANT_COLUMN_WIDTH_PORTRAIT = 312
private const val LOT_COLUMN_WIDTH_LANDSCAPE = 312
private const val QUANT_COLUMN_WIDTH_LANDSCAPE = 392

@Composable
fun LotInteractionScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onLotSearchClick: (Int?, Int) -> Unit,
    viewModel: LotInteractionViewModel = hiltViewModel()
) {
    BaseMviScreen<LotInteractionState, LotInteractionEvent, LotInteractionIntent>(
        viewModel = viewModel,
        onEvent = {
            when (it) {
                is LotInteractionEvent.NavigateToLotSearch -> onLotSearchClick(
                    it.filterProductId,
                    it.sourceId
                )

                LotInteractionEvent.NavigateBack -> onNavigateBack()
            }
        }
    ) { state, handleIntent ->
        val onCloseDialogClick = { handleIntent(LotInteractionIntent.CloseCurrentDialog) }
        when (state.activeDialog) {
            LotInteractionDialog.SaveOrDiscardChanges -> {
                VCBasicDialog(
                    onDismissRequest = onCloseDialogClick,
                    text = stringResource(R.string.discard_changes_body),
                    title = stringResource(R.string.discard_changes_title),
                    primaryButtonConfig = ButtonConfig(
                        text = stringResource(DesignSystemR.string.yes),
                        testTag = TestTags.Counts.LotInteraction.DISCARD_CHANGES_DIALOG_CONFIRM_BUTTON,
                        onClick = { handleIntent(LotInteractionIntent.ConfirmDiscardChanges) },
                    ),
                    secondaryButtonConfig = ButtonConfig(
                        text = stringResource(DesignSystemR.string.no),
                        testTag = TestTags.Counts.LotInteraction.DISCARD_CHANGES_DIALOG_CANCEL_BUTTON,
                        onClick = onCloseDialogClick
                    )
                )
            }

            LotInteractionDialog.ExpiredProductScanned -> {
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

            LotInteractionDialog.MismatchedProduct -> {
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

            is LotInteractionDialog.WrongProductScanned -> {
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

            is LotInteractionDialog.NumPadEntry -> {
                var inputKey by remember { mutableStateOf("") }
                KeypadDialog(
                    dialogTitle = stringResource(R.string.enter_quantity),
                    input = inputKey,
                    onCloseClick = onCloseDialogClick,
                    onClearClick = { inputKey = "" },
                    onDigitClick = {
                        if (inputKey.length < 8) {
                            inputKey += it
                        }
                    },
                    onDeleteClick = { inputKey = inputKey.dropLast(1) },
                    onSubmit = {
                        handleIntent(
                            LotInteractionIntent.NumPadEntry(
                                lotNumber = state.activeDialog.lotName,
                                delta = inputKey.toInt()
                            )
                        )
                    }
                )
            }
        }

        ProvideStock(state.stockType) {
            LotInteractionContent(
                modifier = modifier,
                orientation = LocalConfiguration.current.orientation,
                state = state,
                onHighlightComplete = { handleIntent(LotInteractionIntent.HighlightComplete) },
                onNavigateBack = { handleIntent(LotInteractionIntent.CloseScreen) },
                onSearchLotClick = { handleIntent(LotInteractionIntent.SearchLot) },
                onShowDialogClick = { handleIntent(LotInteractionIntent.OpenNumPad(it.lotNumber)) },
                onConfirmInventoryClick = { handleIntent(LotInteractionIntent.ConfirmLotInventory) },
                onLotDelta = { item, delta ->
                    handleIntent(LotInteractionIntent.UpdateLotDelta(item, delta))
                },
                onDeleteClick = { lot, onHand, isCurrentlyDeleted ->
                    handleIntent(
                        LotInteractionIntent.ToggleDelete(
                            lotNumber = lot,
                            onHand = onHand,
                            isCurrentlyDeleted = isCurrentlyDeleted
                        )
                    )
                },
                onBarcodeScanned = {
                    handleIntent(LotInteractionIntent.ScanLot(it))
                }
            )
        }
    }
}

@Composable
fun LotInteractionContent(
    modifier: Modifier = Modifier,
    state: LotInteractionState,
    orientation: Int,
    onHighlightComplete: () -> Unit,
    onNavigateBack: () -> Unit,
    onSearchLotClick: () -> Unit,
    onShowDialogClick: (LotInventoryUi) -> Unit,
    onConfirmInventoryClick: () -> Unit,
    onLotDelta: (LotInventoryUi, Int) -> Unit,
    onDeleteClick: (String, Int, Boolean) -> Unit,
    onBarcodeScanned: (ParsedBarcode) -> Unit,
) {
    VCScaffold(
        modifier = modifier.testTag(TestTags.Counts.LotInteraction.CONTAINER),
        topBar = {
            BaseTitleBar(
                modifier = Modifier,
                title = R.string.lot_interaction_title,
                buttonIcon = DesignSystemR.drawable.ic_chevron_left,
                onButtonClick = onNavigateBack
            )
        },
        fab = {
            VCFloatingActionButton(
                modifier = Modifier.testTag(TestTags.Counts.LotInteraction.CONFIRM_BUTTON),
                onClick = onConfirmInventoryClick,
                iconPainter = painterResource(DesignSystemR.drawable.ic_check),
                enabled = !state.isActionRequired
            )
        },
    ) {
        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                LotInteractionLandscape(
                    state = state,
                    onHighlightComplete = onHighlightComplete,
                    onLotSearchClick = onSearchLotClick,
                    onShowDialogClick = onShowDialogClick,
                    onLotDelta = onLotDelta,
                    onDeleteClick = onDeleteClick,
                    onBarcodeScanned = onBarcodeScanned
                )
            }

            else -> {
                LotInteractionPortrait(
                    state = state,
                    onHighlightComplete = onHighlightComplete,
                    onLotSearchClick = onSearchLotClick,
                    onShowDialogClick = onShowDialogClick,
                    onLotDelta = onLotDelta,
                    onDeleteClick = onDeleteClick,
                    onBarcodeScanned = onBarcodeScanned
                )
            }
        }
    }
}

@Composable
private fun LotInteractionLandscape(
    modifier: Modifier = Modifier,
    state: LotInteractionState,
    onHighlightComplete: () -> Unit,
    onLotSearchClick: () -> Unit,
    onShowDialogClick: (LotInventoryUi) -> Unit,
    onLotDelta: (LotInventoryUi, Int) -> Unit,
    onDeleteClick: (String, Int, Boolean) -> Unit,
    onBarcodeScanned: (ParsedBarcode) -> Unit
) {
    Row(
        modifier
            .padding(top = measurement.spacing.small)
            .padding(horizontal = 40.dp)
    ) {
        Column(modifier = Modifier.width(784.dp)) {
            LotInventorySheet(
                modifier = Modifier.weight(1f),
                state = state,
                isLandscape = true,
                onHighlightComplete = onHighlightComplete,
                onShowDialogClick = onShowDialogClick,
                onLotDelta = onLotDelta,
                onDeleteClick = onDeleteClick
            )

            ProductSummaryFooter(
                antigen = state.antigen,
                prettyName = state.prettyName,
                cartonCount = state.cartonCount,
                lotCountOriginal = state.lotCountOriginal,
                lotCountTotal = state.lotCountTotal,
            )
        }

        LandscapeScannerSection(
            headerText = stringResource(R.string.lot_interaction_scan_each_product_lot),
            scannerActive = state.isScannerActive,
            invalidScan = state.error == LotInteractionError.BadBarcodeScan,
            onBarcodeScanned = onBarcodeScanned,
            onLotSearchClick = onLotSearchClick,
            modifier = Modifier.padding(start = measurement.spacing.small)
        )
    }
}

@Composable
private fun LotInventorySheet(
    modifier: Modifier = Modifier,
    state: LotInteractionState,
    isLandscape: Boolean,
    onHighlightComplete: () -> Unit,
    onShowDialogClick: (LotInventoryUi) -> Unit,
    onLotDelta: (LotInventoryUi, Int) -> Unit,
    onDeleteClick: (String, Int, Boolean) -> Unit,
) {
    Surface(
        color = color.container.primaryContainer,
        shape = RoundedCornerShape(measurement.radius.cardMedium),
        modifier = modifier
    ) {
        val selectedFlash = remember {
            Animatable(Color.Transparent)
        }
        val flashColor = LocalStock.current.colors.containerLight
        val defaultColor = color.container.primaryContainer
        val lazyListState = rememberLazyListState()

        LaunchedEffect(state.searchedLot) {
            // scroll
            val selectedIndex = state.lots
                .indexOfFirst {
                    it.lotNumber == state.searchedLot
                }.also {
                    if (it == -1) {
                        onHighlightComplete()
                        return@LaunchedEffect
                    }
                }

            lazyListState.animateScrollToItem(selectedIndex)

            // color flash
            selectedFlash.animateTo(flashColor)
            delay(300)
            selectedFlash.animateTo(defaultColor)
            onHighlightComplete()
        }

        Column {
            LotSheetHeader(isLandscape)

            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(bottom = 88.dp),
                modifier = Modifier.fillMaxHeight(),
            ) {
                items(
                    items = state.lots,
                    key = { item -> item.lotNumber }
                ) { item ->
                    val overlayColor = if (item.lotNumber == state.searchedLot) {
                        selectedFlash.value
                    } else {
                        Color.Transparent
                    }

                    Box(modifier = Modifier.background(overlayColor)) {
                        LotInteractionItem(
                            item = item,
                            backgroundColor = if (item.isActionRequired) {
                                color.container.warningContainer
                            } else {
                                Color.Transparent
                            },
                            isLandscape = isLandscape,
                            onTextboxClick = { onShowDialogClick(item) },
                            presentation = state.presentation,
                            onDeltaUpdate = { delta ->
                                onLotDelta(item, delta)
                            },
                            onDeleteClick = {
                                onDeleteClick(
                                    item.lotNumber,
                                    item.onHand,
                                    item.isDeleted
                                )
                            }
                        )
                        HorizontalDivider(thickness = 2.dp, color = color.outline.threeHundred)
                    }
                }
            }
        }
    }
}

@Composable
private fun LotInteractionPortrait(
    modifier: Modifier = Modifier,
    state: LotInteractionState,
    onHighlightComplete: () -> Unit,
    onLotSearchClick: () -> Unit,
    onShowDialogClick: (LotInventoryUi) -> Unit,
    onLotDelta: (LotInventoryUi, Int) -> Unit,
    onDeleteClick: (String, Int, Boolean) -> Unit,
    onBarcodeScanned: (ParsedBarcode) -> Unit
) {
    Column(modifier) {
        PortraitScannerSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = measurement.spacing.small,
                    bottom = measurement.spacing.large
                ),
            headerText = stringResource(R.string.lot_interaction_scan_each_product_lot),
            scannerActive = state.isScannerActive,
            onBarcodeScanned = onBarcodeScanned,
            onLotSearchClick = onLotSearchClick,
            invalidScan = state.error == LotInteractionError.BadBarcodeScan
        )

        Column(modifier = Modifier.padding(horizontal = measurement.spacing.small)) {
            LotInventorySheet(
                modifier = Modifier.weight(1f),
                state = state,
                isLandscape = false,
                onHighlightComplete = onHighlightComplete,
                onShowDialogClick = onShowDialogClick,
                onLotDelta = onLotDelta,
                onDeleteClick = onDeleteClick
            )

            ProductSummaryFooter(
                antigen = state.antigen,
                prettyName = state.prettyName,
                cartonCount = state.cartonCount,
                lotCountOriginal = state.lotCountOriginal,
                lotCountTotal = state.lotCountTotal,
            )
        }
    }
}

@Composable
private fun ProductSummaryFooter(
    antigen: String,
    prettyName: String,
    cartonCount: Int,
    lotCountOriginal: String?,
    lotCountTotal: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(152.dp)
            .padding(
                top = measurement.spacing.xSmall,
                bottom = measurement.spacing.large
            ).background(
                color = color.container.primaryContainer,
                shape = RoundedCornerShape(measurement.radius.cardMedium)
            ).padding(
                start = measurement.spacing.large,
                top = measurement.spacing.small,
                bottom = measurement.spacing.small,
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = antigen,
                style = type.bodyTypeStyle.body3Bold
            )
            Text(
                text = prettyName,
                style = type.bodyTypeStyle.body3
            )
            Spacer(Modifier.height(8.dp))
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
            if (lotCountOriginal != null) {
                Text(
                    text = lotCountOriginal,
                    style = type.headerTypeStyle.headlineMedium.copy(
                        fontStyle = FontStyle.Italic,
                        textDecoration = TextDecoration.LineThrough
                    )
                )
            }
            Spacer(Modifier.width(measurement.spacing.xSmall))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.total),
                    style = type.bodyTypeStyle.label,
                    modifier = Modifier.padding(bottom = measurement.spacing.xSmall)
                )

                AnimatedContent(
                    targetState = lotCountTotal,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInVertically { -it } togetherWith slideOutVertically { it }
                        } else {
                            slideInVertically { it } togetherWith slideOutVertically { -it }
                        }
                    }
                ) { total ->
                    Text(
                        text = total,
                        style = type.headerTypeStyle.headlineMediumBold,
                        modifier = Modifier.testTag(TestTags.Counts.LotInteraction.FOOTER_TOTAL_QUANTITY_LABEL)
                    )
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
private fun LotInteractionItem(
    modifier: Modifier = Modifier,
    item: LotInventoryUi,
    backgroundColor: Color = Color.White,
    isLandscape: Boolean,
    presentation: Presentation,
    onTextboxClick: () -> Unit,
    onDeltaUpdate: (Int) -> Unit,
    onDeleteClick: () -> Unit
) {
    val (lotWidth, quantityWidth) = if (isLandscape) {
        LOT_COLUMN_WIDTH_LANDSCAPE to QUANT_COLUMN_WIDTH_LANDSCAPE
    } else {
        LOT_COLUMN_WIDTH_PORTRAIT to QUANT_COLUMN_WIDTH_PORTRAIT
    }

    AnimatedContent(
        targetState = item.isDeleted,
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { isDeleted ->

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .testTag(TestTags.Counts.LotInteraction.lotItem(item.lotNumber))
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(vertical = measurement.spacing.small)
        ) {
            with(item) {
                ProductCell(
                    topContent = {
                        if (isActionRequired) {
                            Text(
                                text = stringResource(R.string.lot_interaction_update_count),
                                style = type.bodyTypeStyle.body5Italic,
                            )
                        }
                    },
                    titleRow = {
                        ProductTitleLine(
                            leadingIcon = Icons.presentationIcon(presentation),
                            title = lotNumber
                        )
                    },
                    bottomContent = {
                        LotExpirationText(
                            isExpired = isExpired,
                            expiration = expiration
                        )
                    },
                    strikeText = isDeleted,
                    modifier = Modifier
                        .width(lotWidth.dp)
                        .padding(start = measurement.spacing.small)
                )
            }

            if (!isDeleted) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.width(quantityWidth.dp)
                ) {
                    EditQuantityCell(
                        quantity = item.adjustment ?: item.onHand,
                        onDecrementClick = { onDeltaUpdate(-1) },
                        onDecrementLongClick = { onDeltaUpdate(-5) },
                        onIncrementClick = { onDeltaUpdate(1) },
                        onIncrementLongClick = { onDeltaUpdate(5) },
                        onInputNumberClick = onTextboxClick,
                        decrementEnabled = (item.adjustment ?: item.onHand) > 0,
                    )
                }
            }

            if (isDeleted) {
                ElevatedButton(
                    onClick = onDeleteClick,
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
private fun NumberInput(
    value: Int,
    isMinusEnabled: Boolean,
    onDeltaUpdate: (Int) -> Unit,
    onTextboxClick: () -> Unit,
    lotNumber: String
) {
    Row(
        modifier = Modifier.width(312.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        ElevatedIconButton(
            enabled = isMinusEnabled,
            onClick = { onDeltaUpdate(-1) },
            onLongClick = { onDeltaUpdate(-5) },
            iconDrawRes = DesignSystemR.drawable.ic_minus,
            modifier = Modifier.testTag(TestTags.Counts.LotInteraction.lotItemMinusButton(lotNumber))
        )

        Box(
            modifier = Modifier
                .padding(horizontal = measurement.spacing.xSmall)
                .border(
                    width = 1.dp,
                    color = color.outline.fourHundred,
                    shape = RoundedCornerShape(measurement.radius.chip)
                ).size(80.dp, 60.dp)
                .clip(RoundedCornerShape(measurement.radius.chip))
                .background(
                    color = Color.White,
                ).clickable(onClick = onTextboxClick)
                .padding(horizontal = measurement.spacing.xSmall)
                .testTag(TestTags.Counts.LotInteraction.lotItemQuantityInput(lotNumber)),
            contentAlignment = Alignment.CenterEnd
        ) {
            AnimatedContent(
                targetState = value,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically { -it } togetherWith slideOutVertically { it }
                    } else {
                        slideInVertically { it } togetherWith slideOutVertically { -it }
                    }
                }
            ) { confirm ->

                Text(
                    text = (confirm).toString(),
                    style = type.bodyTypeStyle.body2Bold,
                    textAlign = TextAlign.End
                )
            }
        }

        ElevatedIconButton(
            onClick = { onDeltaUpdate(1) },
            onLongClick = { onDeltaUpdate(5) },
            modifier = Modifier
                .padding(end = measurement.spacing.small)
                .testTag(TestTags.Counts.LotInteraction.lotItemPlusButton(lotNumber)),
            iconDrawRes = DesignSystemR.drawable.ic_plus
        )
    }
}

@FullDevicePreview
@Composable
private fun LotInteractionPreview() {
    LotInteractionContent(
        orientation = LocalConfiguration.current.orientation,
        state = LotInteractionSampleData.Default.copy(
            searchedLot = "TLSK498",
            lots = LotInteractionSampleData.Default.lots.subList(0, 4),
            lotCountOriginal = "125k",
            lotCountTotal = "125k",
            error = LotInteractionError.BadBarcodeScan
        ),
        onHighlightComplete = {},
        onNavigateBack = {},
        onSearchLotClick = {},
        onShowDialogClick = {},
        onConfirmInventoryClick = {},
        onLotDelta = { _, _ -> },
        onDeleteClick = { _, _, _ -> },
        onBarcodeScanned = { }
    )
}
