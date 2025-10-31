package com.vaxcare.unifiedhub.feature.transactions.ui.common.confirmstock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.core.ui.component.VCScaffold
import com.vaxcare.unifiedhub.core.ui.component.button.TonalButton
import com.vaxcare.unifiedhub.core.ui.component.button.VCFloatingActionButton
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.core.ui.model.StockUi.*
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
fun ConfirmStockScreen(
    onStockConfirmed: (stockType: StockType) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ConfirmStockViewModel = hiltViewModel(),
) {
    BaseMviScreen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                ConfirmStockEvent.GoBack -> onNavigateBack()
                is ConfirmStockEvent.StockConfirmed -> onStockConfirmed(event.stock)
            }
        }
    ) { state, sendIntent ->
        ProvideStock(state.selectedStock) {
            ConfirmStockContent(state, sendIntent)
        }
    }
}

@Composable
fun ConfirmStockContent(state: ConfirmStockState, handleIntent: (ConfirmStockIntent) -> Unit) {
    VCScaffold(
        topBar = {
            BaseTitleBar(
                title = state.title ?: DesignSystemR.string.confirm,
                buttonIcon = DesignSystemR.drawable.ic_close,
                onButtonClick = { handleIntent(ConfirmStockIntent.Close) }
            )
        },
        fab = {
            VCFloatingActionButton(
                onClick = { handleIntent(ConfirmStockIntent.ConfirmStock) },
                iconPainter = painterResource(DesignSystemR.drawable.ic_arrow_forward),
                enabled = true,
                modifier = Modifier.testTag(TestTags.ConfirmStock.NEXT_BUTTON)
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(VaxCareTheme.measurement.spacing.topBarLargeY))

            state.subtitle?.let {
                Text(
                    textAlign = TextAlign.Start,
                    text = stringResource(state.subtitle),
                    style = VaxCareTheme.type.bodyTypeStyle.body2
                )
            }

            Spacer(modifier = Modifier.height(29.dp))

            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 504.dp),
                verticalArrangement = Arrangement.spacedBy(VaxCareTheme.measurement.spacing.buttonLg)
            ) {
                state.stocks.forEach { stock ->
                    val isSelected = stock == state.selectedStock
                    TonalButton(
                        onClick = { handleIntent(ConfirmStockIntent.StockSelected(stock)) },
                        text = stringResource(
                            R.string.confirm_stock_vaccine_stock,
                            stock.prettyName
                        ),
                        leadingIconRes = if (isSelected) DesignSystemR.drawable.ic_check else null,
                        highlight = isSelected,
                        modifier = Modifier.testTag(TestTags.ConfirmStock.stockButton(stock))
                    )
                }
            }
        }
    }
}

@FullDevicePreview
@Composable
fun PreviewConfirmStockScreen() {
    ConfirmStockContent(
        state = ConfirmStockState(
            title = DesignSystemR.string.confirm,
            subtitle = DesignSystemR.string.nonseasonal,
            stocks = StockUi.entries
        )
    ) { }
}
