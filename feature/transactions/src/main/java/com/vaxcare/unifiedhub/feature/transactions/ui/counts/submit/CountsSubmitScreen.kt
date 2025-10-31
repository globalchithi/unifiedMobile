package com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.core.ui.component.ButtonConfig
import com.vaxcare.unifiedhub.core.ui.component.LogoSpinner
import com.vaxcare.unifiedhub.core.ui.component.VCBasicDialog
import com.vaxcare.unifiedhub.core.ui.component.VCScaffold
import com.vaxcare.unifiedhub.core.ui.component.button.VCFloatingActionButton
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.ProductCell
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.QuantityCell
import com.vaxcare.unifiedhub.core.ui.compose.LocalStock
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.compose.preview.PreviewContainer
import com.vaxcare.unifiedhub.core.ui.ext.verticalFadingEdge
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.model.CountsSection
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.model.CountTotals
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitDialog.SubmissionFailed
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitEvent.NavigateBack
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitEvent.NavigateToConfirmation
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitIntent.ConfirmCount
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitIntent.DismissDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitIntent.GoBack
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitIntent.RetrySubmit
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.model.ProductUi
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

private val COL_WIDTHS_PORTRAIT = intArrayOf(
    276, // Product
    140, // Quantity
    140, // Adjustment
    144 // Impact
)
private val COL_WIDTHS_PORTRAIT_NO_IMPACT = intArrayOf(
    412, // Product
    144, // Quantity
    144, // Adjustment
    0 // Impact
)
private val COL_WIDTHS_LANDSCAPE = intArrayOf(
    408, // Product
    176, // Quantity
    176, // Adjustment
    176 // Impact
)
private val COL_WIDTHS_LANDSCAPE_NO_IMPACT = intArrayOf(
    584, // Product
    176, // Quantity
    176, // Adjustment
    0 // Impact
)

@Composable
fun CountsSubmit(
    viewModel: CountsSubmitViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToConfirmation: (CountTotals) -> Unit,
) {
    BaseMviScreen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                is NavigateBack -> {
                    navigateBack()
                }

                is NavigateToConfirmation -> {
                    navigateToConfirmation(event.totals)
                }
            }
        }
    ) { state, handleIntent ->

        when (state.activeDialog) {
            SubmissionFailed -> {
                SubmissionFailedDialog(
                    onDismiss = { handleIntent(DismissDialog) },
                    onRetry = { handleIntent(RetrySubmit) }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            CountsSubmitScreen(state, handleIntent)

            CountsSubmitLoading(state.isLoading)
        }
    }
}

@Composable
fun CountsSubmitScreen(state: CountsSubmitState, handleIntent: (CountsSubmitIntent) -> Unit) {
    ProvideStock(state.stockType) {
        VCScaffold(
            modifier = Modifier.testTag(TestTags.Counts.Submit.CONTAINER),
            topBar = {
                BaseTitleBar(
                    title = R.string.screen_submit_count,
                    buttonIcon = DesignSystemR.drawable.ic_chevron_left,
                    onButtonClick = {
                        handleIntent(GoBack)
                    }
                )
            },
            fab = {
                VCFloatingActionButton(
                    onClick = {
                        handleIntent(ConfirmCount)
                    },
                    iconPainter = painterResource(DesignSystemR.drawable.ic_check),
                    modifier = Modifier.testTag(TestTags.Counts.Submit.CONFIRM_BUTTON),
                )
            },
        ) {
            CountsSubmitContent(
                orientation = LocalConfiguration.current.orientation,
                state = state,
            )
        }
    }
}

@Composable
fun CountsSubmitLoading(isLoading: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color.container.primaryContainer)
                .pointerInput(Unit) { } // Consume all input
        ) {
            LogoSpinner(
                modifier = Modifier
                    .size(80.dp)
                    .testTag(TestTags.Counts.Submit.LOADING_SPINNER)
            )

            Text(
                text = stringResource(R.string.loading_screen_message),
                style = type.bodyTypeStyle.body3Bold,
                modifier = Modifier.padding(top = measurement.spacing.large)
            )
        }
    }
}

@Composable
private fun CountsSubmitContent(
    orientation: Int,
    state: CountsSubmitState,
    modifier: Modifier = Modifier
) {
    when (orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            CountsSubmitPortrait(
                showImpact = state.showImpact,
                stockType = state.stockType,
                nonSeasonalProducts = state.nonSeasonalProducts,
                seasonalProducts = state.seasonalProducts,
                subTotal = state.subTotal,
                modifier = modifier,
            )
        }

        Configuration.ORIENTATION_LANDSCAPE -> {
            CountsSubmitLandscape(
                showImpact = state.showImpact,
                stockType = state.stockType,
                nonSeasonalProducts = state.nonSeasonalProducts,
                seasonalProducts = state.seasonalProducts,
                subTotal = state.subTotal,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun CountsSubmitPortrait(
    showImpact: Boolean,
    stockType: StockUi,
    nonSeasonalProducts: List<ProductUi>,
    seasonalProducts: List<ProductUi>,
    subTotal: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = measurement.spacing.small)
    ) {
        ScreenHeaderPortrait(
            stockType = stockType,
            modifier = Modifier.padding(
                start = 40.dp,
                top = measurement.spacing.medium,
                bottom = measurement.spacing.medium
            )
        )

        ProductSheet(
            showImpact = showImpact,
            nonSeasonalProducts = nonSeasonalProducts,
            seasonalProducts = seasonalProducts,
            columns = if (showImpact) COL_WIDTHS_PORTRAIT else COL_WIDTHS_PORTRAIT_NO_IMPACT,
            bottomPanel = {
                if (showImpact) SummaryBottomPanelPortrait(subTotal)
            },
            modifier = Modifier.width(measurement.size.cardLarge)
        )
    }
}

@Composable
private fun CountsSubmitLandscape(
    showImpact: Boolean,
    stockType: StockUi,
    nonSeasonalProducts: List<ProductUi>,
    seasonalProducts: List<ProductUi>,
    subTotal: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 40.dp)
    ) {
        ScreenHeaderLandscape(
            stockType = stockType,
            modifier = Modifier.padding(
                top = measurement.spacing.medium,
                bottom = measurement.spacing.small
            )
        )

        ProductSheet(
            showImpact = showImpact,
            nonSeasonalProducts = nonSeasonalProducts,
            seasonalProducts = seasonalProducts,
            columns = if (showImpact) COL_WIDTHS_LANDSCAPE else COL_WIDTHS_LANDSCAPE_NO_IMPACT,
            bottomPanel = {
                if (showImpact) SummaryBottomPanelLandscape(subTotal)
            },
            modifier = Modifier.width(1104.dp)
        )
    }
}

@Composable
private fun ScreenHeaderPortrait(stockType: StockUi, modifier: Modifier = Modifier) {
    Column(modifier) {
        InventoryText(
            stockType = stockType,
            modifier = Modifier.padding(bottom = measurement.spacing.xSmall)
        )

        HeaderDescription()
    }
}

@Composable
private fun ScreenHeaderLandscape(stockType: StockUi, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        InventoryText(
            stockType = stockType,
            modifier = Modifier.padding(end = measurement.spacing.large)
        )

        HeaderDescription()
    }
}

@Composable
private fun InventoryText(stockType: StockUi, modifier: Modifier = Modifier) {
    Text(
        text = buildAnnotatedString {
            pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
            append(stringResource(DesignSystemR.string.vaccines))
            pop()
            append(" ${stockType.prettyName}")
        },
        style = type.headerTypeStyle.headlineMedium,
        modifier = modifier
    )
}

@Composable
private fun HeaderDescription(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.header_subtext_confirmed_products),
        style = type.bodyTypeStyle.body4,
        modifier = modifier
    )
}

@Composable
private fun ProductSheet(
    showImpact: Boolean,
    nonSeasonalProducts: List<ProductUi>,
    seasonalProducts: List<ProductUi>,
    columns: IntArray,
    bottomPanel: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(
            topStart = measurement.radius.cardMedium,
            topEnd = measurement.radius.cardMedium,
        ),
        color = color.container.primaryContainer,
        modifier = modifier
    ) {
        Column {
            ProductSheetHeader(showImpact, columns, modifier)

            val lazyListState = rememberLazyListState()
            val isLandscape =
                LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            LazyColumn(
                state = lazyListState,
                contentPadding = if (isLandscape) {
                    PaddingValues(bottom = 66.dp)
                } else {
                    PaddingValues(0.dp)
                },
                modifier = Modifier
                    .verticalFadingEdge(lazyListState = lazyListState, height = 32.dp)
                    .weight(1f)
                    .testTag(TestTags.Counts.Submit.PRODUCT_LIST)
            ) {
                val showSectionHeaders =
                    nonSeasonalProducts.isNotEmpty() && seasonalProducts.isNotEmpty()

                if (showSectionHeaders) {
                    item {
                        ProductSectionHeader(
                            title = stringResource(CountsSection.NON_SEASONAL.title).uppercase(),
                        )
                    }
                }

                itemsIndexed(items = nonSeasonalProducts) { idx, product ->
                    ProductListItem(showImpact, product, columns)

                    if (idx != nonSeasonalProducts.lastIndex) {
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }

                if (showSectionHeaders) {
                    item {
                        ProductSectionHeader(
                            title = stringResource(CountsSection.SEASONAL.title).uppercase(),
                        )
                    }
                }

                itemsIndexed(items = seasonalProducts) { idx, product ->
                    ProductListItem(showImpact, product, columns)

                    if (idx != nonSeasonalProducts.lastIndex) {
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }

            bottomPanel()
        }
    }
}

@Composable
private fun ProductSheetHeader(
    showImpact: Boolean,
    columns: IntArray,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(40.dp)
            .padding(
                start = measurement.spacing.small,
                end = measurement.spacing.medium,
                top = measurement.spacing.xSmall,
            )
    ) {
        Text(
            text = stringResource(DesignSystemR.string.product).uppercase(),
            style = type.bodyTypeStyle.label,
            modifier = Modifier
                .width(columns[0].dp)
                .padding(start = 32.dp)
        )
        Text(
            text = stringResource(DesignSystemR.string.quantity).uppercase(),
            style = type.bodyTypeStyle.label,
            textAlign = TextAlign.Right,
            modifier = Modifier.width(columns[1].dp)
        )
        Text(
            text = stringResource(R.string.adjustment).uppercase(),
            style = type.bodyTypeStyle.label,
            textAlign = TextAlign.Right,
            modifier = Modifier.width(columns[2].dp)
        )
        if (showImpact) {
            Text(
                text = stringResource(R.string.impact).uppercase(),
                style = type.bodyTypeStyle.label,
                textAlign = TextAlign.Right,
                modifier = Modifier.width(columns[3].dp)
            )
        }
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
    showImpact: Boolean,
    product: ProductUi,
    columns: IntArray,
    modifier: Modifier = Modifier,
) {
    with(product) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .padding(start = measurement.spacing.small, end = measurement.spacing.medium)
                .heightIn(min = 80.dp)
                .testTag(TestTags.Counts.Submit.productItem(product.id))
        ) {
            ProductCell(
                prettyName = prettyName,
                antigenName = antigen,
                presentation = painterResource(presentationIcon),
                modifier = Modifier
                    .width(columns[0].dp)
                    .testTag(TestTags.Counts.Submit.productItemName(id))
            )

            val adjustedQuantity = if (delta != 0) {
                quantity + delta
            } else {
                null
            }
            if (showImpact) {
                QuantityCell(
                    initialQuantity = quantity,
                    adjustedQuantity = adjustedQuantity,
                    unitPrice = "$unitPrice ea.",
                    modifier = Modifier.width(columns[1].dp),
                    testTag = TestTags.Counts.Submit.productItemQuantity(id)
                )
            } else {
                QuantityCell(
                    initialQuantity = quantity,
                    adjustedQuantity = adjustedQuantity,
                    modifier = Modifier.width(columns[1].dp),
                    testTag = TestTags.Counts.Submit.productItemQuantity(id)
                )
            }

            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier.width(columns[2].dp)
            ) {
                Text(
                    modifier = Modifier.testTag(TestTags.Counts.Submit.productItemAdjustment(id)),
                    text = delta.toString(),
                    style = type.bodyTypeStyle.body3Bold
                )
            }

            if (showImpact) {
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.width(columns[3].dp)
                ) {
                    Text(
                        modifier = Modifier.testTag(TestTags.Counts.Submit.productItemImpact(id)),
                        text = impact,
                        style = type.bodyTypeStyle.body3
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryBottomPanelPortrait(subTotal: String, modifier: Modifier = Modifier) {
    Column(modifier.padding(measurement.spacing.medium)) {
        SubTotal(
            subTotal = subTotal,
            modifier = Modifier.padding(
                start = measurement.spacing.xSmall,
                bottom = measurement.spacing.xSmall
            )
        )

        InvoiceWarningCard()
    }
}

@Composable
private fun SummaryBottomPanelLandscape(subTotal: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(measurement.spacing.small),
    ) {
        InvoiceWarningCard(
            modifier = Modifier.padding(end = measurement.spacing.large)
        )

        SubTotal(subTotal)
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun SubTotal(subTotal: String, modifier: Modifier = Modifier) {
    Text(
        text = buildAnnotatedString {
            // Sub-Total:
            append("${stringResource(DesignSystemR.string.subtotal)}: ")

            pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
            append(subTotal)
        },
        style = type.headerTypeStyle.headlineMedium,
        modifier = modifier.testTag(TestTags.Counts.Submit.SUBTOTAL_LABEL)
    )
}

@Composable
private fun InvoiceWarningCard(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.container.inversePrimary,
        modifier = modifier
            .size(455.dp, 124.dp)
            .testTag(TestTags.Counts.Submit.INVOICE_WARNING_CARD)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = measurement.spacing.medium,
                vertical = measurement.spacing.small
            )
        ) {
            Text(
                text = stringResource(R.string.bottom_panel_header),
                style = type.bodyTypeStyle.body4Bold,
                modifier = Modifier.padding(bottom = measurement.spacing.xSmall)
            )

            Text(
                text = stringResource(R.string.bottom_panel_description),
                style = type.bodyTypeStyle.body6
            )
        }
    }
}

@Composable
fun SubmissionFailedDialog(
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    VCBasicDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = stringResource(R.string.dialog_title),
        text = stringResource(R.string.dialog_description),
        primaryButtonConfig = ButtonConfig(
            text = stringResource(DesignSystemR.string.retry),
            onClick = onRetry,
            testTag = TestTags.Counts.Submit.SUBMISSION_FAILED_DIALOG_RETRY_BUTTON
        ),
        secondaryButtonConfig = ButtonConfig(
            text = stringResource(DesignSystemR.string.cancel),
            onClick = onDismiss,
            testTag = TestTags.Counts.Submit.SUBMISSION_FAILED_DIALOG_CANCEL_BUTTON
        )
    )
}

@FullDevicePreview
@Composable
private fun Default() {
    PreviewContainer {
        CountsSubmitScreen(
            state = CountsSubmitSampleData.Default,
            handleIntent = {}
        )
    }
}

@FullDevicePreview
@Composable
private fun PublicStock() {
    PreviewContainer {
        CountsSubmitScreen(
            state = CountsSubmitSampleData.PublicStock,
            handleIntent = {}
        )
    }
}
