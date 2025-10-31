package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.core.ui.component.ButtonConfig
import com.vaxcare.unifiedhub.core.ui.component.LogoSpinner
import com.vaxcare.unifiedhub.core.ui.component.VCBasicDialog
import com.vaxcare.unifiedhub.core.ui.component.VCScaffold
import com.vaxcare.unifiedhub.core.ui.component.button.VCFloatingActionButton
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.QuantityCell
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.ProductInfo
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.ProductInfoUi
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.SubtitleLine
import com.vaxcare.unifiedhub.core.ui.compose.LocalStock
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.compose.preview.PreviewContainer
import com.vaxcare.unifiedhub.core.ui.ext.verticalFadingEdge
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.BROKEN_OR_CONTAMINATED
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.DELIVER_OUT_OF_TEMP
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.EXPIRED
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.OTHER
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.PREPPED_AND_NOT_ADMINISTERED
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.SPOILED_OR_OUT_OF_RANGE
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.LogWasteSummaryDialog.SubmissionFailed
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.LogWasteSummaryIntent.DismissDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.LogWasteSummaryIntent.GoBack
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.LogWasteSummaryIntent.RetrySubmission
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.LogWasteSummaryIntent.SubmitLogWaste
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.model.ProductUi
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

private val COL_WIDTHS_PORTRAIT = intArrayOf(
    312, // Product
    176, // Quantity
    216 // Value
)
private val COL_WIDTHS_LANDSCAPE_WITH_VALUE = intArrayOf(
    408, // Product
    304, // Quantity
    220 // Value
)
private val COL_WIDTHS_LANDSCAPE_NO_VALUE = intArrayOf(
    472, // Product
    232, // Quantity
)

@Composable
fun LogWasteSummary(
    navigateBack: () -> Unit,
    navigateToComplete: () -> Unit,
    viewModel: LogWasteSummaryViewModel = hiltViewModel(),
) {
    BaseMviScreen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                LogWasteSummaryEvent.NavigateBack -> navigateBack()
                LogWasteSummaryEvent.NavigateToLogWasteCompleted -> navigateToComplete()
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
            LogWasteSummaryScreen(state, handleIntent)
            LogWasteLoading(state.isLoading)
        }
    }
}

@Composable
fun LogWasteSummaryScreen(state: LogWasteSummaryState, handleIntent: (LogWasteSummaryIntent) -> Unit) {
    ProvideStock(state.stock) {
        VCScaffold(
            topBar = {
                BaseTitleBar(
                    title = stringResource(R.string.log_waste_summary_top_bar_title),
                    buttonIcon = DesignSystemR.drawable.ic_chevron_left,
                    onButtonClick = { handleIntent(GoBack) }
                )
            },
            fab = {
                VCFloatingActionButton(
                    onClick = {
                        handleIntent(SubmitLogWaste)
                    },
                    iconPainter = painterResource(DesignSystemR.drawable.ic_check)
                )
            }
        ) {
            LogWasteSummaryContent(
                orientation = LocalConfiguration.current.orientation,
                state = state
            )
        }
    }
}

@Composable
fun LogWasteSummaryContent(orientation: Int, state: LogWasteSummaryState) {
    when (orientation) {
        Configuration.ORIENTATION_PORTRAIT -> LogWasteSummaryPortrait(state)
        else -> LogWasteSummaryLandscape(state)
    }
}

@Composable
fun LogWasteSummaryPortrait(state: LogWasteSummaryState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(
                vertical = measurement.spacing.small,
                horizontal = 17.dp
            )
    ) {
        Surface(
            shape = RoundedCornerShape(measurement.radius.cardMedium),
            color = color.container.primaryContainer,
            modifier = modifier
                .height(800.dp)
        ) {
            Column {
                ProductSheetHeader(
                    columns = COL_WIDTHS_PORTRAIT.let {
                        if (state.stock != StockUi.PRIVATE) {
                            intArrayOf(
                                COL_WIDTHS_PORTRAIT[0],
                                COL_WIDTHS_PORTRAIT[1] + 56,
                                COL_WIDTHS_PORTRAIT[2]
                            )
                        } else {
                            it
                        }
                    },
                    showValue = state.stock == StockUi.PRIVATE
                )

                val lazyListState = rememberLazyListState()

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .verticalFadingEdge(lazyListState = lazyListState, height = 32.dp)
                ) {
                    itemsIndexed(state.products) { i, item ->
                        LogWasteSummaryItem(
                            productUi = item,
                            columns = COL_WIDTHS_PORTRAIT.let {
                                if (state.stock != StockUi.PRIVATE) {
                                    intArrayOf(
                                        COL_WIDTHS_PORTRAIT[0],
                                        COL_WIDTHS_PORTRAIT[1] + 56,
                                        COL_WIDTHS_PORTRAIT[2]
                                    )
                                } else {
                                    it
                                }
                            },
                            showValue = state.stock == StockUi.PRIVATE
                        )

                        HorizontalDivider(
                            color = color.outline.twoHundred,
                            thickness = 2.dp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(measurement.spacing.xSmall))

        ReasonAndTotalFooter(
            reason = state.reason,
            total = state.total,
            extraEndPadding = false,
            stockName = state.stock.prettyName,
            modifier = Modifier
                .width(measurement.size.cardLarge)
                .height(104.dp)
        )

        Spacer(Modifier.height(measurement.spacing.xSmall))

        DisclaimerFooter(
            modifier = Modifier
                .width(measurement.size.cardLarge)
                .height(104.dp),
            state.stock
        )
    }
}

@Composable
fun LogWasteSummaryLandscape(state: LogWasteSummaryState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(
                vertical = measurement.spacing.small,
                horizontal = 40.dp
            )
    ) {
        Surface(
            shape = RoundedCornerShape(measurement.radius.cardMedium),
            color = color.container.primaryContainer,
            modifier = modifier
                .height(424.dp)
        ) {
            Column {
                val showValue = state.stock == StockUi.PRIVATE

                ProductSheetHeader(
                    columns = if (showValue) {
                        COL_WIDTHS_LANDSCAPE_WITH_VALUE
                    } else {
                        COL_WIDTHS_LANDSCAPE_NO_VALUE
                    },
                    showValue = showValue
                )

                val lazyListState = rememberLazyListState()

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .verticalFadingEdge(lazyListState = lazyListState, height = 32.dp)
                ) {
                    itemsIndexed(state.products) { i, item ->
                        LogWasteSummaryItem(
                            productUi = item,
                            columns = if (showValue) {
                                COL_WIDTHS_LANDSCAPE_WITH_VALUE
                            } else {
                                COL_WIDTHS_LANDSCAPE_NO_VALUE
                            },
                            showValue = showValue
                        )

                        HorizontalDivider(
                            color = color.outline.twoHundred,
                            thickness = 2.dp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(measurement.spacing.xSmall))
        ReasonAndTotalFooter(
            reason = state.reason,
            total = state.total,
            stockName = state.stock.prettyName,
            extraEndPadding = state.stock != StockUi.PRIVATE,
            modifier = Modifier
                .width(1102.dp)
                .height(104.dp)
        )

        Spacer(Modifier.height(measurement.spacing.xSmall))

        DisclaimerFooter(
            modifier = Modifier
                .width(1102.dp)
                .height(104.dp),
            state.stock
        )
    }
}

@Composable
private fun LogWasteSummaryItem(
    productUi: ProductUi,
    columns: IntArray,
    showValue: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(80.dp)
    ) {
        ProductInfo(
            info = ProductInfoUi(
                presentation = Presentation.PREFILLED_SYRINGE,
                mainTextBold = productUi.antigen,
                mainTextRegular = productUi.prettyName,
                subtitleLines = listOf(
                    SubtitleLine(
                        text = stringResource(
                            R.string.product_lot_lot_number,
                            productUi.lotsPreview
                        )
                    )
                )
            ),
            modifier = Modifier
                .width(columns[0].dp)
                .padding(start = measurement.spacing.small)
        )

        if (showValue) {
            QuantityCell(
                initialQuantity = productUi.quantity,
                unitPrice = productUi.unitPrice,
                modifier = Modifier.width(columns[1].dp)
            )
        } else {
            QuantityCell(
                initialQuantity = productUi.quantity,
                modifier = Modifier.width(columns[1].dp)
            )
        }

        if (showValue) {
            Text(
                text = productUi.value,
                style = type.bodyTypeStyle.body3.copy(textAlign = TextAlign.End),
                modifier = Modifier
                    .width(columns[2].dp)
                    .padding(end = measurement.spacing.medium)
            )
        }
    }
}

@Composable
fun LogWasteLoading(isLoading: Boolean, modifier: Modifier = Modifier) {
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
private fun ProductSheetHeader(
    columns: IntArray,
    showValue: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.BottomStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .width(1102.dp)
                .height(40.dp)
                .padding(top = 8.dp)
                .padding(vertical = measurement.spacing.xSmall)
        ) {
            Text(
                text = stringResource(R.string.log_waste_summary_header_product),
                style = type.bodyTypeStyle.label,
                modifier = Modifier
                    .width(columns[0].dp)
                    .padding(start = 48.dp)
            )
            Text(
                text = stringResource(R.string.log_waste_summary_header_quantity),
                style = type.bodyTypeStyle.label,
                textAlign = TextAlign.Right,
                modifier = Modifier.width(columns[1].dp)
            )

            if (showValue) {
                Text(
                    text = stringResource(R.string.log_waste_summary_header_value),
                    style = type.bodyTypeStyle.label,
                    textAlign = TextAlign.Right,
                    modifier = Modifier
                        .width(columns[2].dp)
                        .padding(end = measurement.spacing.medium)
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
fun SubmissionFailedDialog(
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
fun DisclaimerFooter(modifier: Modifier = Modifier, stock: StockUi) {
    Surface(
        shape = RoundedCornerShape(measurement.radius.cardMedium),
        color = LocalStock.current.colors.containerLight,
        modifier = modifier
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

            val disclaimerText =
                if (stock == StockUi.PRIVATE) {
                    stringResource(R.string.log_waste_summary_footer_confirmation_description_private)
                } else {
                    stringResource(
                        R.string.log_waste_summary_footer_confirmation_description_public_fmt,
                        stock.prettyName
                    )
                }

            Text(
                disclaimerText,
                style = type.bodyTypeStyle.body6
            )
        }
    }
}

@Composable
private fun ReasonAndTotalFooter(
    reason: LogWasteReason,
    total: String,
    stockName: String,
    extraEndPadding: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(measurement.radius.cardMedium),
        color = color.container.primaryContainer,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = measurement.spacing.small)
                .padding(
                    start = measurement.spacing.large,
                    end = if (extraEndPadding) 398.dp else 194.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(measurement.spacing.xSmall),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(R.string.reason),
                    style = type.bodyTypeStyle.label
                )

                val reasonResourceId = remember(reason) {
                    when (reason) {
                        SPOILED_OR_OUT_OF_RANGE -> R.string.log_waste_reason_spoiled_or_out_of_range
                        BROKEN_OR_CONTAMINATED -> R.string.log_waste_reason_broken_or_contaminated
                        PREPPED_AND_NOT_ADMINISTERED -> R.string.log_waste_reason_prepped_and_not_administered
                        EXPIRED -> R.string.expired
                        DELIVER_OUT_OF_TEMP -> R.string.log_waste_reason_delivered_out_of_temp
                        OTHER -> R.string.log_waste_reason_other
                    }
                }

                Text(
                    stringResource(
                        R.string.log_waste_footer_reason,
                        stringResource(reasonResourceId),
                        stockName
                    ),
                    style = type.bodyTypeStyle.body3
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(measurement.spacing.xSmall),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    stringResource(R.string.total),
                    style = type.bodyTypeStyle.label
                )

                Text(
                    total,
                    style = type.headerTypeStyle.headlineMediumBold
                )
            }
        }
    }
}

@FullDevicePreview
@Composable
fun WithValue() {
    PreviewContainer {
        LogWasteSummaryScreen(
            state = LogWasteSummaryState(
                reason = PREPPED_AND_NOT_ADMINISTERED,
                products = ProductUi.Sample,
                total = "1",
                stock = StockUi.PRIVATE
            ),
            handleIntent = {},
        )
    }
}

@FullDevicePreview
@Composable
fun NoValue() {
    PreviewContainer {
        LogWasteSummaryScreen(
            state = LogWasteSummaryState(
                reason = PREPPED_AND_NOT_ADMINISTERED,
                products = ProductUi.Sample,
                total = "1",
                stock = StockUi.VFC
            ),
            handleIntent = {},
        )
    }
}
