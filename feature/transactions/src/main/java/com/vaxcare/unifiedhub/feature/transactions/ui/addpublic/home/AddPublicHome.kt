package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.Icons
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.core.ui.component.ButtonConfig
import com.vaxcare.unifiedhub.core.ui.component.VCBasicDialog
import com.vaxcare.unifiedhub.core.ui.component.VCScaffold
import com.vaxcare.unifiedhub.core.ui.component.button.ElevatedButton
import com.vaxcare.unifiedhub.core.ui.component.button.ElevatedIconButton
import com.vaxcare.unifiedhub.core.ui.component.button.VCFloatingActionButton
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.ProductCell
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.compose.preview.LandscapePreview
import com.vaxcare.unifiedhub.core.ui.compose.preview.PortraitPreview
import com.vaxcare.unifiedhub.core.ui.ext.verticalFadingEdge
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.model.ProductUi
import com.vaxcare.unifiedhub.feature.transactions.ui.common.components.LandscapeScannerSection
import com.vaxcare.unifiedhub.feature.transactions.ui.common.components.PortraitScannerSection
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode
import kotlinx.coroutines.launch
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

private const val PRODUCT_QUANT_WIDTH_PORTRAIT = 471
private const val PRODUCT_QUANT_WIDTH_LANDSCAPE = 584

@Composable
fun AddPublicHomeScreen(
    navigateBack: () -> Unit,
    navigateToSummary: () -> Unit,
    navigateToLotInteraction: () -> Unit,
    navigateToLotSearch: (Int) -> Unit,
    viewModel: AddPublicHomeViewModel = hiltViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    BaseMviScreen(
        viewModel = viewModel,
        onEvent = {
            when (it) {
                AddPublicHomeEvent.NavigateBack -> navigateBack()

                AddPublicHomeEvent.NavigateToLotInteraction -> navigateToLotInteraction()

                is AddPublicHomeEvent.NavigateToLotSearch -> navigateToLotSearch(it.sourceId)

                AddPublicHomeEvent.NavigateToSummary -> navigateToSummary()

                is AddPublicHomeEvent.ScrollToItem -> {
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(it.index)
                    }
                }
            }
        }
    ) { state, handleIntent ->

        when (state.activeDialog) {
            AddPublicHomeDialog.DiscardChanges -> {
                DiscardChangesDialog(
                    onDismiss = { handleIntent(AddPublicHomeIntent.DismissDialog) },
                    onDiscard = { handleIntent(AddPublicHomeIntent.DiscardChanges) }
                )
            }

            AddPublicHomeDialog.ExpiredProductScanned -> {
                ExpiredProductDialog(
                    onDismiss = { handleIntent(AddPublicHomeIntent.DismissDialog) }
                )
            }

            is AddPublicHomeDialog.WrongProductScanned -> {
                WrongProductDialog(
                    onDismiss = { handleIntent(AddPublicHomeIntent.DismissDialog) },
                    errorMessage = state.activeDialog.errorMessage
                )
            }
        }

        ProvideStock(state.stockType) {
            AddPublicHomeContent(
                state = state,
                orientation = LocalConfiguration.current.orientation,
                lazyListState = lazyListState,
                onBackClick = { handleIntent(AddPublicHomeIntent.CloseScreen) },
                onUndoProductClick = { handleIntent(AddPublicHomeIntent.RestoreProduct(it)) },
                onEditProductClick = { handleIntent(AddPublicHomeIntent.EditProduct(it)) },
                onDeleteProductClick = { handleIntent(AddPublicHomeIntent.DeleteProduct(it)) },
                onSearchClick = { handleIntent(AddPublicHomeIntent.SearchLots) },
                onNextClick = { handleIntent(AddPublicHomeIntent.ProceedToSummary) },
                onBarcodeScanned = { handleIntent(AddPublicHomeIntent.ScanLot(it)) }
            )
        }
    }
}

@Composable
fun AddPublicHomeContent(
    state: AddPublicHomeState,
    orientation: Int,
    lazyListState: LazyListState,
    onBackClick: () -> Unit,
    onUndoProductClick: (ProductUi) -> Unit,
    onEditProductClick: (ProductUi) -> Unit,
    onDeleteProductClick: (ProductUi) -> Unit,
    onSearchClick: () -> Unit,
    onNextClick: () -> Unit,
    onBarcodeScanned: (ParsedBarcode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val products = state.products
    val isNextEnabled = products.isNotEmpty()

    VCScaffold(
        topBar = {
            BaseTitleBar(
                modifier = modifier,
                title = R.string.add_public_home_title,
                buttonIcon = DesignSystemR.drawable.ic_chevron_left,
                onButtonClick = onBackClick
            )
        },
        fab = {
            VCFloatingActionButton(
                onClick = onNextClick,
                iconPainter = painterResource(DesignSystemR.drawable.ic_arrow_forward),
                enabled = isNextEnabled
            )
        },
    ) {
        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                AddPublicHomeLandscape(
                    isScannerActive = state.isScannerActive,
                    total = state.total,
                    stock = state.stockType,
                    products = products,
                    invalidScan = state.isInvalidScan,
                    lazyListState = lazyListState,
                    onUndoProductClick = onUndoProductClick,
                    onEditProductClick = onEditProductClick,
                    onDeleteProductClick = onDeleteProductClick,
                    onSearchClick = onSearchClick,
                    onBarcodeScanned = onBarcodeScanned
                )
            }

            Configuration.ORIENTATION_PORTRAIT -> {
                AddPublicHomePortrait(
                    isScannerActive = state.isScannerActive,
                    total = state.total,
                    stock = state.stockType,
                    products = products,
                    invalidScan = state.isInvalidScan,
                    lazyListState = lazyListState,
                    onUndoProductClick = onUndoProductClick,
                    onEditProductClick = onEditProductClick,
                    onDeleteProductClick = onDeleteProductClick,
                    onSearchClick = onSearchClick,
                    onBarcodeScanned = onBarcodeScanned
                )
            }
        }
    }
}

@Composable
fun AddPublicHomeLandscape(
    isScannerActive: Boolean,
    total: Int,
    stock: StockUi,
    products: List<ProductUi>,
    invalidScan: Boolean,
    lazyListState: LazyListState,
    onUndoProductClick: (ProductUi) -> Unit,
    onEditProductClick: (ProductUi) -> Unit,
    onDeleteProductClick: (ProductUi) -> Unit,
    onBarcodeScanned: (ParsedBarcode) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(measurement.spacing.small, Alignment.Start),
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = measurement.spacing.large)
            .padding(top = measurement.spacing.small, bottom = 18.dp)
    ) {
        Column {
            ProductSheet(
                isLandscape = true,
                lazyListState = lazyListState,
                products = products,
                onUndoProductClick = onUndoProductClick,
                onEditProductClick = onEditProductClick,
                onDeleteProductClick = onDeleteProductClick,
                modifier = Modifier.weight(1f)
            )
            DestinationAndTotalFooter(
                isLandscape = true,
                total = total,
                stock = stock,
                modifier = Modifier.padding(top = measurement.spacing.xSmall)
            )
        }
        LandscapeScannerSection(
            headerText = stringResource(R.string.scan_each_product),
            scannerActive = isScannerActive,
            invalidScan = invalidScan,
            onBarcodeScanned = onBarcodeScanned,
            onLotSearchClick = onSearchClick,
        )
    }
}

@Composable
fun AddPublicHomePortrait(
    isScannerActive: Boolean,
    total: Int,
    stock: StockUi,
    products: List<ProductUi>,
    invalidScan: Boolean,
    lazyListState: LazyListState,
    onUndoProductClick: (ProductUi) -> Unit,
    onEditProductClick: (ProductUi) -> Unit,
    onDeleteProductClick: (ProductUi) -> Unit,
    onBarcodeScanned: (ParsedBarcode) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column {
        Spacer(modifier = Modifier.height(measurement.spacing.small))
        Row(
            horizontalArrangement = Arrangement.spacedBy(measurement.spacing.xLarge, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PortraitScannerSection(
                headerText = stringResource(R.string.scan_each_product),
                scannerActive = isScannerActive,
                invalidScan = invalidScan,
                onBarcodeScanned = onBarcodeScanned,
                onLotSearchClick = onSearchClick
            )
        }
        Spacer(modifier = Modifier.height(measurement.spacing.large))
        Column(
            modifier = Modifier
                .padding(horizontal = measurement.spacing.small)
                .padding(bottom = 38.dp)
        ) {
            ProductSheet(
                isLandscape = false,
                lazyListState = lazyListState,
                products = products,
                onUndoProductClick = onUndoProductClick,
                onEditProductClick = onEditProductClick,
                onDeleteProductClick = onDeleteProductClick,
                modifier = modifier.weight(1f)
            )
            DestinationAndTotalFooter(
                isLandscape = false,
                total = total,
                stock = stock,
                modifier = Modifier.padding(top = measurement.spacing.xSmall)
            )
        }
    }
}

@Composable
private fun DestinationAndTotalFooter(
    isLandscape: Boolean,
    total: Int,
    stock: StockUi,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(size = measurement.radius.cardMedium),
        color = color.container.primaryContainer,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(measurement.spacing.medium, Alignment.Start),
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .height(104.dp)
                .width(if (isLandscape) 784.dp else measurement.size.cardLarge)
                .padding(
                    start = measurement.spacing.large,
                    top = measurement.spacing.small,
                    end = 184.dp,
                    bottom = measurement.spacing.small
                )
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(measurement.spacing.xSmall),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.add_public_home_destination).uppercase(),
                    style = type.bodyTypeStyle.label.copy(fontWeight = FontWeight.SemiBold),
                    color = color.onContainer.onContainerPrimary
                )
                Text(
                    text = stringResource(R.string.add_public_home_stock_inventory, stock.prettyName),
                    style = type.bodyTypeStyle.body3,
                    color = color.onContainer.onContainerPrimary
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(measurement.spacing.xSmall),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = stringResource(R.string.add_public_home_total).uppercase(),
                    style = type.bodyTypeStyle.label.copy(fontWeight = FontWeight.SemiBold),
                    color = color.onContainer.onContainerPrimary
                )
                Text(
                    text = total.toString(),
                    style = type.headerTypeStyle.headlineMediumBold,
                    color = color.onContainer.onContainerPrimary
                )
            }
        }
    }
}

@Composable
fun ProductSheet(
    isLandscape: Boolean,
    lazyListState: LazyListState,
    products: List<ProductUi>,
    onUndoProductClick: (ProductUi) -> Unit,
    onEditProductClick: (ProductUi) -> Unit,
    onDeleteProductClick: (ProductUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    val width = if (isLandscape) {
        784.dp
    } else {
        measurement.size.cardLarge
    }
    Surface(
        shape = RoundedCornerShape(
            size = measurement.radius.cardMedium,
        ),
        color = color.container.primaryContainer,
        modifier = modifier
            .width(width)
    ) {
        Column {
            if (products.isNotEmpty()) {
                ProductSheetHeader(isLandscape = isLandscape)

                Box(modifier = Modifier.fillMaxWidth()) {
                    LazyColumn(
                        state = lazyListState,
                        contentPadding = PaddingValues(bottom = 88.dp),
                        modifier = Modifier
                            .verticalFadingEdge(lazyListState = lazyListState, 32.dp)
                    ) {
                        itemsIndexed(
                            items = products,
                            key = { _, item -> item.productId }
                        ) { _, item ->
                            ProductListItem(
                                isLandscape = isLandscape,
                                product = item,
                                onUndoClick = { onUndoProductClick(item) },
                                onEditClick = { onEditProductClick(item) },
                                onDeleteClick = { onDeleteProductClick(item) }
                            )
                            HorizontalDivider(
                                color = color.outline.twoHundred,
                                thickness = 2.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductSheetHeader(isLandscape: Boolean, modifier: Modifier = Modifier) {
    val headerWidth = if (isLandscape) {
        PRODUCT_QUANT_WIDTH_LANDSCAPE
    } else {
        PRODUCT_QUANT_WIDTH_PORTRAIT
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .size(headerWidth.dp, 40.dp)
            .padding(start = 48.dp, top = 8.dp)
    ) {
        Text(
            text = stringResource(DesignSystemR.string.product).uppercase(),
            style = type.bodyTypeStyle.body6Bold,
        )
        Text(
            text = stringResource(DesignSystemR.string.quantity).uppercase(),
            style = type.bodyTypeStyle.body6Bold
        )
    }
    HorizontalDivider(thickness = 0.5.dp)
}

@Composable
private fun ProductListItem(
    isLandscape: Boolean,
    product: ProductUi,
    onUndoClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp)
    ) {
        val contentModifier = if (isLandscape) {
            Modifier.width(PRODUCT_QUANT_WIDTH_LANDSCAPE.dp)
        } else {
            Modifier.width(PRODUCT_QUANT_WIDTH_PORTRAIT.dp)
        }

        ProductContent(
            product = product,
            modifier = contentModifier
                .padding(start = measurement.spacing.small)
        )
        ProductActions(
            isDeleted = product.isDeleted,
            onUndoClick = onUndoClick,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
        )
    }
}

@Composable
private fun ProductContent(product: ProductUi, modifier: Modifier = Modifier) {
    with(product) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
        ) {
            ProductCell(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(end = measurement.spacing.medium),
                titleRow = {
                    ProductTitleLine(
                        leadingIcon = Icons.presentationIcon(presentation),
                        title = productInfoText(antigen, prettyName)
                    )
                },
                bottomContent = {
                    ProductCellText(text = getLotInfo())
                },
                strikeText = isDeleted,
            )
            if (!isDeleted) {
                Text(
                    text = getQuantity().toString(),
                    fontWeight = FontWeight.SemiBold,
                    style = type.bodyTypeStyle.body3
                )
            }
        }
    }
}

@Composable
private fun ProductActions(
    isDeleted: Boolean,
    onUndoClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        if (isDeleted) {
            ElevatedButton(
                onClick = onUndoClick,
                text = stringResource(R.string.undo),
                modifier = Modifier.padding(end = measurement.spacing.small),
            )
        } else {
            ElevatedIconButton(
                onClick = onEditClick,
                modifier = Modifier.padding(end = measurement.spacing.small)
            ) {
                Icon(
                    painter = painterResource(DesignSystemR.drawable.ic_edit),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(12.dp)
                )
            }
            ElevatedIconButton(
                onClick = onDeleteClick,
                modifier = Modifier.padding(end = measurement.spacing.small)
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
private fun DiscardChangesDialog(
    onDismiss: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier
) {
    VCBasicDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = stringResource(R.string.discard_changes),
        text = stringResource(R.string.discard_changes_description),
        primaryButtonConfig = ButtonConfig(
            text = stringResource(DesignSystemR.string.yes),
            onClick = onDiscard
        ),
        secondaryButtonConfig = ButtonConfig(
            text = stringResource(DesignSystemR.string.no),
            onClick = onDismiss
        ),
    )
}

@Composable
private fun ExpiredProductDialog(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    VCBasicDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = stringResource(R.string.expired_product_title),
        text = stringResource(R.string.expired_product_body),
        primaryButtonConfig = ButtonConfig(
            text = stringResource(DesignSystemR.string.ok),
            onClick = onDismiss
        )
    )
}

@Composable
private fun WrongProductDialog(
    onDismiss: () -> Unit,
    errorMessage: AnnotatedString,
    modifier: Modifier = Modifier
) {
    VCBasicDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = stringResource(R.string.invalid_scan_title),
        text = errorMessage,
        primaryButtonConfig = ButtonConfig(
            text = stringResource(DesignSystemR.string.ok),
            onClick = onDismiss
        )
    )
}

@PortraitPreview
@Composable
private fun ActionRequiredDialog() {
    DiscardChangesDialog({}, {})
}

@FullDevicePreview
@Composable
private fun StockVFC() {
    ProvideStock(StockUi.VFC) {
        AddPublicHomeContent(
            state = AddPublicHomeSampleData.Default,
            orientation = LocalConfiguration.current.orientation,
            lazyListState = rememberLazyListState(),
            {},
            {},
            {},
            {},
            {},
            {},
            {}
        )
    }
}

@LandscapePreview
@Composable
private fun StockState() {
    ProvideStock(StockUi.STATE) {
        AddPublicHomeContent(
            state = AddPublicHomeSampleData.getStateForStock(StockUi.STATE),
            orientation = LocalConfiguration.current.orientation,
            lazyListState = rememberLazyListState(),
            {},
            {},
            {},
            {},
            {},
            {},
            {}
        )
    }
}

@LandscapePreview
@Composable
private fun Stock317() {
    ProvideStock(StockUi.THREE_SEVENTEEN) {
        AddPublicHomeContent(
            state = AddPublicHomeSampleData.getStateForStock(StockUi.THREE_SEVENTEEN),
            orientation = LocalConfiguration.current.orientation,
            lazyListState = rememberLazyListState(),
            {},
            {},
            {},
            {},
            {},
            {},
            {}
        )
    }
}

@PortraitPreview
@Composable
private fun EmptyState() {
    ProvideStock(StockUi.VFC) {
        AddPublicHomeContent(
            state = AddPublicHomeState(stockType = StockUi.VFC),
            orientation = LocalConfiguration.current.orientation,
            lazyListState = rememberLazyListState(),
            {},
            {},
            {},
            {},
            {},
            {},
            {}
        )
    }
}
