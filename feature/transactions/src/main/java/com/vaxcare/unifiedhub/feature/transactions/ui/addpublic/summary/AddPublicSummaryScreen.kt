package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.summary

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.vaxcare.unifiedhub.core.ui.component.LogoSpinner
import com.vaxcare.unifiedhub.core.ui.component.VCBasicDialog
import com.vaxcare.unifiedhub.core.ui.component.VCScaffold
import com.vaxcare.unifiedhub.core.ui.component.button.VCFloatingActionButton
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.ProductCell
import com.vaxcare.unifiedhub.core.ui.compose.LocalStock
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.compose.preview.PreviewContainer
import com.vaxcare.unifiedhub.core.ui.ext.verticalFadingEdge
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.summary.AddPublicSummaryDialog.SubmissionFailed
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.summary.AddPublicSummaryIntent.DismissDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.summary.AddPublicSummaryIntent.GoBack
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.summary.AddPublicSummaryIntent.RetrySubmission
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.summary.AddPublicSummaryIntent.SubmitAddPublic
import kotlin.collections.isNotEmpty
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

private const val PRODUCT_QUANT_WIDTH_PORTRAIT = 528
private const val PRODUCT_QUANT_WIDTH_LANDSCAPE = 688

@Composable
fun AddPublicSummary(
    navigateBack: () -> Unit,
    navigateToComplete: () -> Unit,
    viewModel: AddPublicSummaryViewModel = hiltViewModel(),
) {
    BaseMviScreen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                AddPublicSummaryEvent.NavigateBack -> navigateBack()
                AddPublicSummaryEvent.NavigateToAddPublicCompleted -> navigateToComplete()
            }
        }
    ) { state, handleIntent ->
        when (state.activeDialog) {
            SubmissionFailed ->
                SubmissionFailedDialog(
                    onDismiss = { handleIntent(DismissDialog) },
                    onRetry = { handleIntent(RetrySubmission) }
                )
        }

        Box(Modifier.fillMaxSize()) {
            AddPublicSummaryScreen(state, handleIntent)
            AddPublicLoading(state.isLoading)
        }
    }
}

@Composable
private fun AddPublicSummaryScreen(state: AddPublicSummaryState, handleIntent: (AddPublicSummaryIntent) -> Unit) {
    ProvideStock(state.stock) {
        AddPublicSummaryContent(
            state = state,
            orientation = LocalConfiguration.current.orientation,
            onBackClick = { handleIntent(GoBack) },
            onNextClick = { handleIntent(SubmitAddPublic) }
        )
    }
}

@Composable
private fun AddPublicSummaryContent(
    state: AddPublicSummaryState,
    orientation: Int,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val products = state.products

    VCScaffold(
        topBar = {
            BaseTitleBar(
                modifier = modifier,
                title = R.string.add_public_summary_title,
                buttonIcon = DesignSystemR.drawable.ic_chevron_left,
                onButtonClick = onBackClick
            )
        },
        fab = {
            VCFloatingActionButton(
                onClick = onNextClick,
                iconPainter = painterResource(DesignSystemR.drawable.ic_check),
            )
        },
    ) {
        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                AddPublicSummaryLandscape(
                    total = state.total,
                    stock = state.stock,
                    products = products,
                )
            }

            Configuration.ORIENTATION_PORTRAIT -> {
                AddPublicSummaryPortrait(
                    total = state.total,
                    stock = state.stock,
                    products = products,
                )
            }
        }
    }
}

@Composable
private fun AddPublicSummaryLandscape(
    total: Int,
    stock: StockUi,
    products: List<ProductUi>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = measurement.spacing.large)
            .padding(
                top = measurement.spacing.topBarSmallY,
                bottom = 18.dp
            )
    ) {
        ProductSheet(
            isLandscape = true,
            products = products,
            modifier = Modifier.weight(1f)
        )
        DestinationAndTotalFooter(
            isLandscape = true,
            total = total,
            stock = stock,
            modifier = Modifier.padding(top = measurement.spacing.xSmall)
        )
        DisclaimerFooter(
            stock = stock,
            modifier = Modifier.padding(top = measurement.spacing.xSmall)
        )
    }
}

@Composable
private fun AddPublicSummaryPortrait(
    total: Int,
    stock: StockUi,
    products: List<ProductUi>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = measurement.spacing.small)
            .padding(
                bottom = 38.dp,
                top = measurement.spacing.topBarSmallY
            )
    ) {
        ProductSheet(
            isLandscape = false,
            products = products,
            modifier = modifier.weight(1f)
        )
        DestinationAndTotalFooter(
            isLandscape = false,
            total = total,
            stock = stock,
            modifier = Modifier.padding(top = measurement.spacing.xSmall)
        )
        DisclaimerFooter(
            stock = stock,
            modifier = Modifier.padding(top = measurement.spacing.xSmall)
        )
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
        Box(Modifier.fillMaxWidth()) {
            val width = if (isLandscape) {
                PRODUCT_QUANT_WIDTH_LANDSCAPE
            } else {
                PRODUCT_QUANT_WIDTH_PORTRAIT
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .requiredSize(width = width.dp, height = 104.dp)
                    .padding(
                        start = measurement.spacing.large,
                        top = measurement.spacing.small,
                        bottom = measurement.spacing.small
                    )
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(measurement.spacing.xSmall),
                    modifier = Modifier.padding(end = measurement.spacing.medium)
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
                        style = type.bodyTypeStyle.label,
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
}

@Composable
private fun ProductSheet(
    isLandscape: Boolean,
    products: List<ProductUi>,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(
            size = measurement.radius.cardMedium,
        ),
        color = color.container.primaryContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            if (products.isNotEmpty()) {
                ProductSheetHeader(isLandscape = isLandscape)

                val lazyListState = rememberLazyListState()
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .verticalFadingEdge(lazyListState = lazyListState, 32.dp)
                ) {
                    itemsIndexed(
                        items = products,
                        key = { _, item -> item.id }
                    ) { i, item ->
                        ProductListItem(
                            isLandscape = isLandscape,
                            product = item,
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
            .padding(top = 8.dp)
            .height(40.dp)
            .width(headerWidth.dp)
            .padding(start = 48.dp)
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
    HorizontalDivider(
        color = color.outline.twoHundred,
        thickness = 2.dp
    )
}

@Composable
private fun ProductListItem(
    isLandscape: Boolean,
    product: ProductUi,
    modifier: Modifier = Modifier,
) {
    with(product) {
        val width = if (isLandscape) {
            PRODUCT_QUANT_WIDTH_LANDSCAPE
        } else {
            PRODUCT_QUANT_WIDTH_PORTRAIT
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .size(width = width.dp, height = 80.dp)
        ) {
            with(product) {
                ProductCell(
                    prettyName = product.prettyName,
                    antigenName = product.antigen,
                    presentation = painterResource(Icons.presentationIcon(presentation)),
                    bottomText = getLotDetails(),
                    topText = null,
                    modifier = Modifier
                        .padding(horizontal = measurement.spacing.small)
                        .weight(1f, fill = false)
                )

                Text(
                    text = getTotal().toString(),
                    style = type.bodyTypeStyle.body3Bold,
                )
            }
        }
    }
}

@Composable
private fun AddPublicLoading(isLoading: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color.container.primaryContainer)
                .pointerInput(Unit) { } // Consume all input
        ) {
            LogoSpinner(modifier = Modifier.size(80.dp))
        }
    }
}

@Composable
private fun SubmissionFailedDialog(
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    VCBasicDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = stringResource(R.string.dialog_title),
        text = stringResource(R.string.dialog_internet_required_adjustment_description),
        primaryButtonConfig = ButtonConfig(
            text = stringResource(DesignSystemR.string.retry),
            onClick = onRetry
        ),
        secondaryButtonConfig = ButtonConfig(
            text = stringResource(DesignSystemR.string.cancel),
            onClick = onDismiss
        )
    )
}

@Composable
private fun DisclaimerFooter(modifier: Modifier = Modifier, stock: StockUi) {
    Surface(
        shape = RoundedCornerShape(measurement.radius.cardMedium),
        color = LocalStock.current.colors.containerLight,
        modifier = modifier
            .height(104.dp)
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(measurement.spacing.xSmall),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(vertical = measurement.spacing.small)
                .padding(start = measurement.spacing.large, end = 184.dp)
        ) {
            Text(
                text = stringResource(R.string.log_waste_summary_footer_confirmation),
                style = type.bodyTypeStyle.label
            )

            Text(
                stringResource(R.string.add_public_summary_disclaimer, stock.prettyName),
                style = type.bodyTypeStyle.body6
            )
        }
    }
}

@FullDevicePreview
@Composable
fun ForStockVFC() {
    PreviewContainer {
        AddPublicSummaryScreen(
            state = AddPublicSummaryState(
                products = ProductUi.Sample,
                total = 1,
                stock = StockUi.VFC
            ),
            handleIntent = {},
        )
    }
}

@FullDevicePreview
@Composable
fun ForStockState() {
    PreviewContainer {
        AddPublicSummaryScreen(
            state = AddPublicSummaryState(
                products = ProductUi.Sample,
                total = 1,
                stock = StockUi.STATE
            ),
            handleIntent = {},
        )
    }
}
