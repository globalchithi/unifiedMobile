package com.vaxcare.unifiedhub.feature.home.ui.onhand

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.VCScaffold
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.ProductCell
import com.vaxcare.unifiedhub.core.ui.compose.LocalStock
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullscreenPreview
import com.vaxcare.unifiedhub.core.ui.ext.verticalFadingEdge
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.home.R
import com.vaxcare.unifiedhub.feature.home.ui.component.StockMenuDialog
import com.vaxcare.unifiedhub.feature.home.ui.component.StockSelectorOneLine
import com.vaxcare.unifiedhub.feature.home.ui.onhand.OnHandDialog.StockSelection
import com.vaxcare.unifiedhub.feature.home.ui.onhand.OnHandIntent.DismissDialog
import com.vaxcare.unifiedhub.feature.home.ui.onhand.OnHandIntent.OpenStockSelector
import com.vaxcare.unifiedhub.feature.home.ui.onhand.OnHandIntent.SelectSection
import com.vaxcare.unifiedhub.feature.home.ui.onhand.OnHandIntent.SelectStock
import com.vaxcare.unifiedhub.feature.home.ui.onhand.model.GroupedProducts
import com.vaxcare.unifiedhub.feature.home.ui.onhand.model.OnHandSection
import com.vaxcare.unifiedhub.feature.home.ui.onhand.model.OnHandSection.NON_SEASONAL
import com.vaxcare.unifiedhub.feature.home.ui.onhand.model.OnHandSection.SEASONAL
import com.vaxcare.unifiedhub.feature.home.ui.onhand.model.ProductUI
import kotlinx.coroutines.launch
import com.vaxcare.unifiedhub.core.designsystem.R as designSystemR

private const val LIST_CONTAINER_WIDTH_PORTRAIT = 704
private const val LIST_CONTAINER_WIDTH_LANDSCAPE = 1104
private const val ROW_WIDTH_PORTRAIT = 544
private const val ROW_WIDTH_LANDSCAPE = 704

@Composable
fun OnHand(
    jumpToSeasonal: Boolean = false,
    jumpToNonSeasonal: Boolean = false,
    onJumpCompleted: () -> Unit,
    viewModel: OnHandViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    fun scrollToItem(index: Int, onComplete: () -> Unit = {}) {
        coroutineScope
            .launch {
                lazyListState.animateScrollToItem(index)
            }.invokeOnCompletion { onComplete() }
    }

    BaseMviScreen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                is OnHandEvent.ScrollToItem -> {
                    scrollToItem(event.index)
                }
            }
        }
    ) { state, handleIntent ->
        LaunchedEffect(jumpToSeasonal, state.seasonalProducts, state.nonSeasonalProducts) {
            if (jumpToSeasonal) {
                scrollToItem(state.nonSeasonalProducts.lastIndex + 1, onJumpCompleted)
            }
        }
        LaunchedEffect(jumpToNonSeasonal, state.seasonalProducts, state.nonSeasonalProducts) {
            if (jumpToNonSeasonal) {
                scrollToItem(0, onJumpCompleted)
            }
        }

        ProvideStock(state.activeStock) {
            when (state.activeDialog) {
                StockSelection -> {
                    StockMenuDialog(
                        onDismiss = {
                            handleIntent(DismissDialog)
                        },
                        stockOptions = state.availableStocks,
                        activeStock = state.activeStock,
                        onStockSelected = {
                            handleIntent(SelectStock(it))
                        }
                    )
                }
            }
            OnHandContent(
                orientation = LocalConfiguration.current.orientation,
                lazyListState = lazyListState,
                state = state,
                onTabClick = { handleIntent(SelectSection(it)) },
                onStockClick = { handleIntent(OpenStockSelector) }
            )
        }
    }
}

@Composable
private fun OnHandContent(
    orientation: Int,
    state: OnHandState,
    lazyListState: LazyListState,
    onTabClick: (OnHandSection) -> Unit,
    onStockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    VCScaffold(
        topBar = { },
    ) {
        when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                OnHandPortrait(
                    selectedStock = state.activeStock,
                    nonSeasonalProducts = state.nonSeasonalProducts,
                    seasonalProducts = state.seasonalProducts,
                    showStockSelectionButton = state.enableStockSelection,
                    lazyListState = lazyListState,
                    onTabClick = onTabClick,
                    onStockClick = onStockClick,
                    modifier = modifier
                )
            }

            Configuration.ORIENTATION_LANDSCAPE -> {
                OnHandLandscape(
                    selectedStock = state.activeStock,
                    nonSeasonalProducts = state.nonSeasonalProducts,
                    seasonalProducts = state.seasonalProducts,
                    showStockSelectionButton = state.enableStockSelection,
                    lazyListState = lazyListState,
                    onTabClick = onTabClick,
                    onStockClick = onStockClick,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun OnHandPortrait(
    seasonalProducts: List<GroupedProducts>,
    nonSeasonalProducts: List<GroupedProducts>,
    selectedStock: StockUi,
    showStockSelectionButton: Boolean,
    lazyListState: LazyListState,
    onTabClick: (OnHandSection) -> Unit,
    onStockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(measurement.spacing.small, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = measurement.spacing.small)
            .padding(top = measurement.spacing.topBarLargeY)
    ) {
        OnHandHeader(selectedStock, showStockSelectionButton, onStockClick)
        Column {
            AnimatedVisibility(
                visible = seasonalProducts.isNotEmpty() && nonSeasonalProducts.isNotEmpty(),
                enter = fadeIn(),
                label = "SectionTabs"
            ) {
                SectionTabs(
                    seasonalHeaderIndex = nonSeasonalProducts.lastIndex + 1,
                    onTabClick = onTabClick,
                    modifier = Modifier.padding(start = measurement.spacing.small, bottom = measurement.spacing.small),
                    lazyListState = lazyListState
                )
            }
            ProductSheet(
                isLandscape = false,
                lazyListState = lazyListState,
                seasonalProducts = seasonalProducts,
                nonSeasonalProducts = nonSeasonalProducts,
            )
        }
    }
}

@Composable
private fun OnHandLandscape(
    seasonalProducts: List<GroupedProducts>,
    nonSeasonalProducts: List<GroupedProducts>,
    selectedStock: StockUi,
    showStockSelectionButton: Boolean,
    lazyListState: LazyListState,
    onTabClick: (OnHandSection) -> Unit,
    onStockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(measurement.spacing.small, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = measurement.spacing.large)
            .padding(top = measurement.spacing.topBarMediumY)
    ) {
        OnHandHeader(selectedStock, showStockSelectionButton, onStockClick)
        Column {
            AnimatedVisibility(
                visible = seasonalProducts.isNotEmpty() && nonSeasonalProducts.isNotEmpty(),
                enter = fadeIn(),
                label = "SectionTabs"
            ) {
                SectionTabs(
                    seasonalHeaderIndex = nonSeasonalProducts.lastIndex + 1,
                    onTabClick = onTabClick,
                    modifier = Modifier.padding(start = measurement.spacing.small, bottom = measurement.spacing.small),
                    lazyListState = lazyListState
                )
            }
            ProductSheet(
                isLandscape = true,
                lazyListState = lazyListState,
                seasonalProducts = seasonalProducts,
                nonSeasonalProducts = nonSeasonalProducts,
            )
        }
    }
}

@Composable
private fun OnHandHeader(
    selectedStock: StockUi,
    showStockSelectionButton: Boolean,
    onStockClick: () -> Unit,
    textStyle: TextStyle = type.displayTypeStyle.display3
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(measurement.spacing.xSmall, Alignment.Top),
        modifier = Modifier.padding(horizontal = 40.dp)
    ) {
        Text(
            text = stringResource(R.string.on_hand_header).uppercase(),
            style = type.bodyTypeStyle.body5Bold,
            color = color.onContainer.info
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            StockSelectorOneLine(selectedStock, showStockSelectionButton, onStockClick, textStyle)
        }
    }
}

@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun SectionTabs(
    lazyListState: LazyListState,
    seasonalHeaderIndex: Int,
    onTabClick: (OnHandSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentTab = remember(lazyListState.firstVisibleItemIndex, seasonalHeaderIndex) {
        if (lazyListState.firstVisibleItemIndex < seasonalHeaderIndex) {
            NON_SEASONAL
        } else {
            SEASONAL
        }
    }

    Row(
        modifier = modifier
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = color.outline.fourHundred,
                shape = CircleShape
            )
    ) {
        OnHandSection.entries.forEach {
            val containerColor by animateColorAsState(
                targetValue = if (currentTab == it) {
                    LocalStock.current.colors.container
                } else {
                    Color.Transparent
                }
            )
            val textColor by animateColorAsState(
                targetValue = if (currentTab == it) {
                    color.onContainer.primaryInverse
                } else {
                    color.onContainer.onContainerPrimary
                }
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(151.dp, 40.dp)
                    .background(containerColor)
                    .clickable { onTabClick(it) }
            ) {
                Text(
                    text = stringResource(it.title),
                    style = type.bodyTypeStyle.body6Bold,
                    color = textColor
                )
            }
        }
    }
}

@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun ProductSheet(
    isLandscape: Boolean,
    lazyListState: LazyListState,
    seasonalProducts: List<GroupedProducts>,
    nonSeasonalProducts: List<GroupedProducts>,
) {
    if (seasonalProducts.size + nonSeasonalProducts.size == 0) {
        if (isLandscape) {
            EmptyContent(modifier = Modifier.height(512.dp))
        } else {
            EmptyContent(modifier = Modifier.height(896.dp))
        }
    } else {
        ProductSheetContent(
            isLandscape,
            lazyListState,
            nonSeasonalProducts,
            seasonalProducts
        )
    }
}

@Composable
private fun ProductSheetContent(
    isLandscape: Boolean,
    lazyListState: LazyListState,
    nonSeasonalProducts: List<GroupedProducts>,
    seasonalProducts: List<GroupedProducts>,
) {
    val listWidth = remember(isLandscape) {
        if (isLandscape) {
            LIST_CONTAINER_WIDTH_LANDSCAPE
        } else {
            LIST_CONTAINER_WIDTH_PORTRAIT
        }
    }

    Surface(
        shape = RoundedCornerShape(
            topStart = measurement.radius.cardMedium,
            topEnd = measurement.radius.cardMedium,
        ),
        color = color.container.primaryContainer,
        modifier = Modifier.fillMaxHeight().width(listWidth.dp)
    ) {
        Column {
            ProductSheetHeader(isLandscape = isLandscape)
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .verticalFadingEdge(lazyListState = lazyListState, 32.dp)
            ) {
                itemsIndexed(
                    items = nonSeasonalProducts,
                    key = { _, item -> item.inventoryGroup }
                ) { idx, item ->
                    ProductListItem(
                        isLandscape = isLandscape,
                        product = item,
                    )

                    if (idx != nonSeasonalProducts.lastIndex) {
                        HorizontalDivider(thickness = 2.dp, color = color.outline.twoHundred)
                    }
                }

                if (nonSeasonalProducts.isNotEmpty() && seasonalProducts.isNotEmpty()) {
                    item {
                        ProductSectionHeader(
                            title = stringResource(designSystemR.string.seasonal).uppercase(),
                        )
                    }
                }

                itemsIndexed(
                    items = seasonalProducts,
                    key = { _, item -> item.inventoryGroup }
                ) { idx, item ->
                    ProductListItem(
                        isLandscape = isLandscape,
                        product = item,
                    )

                    if (idx != seasonalProducts.lastIndex) {
                        HorizontalDivider(thickness = 2.dp, color = color.outline.twoHundred)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyContent(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(
            topStart = measurement.radius.cardMedium,
            topEnd = measurement.radius.cardMedium,
            bottomStart = measurement.radius.cardMedium,
            bottomEnd = measurement.radius.cardMedium,
        ),
        color = color.container.primaryContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.on_hand_empty),
            style = type.displayTypeStyle.display2,
            color = color.onContainer.disabled,
            modifier = Modifier.padding(measurement.spacing.large)
        )
    }
}

@Composable
private fun InventoryGroupCell(
    groupName: String,
    products: List<ProductUI>,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(measurement.spacing.xSmall),
        modifier = modifier.padding(vertical = 16.dp)
    ) {
        Text(
            text = groupName,
            style = type.bodyTypeStyle.body4Bold,
        )

        products.forEach { product ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(product.presentationIcon),
                    contentDescription = null,
                    modifier = Modifier.width(24.dp).height(24.dp)
                )

                Text(
                    text = buildAnnotatedString {
                        pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                        append(product.onHand.toString())
                        pop()
                        append(" (${product.prettyName})")
                    },
                    style = type.bodyTypeStyle.body4,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (product.isExpired) {
                Text(
                    text = stringResource(R.string.expired_doses),
                    style = type.bodyTypeStyle.body5Bold,
                    color = color.onContainer.error,
                    modifier = Modifier.padding(start = measurement.spacing.large)
                )
            }
        }
    }
}

@Composable
private fun ProductSheetHeader(isLandscape: Boolean, modifier: Modifier = Modifier) {
    val headerWidth = getListRowWidth(isLandscape)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .size(headerWidth, 40.dp)
            .padding(start = 48.dp, top = 8.dp)
    ) {
        Text(
            text = stringResource(designSystemR.string.product).uppercase(),
            style = type.bodyTypeStyle.label,
        )
        Text(
            text = "On Hand".uppercase(),
            style = type.bodyTypeStyle.label,
        )
    }
}

@Composable
private fun getListRowWidth(isLandscape: Boolean) =
    remember(isLandscape) {
        if (isLandscape) {
            ROW_WIDTH_LANDSCAPE.dp
        } else {
            ROW_WIDTH_PORTRAIT.dp
        }
    }

@Composable
private fun ProductSectionHeader(title: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .background(LocalStock.current.colors.containerLight)
    ) {
        Text(
            text = title,
            style = type.bodyTypeStyle.label,
            modifier = Modifier.padding(start = 48.dp)
        )
    }
}

@Composable
private fun ProductListItem(
    isLandscape: Boolean,
    product: GroupedProducts,
    modifier: Modifier = Modifier,
) {
    val contentModifier = Modifier.width(getListRowWidth(isLandscape))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = contentModifier.then(modifier)
    ) {
        if (product.products.size > 1) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                InventoryGroupCell(
                    groupName = product.antigen,
                    products = product.products,
                    modifier = Modifier.padding(start = measurement.spacing.large)
                )
                QuantityCell(product.onHand)
            }
        } else {
            ProductContent(
                product = product.products.firstOrNull(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun QuantityCell(
    quantity: Int,
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.End,
) {
    Column(
        horizontalAlignment = alignment,
        modifier = modifier
    ) {
        Text(
            text = quantity.toString(),
            style = type.bodyTypeStyle.body3Bold,
        )
    }
}

@Composable
private fun ProductContent(product: ProductUI?, modifier: Modifier = Modifier) {
    if (product == null) return
    with(product) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(vertical = measurement.spacing.small)
                .padding(start = measurement.spacing.small)
                .then(modifier)
        ) {
            ProductCell(
                prettyName = prettyName,
                antigenName = antigen,
                presentation = painterResource(presentationIcon),
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(end = measurement.spacing.medium)
            )

            QuantityCell(
                quantity = product.onHand
            )
        }
    }
}

@FullDevicePreview
@Composable
private fun Default() {
    FullscreenPreview {
        OnHandContent(
            orientation = LocalConfiguration.current.orientation,
            state = OnHandState(
                seasonalProducts = listOf(
                    GroupedProducts(
                        inventoryGroup = "invGroup 1",
                        antigen = "antigen 1",
                        products = listOf(
                            ProductUI(
                                productId = 0,
                                antigen = "antigen 1",
                                prettyName = "product 1",
                                onHand = 10,
                                isExpired = true,
                                presentationIcon = designSystemR.drawable.ic_presentation_syringe,
                            ),
                            ProductUI(
                                productId = 0,
                                antigen = "antigen 1",
                                prettyName = "product 2",
                                onHand = 10,
                                isExpired = false,
                                presentationIcon = designSystemR.drawable.ic_presentation_single_vial,
                            ),
                        ),
                        onHand = 10
                    ),
                    GroupedProducts(
                        inventoryGroup = "invGroup 2",
                        antigen = "antigen 2",
                        products = listOf(
                            ProductUI(
                                productId = 0,
                                antigen = "antigen 2",
                                prettyName = "product 1",
                                onHand = 10,
                                isExpired = true,
                                presentationIcon = designSystemR.drawable.ic_presentation_syringe,
                            ),
                        ),
                        onHand = 10
                    ),
                    GroupedProducts(
                        inventoryGroup = "invGroup 3",
                        antigen = "antigen 3",
                        products = listOf(
                            ProductUI(
                                productId = 0,
                                antigen = "antigen 3",
                                prettyName = "product 1",
                                onHand = 10,
                                isExpired = false,
                                presentationIcon = designSystemR.drawable.ic_presentation_syringe,
                            ),
                            ProductUI(
                                productId = 0,
                                antigen = "antigen 3",
                                prettyName = "product 2",
                                onHand = 10,
                                isExpired = true,
                                presentationIcon = designSystemR.drawable.ic_presentation_single_vial,
                            ),
                        ),
                        onHand = 10
                    ),
                ),
                nonSeasonalProducts = listOf(
                    GroupedProducts(
                        inventoryGroup = "invGroup 4",
                        antigen = "antigen 4",
                        products = listOf(
                            ProductUI(
                                productId = 0,
                                antigen = "antigen 4",
                                prettyName = "product 1",
                                onHand = 10,
                                isExpired = false,
                                presentationIcon = designSystemR.drawable.ic_presentation_syringe,
                            ),
                        ),
                        onHand = 10
                    ),
                    GroupedProducts(
                        inventoryGroup = "invGroup 5",
                        antigen = "antigen 5",
                        products = listOf(
                            ProductUI(
                                productId = 0,
                                antigen = "antigen 5",
                                prettyName = "product 1",
                                onHand = 10,
                                isExpired = true,
                                presentationIcon = designSystemR.drawable.ic_presentation_syringe,
                            ),
                        ),
                        onHand = 10
                    ),
                    GroupedProducts(
                        inventoryGroup = "invGroup 6",
                        antigen = "antigen 6",
                        products = listOf(
                            ProductUI(
                                productId = 0,
                                antigen = "antigen 6",
                                prettyName = "product 1",
                                onHand = 10,
                                isExpired = true,
                                presentationIcon = designSystemR.drawable.ic_presentation_syringe,
                            ),
                            ProductUI(
                                productId = 0,
                                antigen = "antigen 6",
                                prettyName = "product 2",
                                onHand = 10,
                                isExpired = false,
                                presentationIcon = designSystemR.drawable.ic_presentation_syringe,
                            ),
                        ),
                        onHand = 10
                    ),
                )
            ),
            lazyListState = rememberLazyListState(),
            onTabClick = {},
            onStockClick = {}
        )
    }
}

@FullDevicePreview
@Composable
private fun SingleItem() {
    FullscreenPreview {
        OnHandContent(
            orientation = LocalConfiguration.current.orientation,
            state = OnHandState(
                seasonalProducts = listOf(
                    GroupedProducts(
                        inventoryGroup = "invGroup 1",
                        antigen = "antigen 1",
                        products = listOf(
                            ProductUI(
                                productId = 0,
                                antigen = "antigen 1",
                                prettyName = "product 1",
                                onHand = 10,
                                isExpired = false,
                                presentationIcon = designSystemR.drawable.ic_presentation_syringe,
                            ),
                        ),
                        onHand = 10
                    ),
                ),
                nonSeasonalProducts = emptyList()
            ),
            lazyListState = rememberLazyListState(),
            onTabClick = {},
            onStockClick = {}
        )
    }
}

@FullDevicePreview
@Composable
private fun EmptyState() {
    FullscreenPreview {
        OnHandContent(
            orientation = LocalConfiguration.current.orientation,
            state = OnHandState(
                seasonalProducts = emptyList(),
                nonSeasonalProducts = emptyList(),
            ),
            lazyListState = rememberLazyListState(),
            onTabClick = {},
            onStockClick = {}
        )
    }
}
