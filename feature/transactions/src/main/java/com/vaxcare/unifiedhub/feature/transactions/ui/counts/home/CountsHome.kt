package com.vaxcare.unifiedhub.feature.transactions.ui.counts.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.core.ui.component.ButtonConfig
import com.vaxcare.unifiedhub.core.ui.component.LogoSpinnerWithExit
import com.vaxcare.unifiedhub.core.ui.component.NoInternetDialog
import com.vaxcare.unifiedhub.core.ui.component.VCBasicDialog
import com.vaxcare.unifiedhub.core.ui.component.VCScaffold
import com.vaxcare.unifiedhub.core.ui.component.button.ElevatedButton
import com.vaxcare.unifiedhub.core.ui.component.button.ElevatedIconButton
import com.vaxcare.unifiedhub.core.ui.component.button.VCFloatingActionButton
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.ProductCell
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.QuantityCell
import com.vaxcare.unifiedhub.core.ui.compose.LocalStock
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.compose.preview.LandscapePreview
import com.vaxcare.unifiedhub.core.ui.compose.preview.PortraitPreview
import com.vaxcare.unifiedhub.core.ui.ext.verticalFadingEdge
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.CountsHomeEvent.*
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.CountsHomeIntent.*
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.model.CountsSection
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.model.ProductUi
import kotlinx.coroutines.launch
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

private const val PRODUCT_QUANT_WIDTH_PORTRAIT = 471
private const val PRODUCT_QUANT_WIDTH_LANDSCAPE = 660

@Composable
fun CountsHomeScreen(
    navigateBack: () -> Unit,
    navigateToSubmit: () -> Unit,
    navigateToLotInteraction: () -> Unit,
    navigateToLotSearch: (Int) -> Unit,
    viewModel: CountsHomeViewModel = hiltViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    BaseMviScreen(
        viewModel = viewModel,
        onEvent = {
            when (it) {
                is NavigateBack -> navigateBack()
                is NavigateToSummary -> navigateToSubmit()
                is NavigateToLotInteraction -> navigateToLotInteraction()
                is NavigateToLotSearch -> navigateToLotSearch(it.sourceId)
                is ScrollToItem -> {
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(it.index)
                    }
                }
            }
        }
    ) { state, handleIntent ->
        val activeDialog = state.activeDialog

        when (activeDialog) {
            CountsHomeDialog.DiscardChanges -> {
                DiscardChangesDialog(
                    onDismiss = { handleIntent(DismissDialog) },
                    onDiscard = { handleIntent(DiscardChanges) }
                )
            }

            CountsHomeDialog.ActionRequired -> {
                ActionRequiredDialog(
                    onDismiss = {
                        handleIntent(DismissDialog)
                    },
                )
            }

            is CountsHomeDialog.NoInternet -> {
                NoInternetDialog(
                    onDismiss = {
                        handleIntent(DismissDialog)
                    },
                    onRetry = {
                        handleIntent(NoInternetTryAgain)
                    },
                    onGoToNetworkSettings = null
                )
            }
        }

        ProvideStock(state.stockType) {
            CountsHomeContent(
                state = state,
                orientation = LocalConfiguration.current.orientation,
                lazyListState = lazyListState,
                onBackClick = { handleIntent(CloseScreen) },
                onTabClick = { handleIntent(SelectSection(it)) },
                onEditProductClick = { handleIntent(EditProduct(it)) },
                onConfirmProductClick = { handleIntent(ConfirmProduct(it)) },
                onSearchClick = { handleIntent(SearchLots) },
                onNextClick = { handleIntent(ProceedToSummary) }
            )
        }
    }
}

@Composable
fun CountsHomeContent(
    state: CountsHomeState,
    orientation: Int,
    lazyListState: LazyListState,
    onBackClick: () -> Unit,
    onTabClick: (CountsSection) -> Unit,
    onEditProductClick: (ProductUi) -> Unit,
    onConfirmProductClick: (ProductUi) -> Unit,
    onSearchClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val seasonalProducts = state.seasonalProducts
    val nonSeasonalProducts = state.nonSeasonalProducts
    val isNextEnabled = !state.isLoading &&
        nonSeasonalProducts
            .plus(seasonalProducts)
            .fastAny { it.isConfirmed }

    VCScaffold(
        topBar = {
            BaseTitleBar(
                title = R.string.count_product,
                buttonIcon = DesignSystemR.drawable.ic_close,
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
        modifier = modifier
    ) {
        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                CountsHomeLandscape(
                    isLoading = state.isLoading,
                    stockName = state.stockType.prettyName,
                    seasonalProducts = seasonalProducts,
                    nonSeasonalProducts = nonSeasonalProducts,
                    lazyListState = lazyListState,
                    onTabClick = onTabClick,
                    onEditProductClick = onEditProductClick,
                    onConfirmProductClick = onConfirmProductClick,
                    onSearchClick = onSearchClick,
                )
            }

            Configuration.ORIENTATION_PORTRAIT -> {
                CountsHomePortrait(
                    isLoading = state.isLoading,
                    stockName = state.stockType.prettyName,
                    seasonalProducts = seasonalProducts,
                    nonSeasonalProducts = nonSeasonalProducts,
                    lazyListState = lazyListState,
                    onTabClick = onTabClick,
                    onEditProductClick = onEditProductClick,
                    onConfirmProductClick = onConfirmProductClick,
                    onSearchClick = onSearchClick,
                )
            }
        }
    }
}

@Composable
fun CountsHomeLandscape(
    isLoading: Boolean,
    stockName: String,
    seasonalProducts: List<ProductUi>,
    nonSeasonalProducts: List<ProductUi>,
    lazyListState: LazyListState,
    onTabClick: (CountsSection) -> Unit,
    onEditProductClick: (ProductUi) -> Unit,
    onConfirmProductClick: (ProductUi) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(horizontal = 40.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = measurement.spacing.medium, bottom = measurement.spacing.small)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = buildAnnotatedString {
                        pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                        append(stringResource(DesignSystemR.string.vaccines))
                        pop()
                        append(" $stockName")
                    },
                    style = type.headerTypeStyle.headlineMedium
                )

                AnimatedVisibility(
                    visible = seasonalProducts.isNotEmpty() && nonSeasonalProducts.isNotEmpty(),
                    enter = fadeIn(),
                    label = "SectionTabs"
                ) {
                    SectionTabs(
                        lazyListState = lazyListState,
                        seasonalHeaderIndex = nonSeasonalProducts.lastIndex + 1,
                        onTabClick = onTabClick,
                        modifier = Modifier.padding(start = 40.dp)
                    )
                }
            }
            ElevatedIconButton(
                onClick = onSearchClick,
                iconDrawRes = DesignSystemR.drawable.ic_search,
            )
        }

        ProductSheet(
            isLoading = isLoading,
            isLandscape = true,
            lazyListState = lazyListState,
            seasonalProducts = seasonalProducts,
            nonSeasonalProducts = nonSeasonalProducts,
            onEditProductClick = onEditProductClick,
            onConfirmProductClick = onConfirmProductClick
        )
    }
}

@Composable
fun CountsHomePortrait(
    isLoading: Boolean,
    stockName: String,
    seasonalProducts: List<ProductUi>,
    nonSeasonalProducts: List<ProductUi>,
    lazyListState: LazyListState,
    onTabClick: (CountsSection) -> Unit,
    onEditProductClick: (ProductUi) -> Unit,
    onConfirmProductClick: (ProductUi) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(horizontal = measurement.spacing.small)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp)
                .padding(vertical = measurement.spacing.medium)
        ) {
            Text(
                text = buildAnnotatedString {
                    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                    append(stringResource(DesignSystemR.string.vaccines))
                    pop()
                    append(" $stockName")
                },
                style = type.headerTypeStyle.headlineMedium
            )

            ElevatedIconButton(
                onClick = onSearchClick,
                iconDrawRes = DesignSystemR.drawable.ic_search,
            )
        }

        AnimatedVisibility(
            visible = seasonalProducts.isNotEmpty() && nonSeasonalProducts.isNotEmpty(),
            enter = expandVertically() + fadeIn(animationSpec = tween(delayMillis = 100)),
            label = "SectionTabs"
        ) {
            SectionTabs(
                lazyListState = lazyListState,
                seasonalHeaderIndex = nonSeasonalProducts.lastIndex + 1,
                onTabClick = onTabClick,
                modifier = Modifier.padding(
                    bottom = measurement.spacing.small,
                    start = measurement.spacing.medium
                )
            )
        }

        ProductSheet(
            isLoading = isLoading,
            isLandscape = false,
            lazyListState = lazyListState,
            seasonalProducts = seasonalProducts,
            nonSeasonalProducts = nonSeasonalProducts,
            onEditProductClick = onEditProductClick,
            onConfirmProductClick = onConfirmProductClick
        )
    }
}

@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun SectionTabs(
    lazyListState: LazyListState,
    seasonalHeaderIndex: Int,
    onTabClick: (CountsSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentTab = if (lazyListState.firstVisibleItemIndex < seasonalHeaderIndex) {
        CountsSection.NON_SEASONAL
    } else {
        CountsSection.SEASONAL
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
        CountsSection.entries.forEach {
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
                    .testTag(
                        TestTags.Counts.Home.sectionTab(stringResource(it.title))
                    )
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

@Composable
fun ProductSheet(
    isLoading: Boolean,
    isLandscape: Boolean,
    lazyListState: LazyListState,
    seasonalProducts: List<ProductUi>,
    nonSeasonalProducts: List<ProductUi>,
    onEditProductClick: (ProductUi) -> Unit,
    onConfirmProductClick: (ProductUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(
            topStart = measurement.radius.cardMedium,
            topEnd = measurement.radius.cardMedium,
        ),
        color = color.container.primaryContainer,
        modifier = modifier.fillMaxSize()
    ) {
        Column {
            ProductSheetHeader(isLandscape = isLandscape)

            Box(modifier = Modifier.fillMaxSize()) {
                var isAnimating by remember { mutableStateOf(true) }
                if (isAnimating) {
                    LogoSpinnerWithExit(
                        isLoading = isLoading,
                        onFinish = { isAnimating = false },
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.Center),
                    )
                }

                this@Column.AnimatedVisibility(visible = !isLoading) {
                    LazyColumn(
                        state = lazyListState,
                        contentPadding = PaddingValues(bottom = 256.dp),
                        modifier = Modifier
                            .verticalFadingEdge(lazyListState = lazyListState, 32.dp)
                            .fillMaxHeight()
                            .testTag(TestTags.Counts.Home.PRODUCT_SHEET_CONTAINER)
                    ) {
                        itemsIndexed(
                            items = nonSeasonalProducts,
                            key = { _, item -> item.productId }
                        ) { idx, item ->
                            ProductListItem(
                                isLandscape = isLandscape,
                                product = item,
                                onEditClick = { onEditProductClick(item) },
                                onConfirmClick = { onConfirmProductClick(item) },
                            )

                            if (idx != nonSeasonalProducts.lastIndex) {
                                HorizontalDivider(thickness = 0.5.dp)
                            }
                        }

                        if (nonSeasonalProducts.isNotEmpty() && seasonalProducts.isNotEmpty()) {
                            item {
                                ProductSectionHeader(
                                    title = stringResource(CountsSection.SEASONAL.title).uppercase(),
                                )
                            }
                        }

                        itemsIndexed(
                            items = seasonalProducts,
                            key = { _, item -> item.productId }
                        ) { idx, item ->
                            ProductListItem(
                                isLandscape = isLandscape,
                                product = item,
                                onEditClick = { onEditProductClick(item) },
                                onConfirmClick = { onConfirmProductClick(item) },
                            )

                            if (idx != seasonalProducts.lastIndex) {
                                HorizontalDivider(thickness = 0.5.dp)
                            }
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
            style = type.bodyTypeStyle.label
        )
        Text(
            text = stringResource(DesignSystemR.string.quantity).uppercase(),
            style = type.bodyTypeStyle.label
        )
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
            .testTag(TestTags.Counts.Home.sectionHeader(title))
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
    product: ProductUi,
    onEditClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = when {
        product.isConfirmed -> LocalStock.current.colors.containerLight
        product.isActionRequired() -> color.container.warningContainer
        else -> Color.Transparent
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .testTag(TestTags.Counts.Home.productItem(product.productId))
    ) {
        val (contentModifier, actionsModifier) = if (isLandscape) {
            Pair(
                first = Modifier.width(PRODUCT_QUANT_WIDTH_LANDSCAPE.dp),
                second = Modifier.padding(end = 180.dp)
            )
        } else {
            Pair(
                first = Modifier.width(PRODUCT_QUANT_WIDTH_PORTRAIT.dp),
                second = Modifier.padding(end = measurement.spacing.small)
            )
        }

        ProductContent(
            product = product,
            modifier = contentModifier
        )
        ProductActions(
            isConfirmed = product.isConfirmed,
            isActionRequired = product.isActionRequired(),
            onEditClick = onEditClick,
            onConfirmClick = onConfirmClick,
            modifier = actionsModifier,
            productId = product.productId
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
                .padding(vertical = measurement.spacing.small)
                .padding(start = measurement.spacing.small)
        ) {
            val topText = if (isActionRequired()) {
                stringResource(R.string.update_count)
            } else {
                null
            }
            ProductCell(
                prettyName = prettyName,
                antigenName = antigen,
                presentation = painterResource(presentationIcon),
                topText = topText,
                bottomText = getLotDetails(),
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(end = measurement.spacing.medium)
            )

            getQuantities().let { (initial, adjusted) ->
                QuantityCell(
                    initialQuantity = initial,
                    adjustedQuantity = adjusted,
                    testTag = TestTags.Counts.Home.productItemQuantity(product.productId)
                )
            }
        }
    }
}

@Composable
private fun ProductActions(
    productId: Int,
    isConfirmed: Boolean,
    isActionRequired: Boolean,
    onEditClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        ElevatedIconButton(
            onClick = onEditClick,
            modifier = Modifier
                .padding(end = measurement.spacing.small)
                .testTag(TestTags.Counts.Home.productItemEditButton(productId)),
            content = {
                Icon(
                    painter = painterResource(DesignSystemR.drawable.ic_edit),
                    contentDescription = null,
                )
            }
        )

        if (isConfirmed) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(112.dp, 48.dp)
            ) {
                Icon(
                    painter = painterResource(DesignSystemR.drawable.ic_check),
                    contentDescription = null,
                    tint = color.onContainer.onContainerPrimary,
                    modifier = Modifier.testTag(
                        TestTags.Counts.Home.productItemConfirmedIcon(
                            productId
                        )
                    )
                )
            }
        } else {
            ElevatedButton(
                onClick = onConfirmClick,
                enabled = !isActionRequired,
                text = stringResource(DesignSystemR.string.confirm),
                modifier = Modifier.testTag(TestTags.Counts.Home.productItemConfirmButton(productId))
            )
        }
    }
}

@Composable
private fun ActionRequiredDialog(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    VCBasicDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = stringResource(R.string.action_required),
        text = stringResource(R.string.action_required_description),
        primaryButtonConfig = ButtonConfig(
            text = stringResource(DesignSystemR.string.ok),
            onClick = onDismiss
        ),
    )
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

@PortraitPreview
@Composable
private fun ActionRequiredDialog() {
    DiscardChangesDialog({}, {})
}

@PortraitPreview
@Composable
private fun DiscardDialog() {
    ActionRequiredDialog({})
}

@FullDevicePreview
@Composable
private fun Default() {
    CountsHomeContent(
        state = CountsHomeSampleData.Default,
        orientation = LocalConfiguration.current.orientation,
        lazyListState = rememberLazyListState(),
        {},
        {},
        {},
        {},
        {},
        {}
    )
}

@FullDevicePreview
@Composable
private fun OnlySeasonal() {
    CountsHomeContent(
        state = CountsHomeSampleData.Default.copy(nonSeasonalProducts = listOf()),
        orientation = LocalConfiguration.current.orientation,
        lazyListState = rememberLazyListState(),
        {},
        {},
        {},
        {},
        {},
        {}
    )
}

@LandscapePreview
@Composable
private fun StockVFC() {
    ProvideStock(StockUi.VFC) {
        CountsHomeContent(
            state = CountsHomeSampleData.NoActionRequired,
            orientation = LocalConfiguration.current.orientation,
            lazyListState = rememberLazyListState(),
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
        CountsHomeContent(
            state = CountsHomeSampleData.NoActionRequired,
            orientation = LocalConfiguration.current.orientation,
            lazyListState = rememberLazyListState(),
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
        CountsHomeContent(
            state = CountsHomeSampleData.NoActionRequired,
            orientation = LocalConfiguration.current.orientation,
            lazyListState = rememberLazyListState(),
            {},
            {},
            {},
            {},
            {},
            {}
        )
    }
}

@FullDevicePreview
@Composable
private fun NoActionRequired() {
    CountsHomeContent(
        state = CountsHomeSampleData.NoActionRequired,
        orientation = LocalConfiguration.current.orientation,
        lazyListState = rememberLazyListState(),
        {},
        {},
        {},
        {},
        {},
        {}
    )
}
