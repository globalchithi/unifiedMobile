package com.vaxcare.unifiedhub.feature.transactions.ui.counts.complete

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.TransactionComplete
import com.vaxcare.unifiedhub.core.ui.component.button.PrimaryButton
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullscreenPreview
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.complete.CountsCompleteEvent.NavigateToHome
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.complete.CountsCompleteIntent.BackToHome
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.complete.CountsCompleteIntent.LogOut
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
fun CountsComplete(navigateBack: () -> Unit, viewModel: CountsCompleteViewModel = hiltViewModel()) {
    BaseMviScreen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                is NavigateToHome -> {
                    navigateBack()
                }
            }
        }
    ) { state, handleIntent ->
        ProvideStock(state.stockType) {
            CountsCompleteContent(
                state = state,
                onHomeClick = {
                    handleIntent(BackToHome)
                },
                onLogOutClick = {
                    handleIntent(LogOut)
                }
            )
        }
    }
}

@Composable
private fun CountsCompleteContent(
    state: CountsCompleteState,
    onHomeClick: () -> Unit,
    onLogOutClick: () -> Unit,
) {
    TransactionComplete(
        title = stringResource(R.string.transaction_complete_title),
        description = stringResource(R.string.confirm_description, state.stockType.prettyName),
        contentTitle = state.date,
        summaryContent = {
            when {
                state.showImpact && state.showVariance -> {
                    val disclaimer = state.disclaimerRes?.let {
                        stringResource(it)
                    }
                    SummaryContentWithImpact(
                        addedUnits = state.addedUnits ?: 0,
                        addedImpact = state.addedImpact ?: "",
                        missingUnits = state.missingUnits ?: 0,
                        missingImpact = state.missingImpact ?: "",
                        totalImpact = state.totalImpact ?: "",
                        disclaimer = disclaimer,
                        onHomeClick = onHomeClick,
                        onLogOutClick = onLogOutClick
                    )
                }

                state.showVariance -> {
                    SummaryContentWithVariance(
                        addedUnits = state.addedUnits ?: 0,
                        missingUnits = state.missingUnits ?: 0,
                        onHomeClick = onHomeClick,
                        onLogOutClick = onLogOutClick
                    )
                }

                else -> {
                    SummaryContentNoVariance(
                        totalProducts = state.totalProducts ?: 0,
                        totalUnits = state.totalUnits ?: 0,
                        onHomeClick = onHomeClick,
                        onLogOutClick = onLogOutClick
                    )
                }
            }
        },
        bottomPanel = if (state.showImpact) {
            { BottomPanel(state.inventoryBalance) }
        } else {
            { Spacer(Modifier.height(70.dp)) }
        }
    )
}

@Composable
private fun SummaryContentWithVariance(
    addedUnits: Int,
    missingUnits: Int,
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit,
    onLogOutClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxHeight()
    ) {
        Column {
            TableRow {
                Text(
                    text = stringResource(R.string.confirm_added_units),
                    style = type.bodyTypeStyle.body3Bold
                )
                Text(
                    text = "$addedUnits",
                    style = type.bodyTypeStyle.body3Bold,
                    modifier = Modifier.testTag(TestTags.Counts.Complete.TOTAL_PRODUCTS_VALUE)
                )
            }
            HorizontalDivider(thickness = 2.dp)
            TableRow {
                Text(
                    text = stringResource(R.string.confirm_missing_units),
                    style = type.bodyTypeStyle.body3Bold
                )
                Text(
                    text = "$missingUnits",
                    style = type.bodyTypeStyle.body3Bold,
                    modifier = Modifier.testTag(TestTags.Counts.Complete.TOTAL_UNITS_VALUE)
                )
            }
            HorizontalDivider(thickness = 2.dp)
        }

        NavigationButtons(
            onHomeClick = onHomeClick,
            onLogOutClick = onLogOutClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun SummaryContentNoVariance(
    totalProducts: Int,
    totalUnits: Int,
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit,
    onLogOutClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxHeight()
    ) {
        Column {
            TableRow {
                Text(
                    text = stringResource(R.string.total_products),
                    style = type.bodyTypeStyle.body3Bold
                )
                Text(
                    text = "$totalProducts",
                    style = type.bodyTypeStyle.body3Bold,
                    modifier = Modifier.testTag(TestTags.Counts.Complete.TOTAL_PRODUCTS_VALUE)
                )
            }
            HorizontalDivider(thickness = 2.dp)
            TableRow {
                Text(
                    text = stringResource(R.string.confirm_total_units),
                    style = type.bodyTypeStyle.body3Bold
                )
                Text(
                    text = "$totalUnits",
                    style = type.bodyTypeStyle.body3Bold,
                    modifier = Modifier.testTag(TestTags.Counts.Complete.TOTAL_UNITS_VALUE)
                )
            }
            HorizontalDivider(thickness = 2.dp)
        }

        NavigationButtons(
            onHomeClick = onHomeClick,
            onLogOutClick = onLogOutClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun SummaryContentWithImpact(
    addedUnits: Int,
    addedImpact: String,
    missingUnits: Int,
    missingImpact: String,
    totalImpact: String,
    disclaimer: String?,
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit,
    onLogOutClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxHeight()
    ) {
        Column {
            TableRow {
                Text(
                    text = stringResource(R.string.confirm_added_units),
                    style = type.bodyTypeStyle.body3Bold
                )
                Row {
                    TableCell(modifier = Modifier.width(160.dp)) {
                        Text(
                            text = "$addedUnits",
                            style = type.bodyTypeStyle.body3Bold,
                            modifier = Modifier.testTag(TestTags.Counts.Complete.ADDED_UNITS_VALUE)
                        )
                    }
                    TableCell(modifier = Modifier.width(160.dp)) {
                        Text(
                            text = addedImpact,
                            style = type.bodyTypeStyle.body3,
                            modifier = Modifier.testTag(TestTags.Counts.Complete.ADDED_IMPACT_VALUE)
                        )
                    }
                }
            }
            HorizontalDivider(thickness = 2.dp)
            TableRow {
                Text(
                    text = stringResource(R.string.confirm_missing_units),
                    style = type.bodyTypeStyle.body3Bold
                )
                Row {
                    TableCell(modifier = Modifier.width(160.dp)) {
                        Text(
                            text = "$missingUnits",
                            style = type.bodyTypeStyle.body3Bold,
                            modifier = Modifier.testTag(TestTags.Counts.Complete.MISSING_UNITS_VALUE)
                        )
                    }
                    TableCell(modifier = Modifier.width(160.dp)) {
                        Text(
                            text = missingImpact,
                            style = type.bodyTypeStyle.body3,
                            modifier = Modifier.testTag(TestTags.Counts.Complete.MISSING_IMPACT_VALUE)
                        )
                    }
                }
            }
            HorizontalDivider(thickness = 2.dp)
            TableRow(
                horizontalArrangement = Arrangement.End
            ) {
                TableCell {
                    Text(
                        text = totalImpact,
                        style = type.bodyTypeStyle.body3Bold,
                        modifier = Modifier.testTag(TestTags.Counts.Complete.TOTAL_IMPACT_VALUE)
                    )
                }
            }
            if (disclaimer != null) {
                Column(
                    modifier = Modifier.padding(measurement.spacing.medium)
                ) {
                    Text(
                        text = stringResource(R.string.confirm_verify_count),
                        style = type.bodyTypeStyle.body6Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = disclaimer,
                        style = type.bodyTypeStyle.body6,
                        modifier = Modifier.testTag(TestTags.Counts.Complete.DISCLAIMER_TEXT)
                    )
                }
            }
        }

        NavigationButtons(
            onHomeClick = onHomeClick,
            onLogOutClick = onLogOutClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun NavigationButtons(
    onHomeClick: () -> Unit,
    onLogOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        TextButton(
            onClick = onHomeClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = color.onContainer.onContainerPrimary
            ),
            modifier = Modifier
                .width(192.dp)
                .padding(measurement.spacing.small)
                .testTag(TestTags.Counts.Complete.BACK_TO_HOME_BUTTON)
        ) {
            Text(
                text = stringResource(DesignSystemR.string.back_to_home),
                style = type.bodyTypeStyle.body4Bold,
                textDecoration = TextDecoration.Underline,
            )
        }

        PrimaryButton(
            onClick = onLogOutClick,
            modifier = Modifier
                .size(240.dp, 56.dp)
                .testTag(TestTags.Counts.Complete.LOG_OUT_BUTTON)
        ) {
            Text(
                text = stringResource(DesignSystemR.string.log_out),
                style = type.bodyTypeStyle.body3Bold
            )
        }
    }
}

@Composable
private fun TableRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(horizontal = measurement.spacing.small)
            .fillMaxWidth()
            .height(80.dp),
        content = content
    )
}

@Composable
private fun TableCell(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = modifier
    ) {
        content()
    }
}

@Composable
private fun BottomPanel(inventoryBalance: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(start = 56.dp)) {
        Text(
            text = stringResource(R.string.confirm_inventory_balance).uppercase(),
            style = type.titleTypeStyle.titleMedium,
            color = color.onContainer.onContainerSecondary,
            modifier = Modifier.padding(bottom = measurement.spacing.xSmall)
        )

        Text(
            text = inventoryBalance,
            style = type.displayTypeStyle.display3,
            color = color.onContainer.onContainerSecondary,
            modifier = Modifier.testTag(TestTags.Counts.Complete.INVENTORY_BALANCE_LABEL)
        )
    }
}

@FullDevicePreview
@Composable
private fun Default() {
    FullscreenPreview {
        CountsCompleteContent(
            state = CountsCompleteSampleData.WithImpact,
            {},
            {}
        )
    }
}

@FullDevicePreview
@Composable
private fun NoImpact() {
    FullscreenPreview {
        ProvideStock(StockUi.THREE_SEVENTEEN) {
            CountsCompleteContent(
                state = CountsCompleteSampleData.NoImpact.copy(stockType = StockUi.THREE_SEVENTEEN),
                {},
                {}
            )
        }
    }
}
