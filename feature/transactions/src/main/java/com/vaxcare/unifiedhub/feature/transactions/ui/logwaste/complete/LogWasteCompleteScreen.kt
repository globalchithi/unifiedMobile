package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.complete

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.TransactionComplete
import com.vaxcare.unifiedhub.core.ui.component.VaxCareConfetti
import com.vaxcare.unifiedhub.core.ui.component.button.PrimaryButton
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.ProductCell
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.ext.verticalFadingEdge
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.model.ProductUi
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR
import com.vaxcare.unifiedhub.core.ui.TestTags.LogWaste.Complete as testTags

const val CONTENT_PANEL_HEIGHT_PORTRAIT = 760
const val CONTENT_PANEL_HEIGHT_LANDSCAPE = 640

@Composable
fun LogWasteCompleteScreen(navigateBack: () -> Unit, viewModel: LogWasteCompleteViewModel = hiltViewModel()) {
    BaseMviScreen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                is LogWasteCompleteEvent.NavigateToHome -> {
                    navigateBack()
                }
            }
        }
    ) { state, handleIntent ->

        LogWasteCompleteContent(
            state = state,
            onHomeClick = {
                handleIntent(LogWasteCompleteIntent.BackToHome)
            },
            onLogOutClick = {
                handleIntent(LogWasteCompleteIntent.LogOut)
            }
        )

        VaxCareConfetti(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun LogWasteCompleteContent(
    state: LogWasteCompleteState,
    onHomeClick: () -> Unit,
    onLogOutClick: () -> Unit,
) {
    val contentPanelHeight = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        CONTENT_PANEL_HEIGHT_LANDSCAPE.dp
    } else {
        CONTENT_PANEL_HEIGHT_PORTRAIT.dp
    }

    ProvideStock(state.stockType) {
        TransactionComplete(
            title = stringResource(R.string.transaction_complete_title),
            description = stringResource(R.string.log_waste_complete_description),
            contentTitle = state.date,
            contentPanelHeight = contentPanelHeight,
            summaryContent = {
                SummaryContent(
                    products = state.products,
                    totalImpact = state.totalImpact,
                    totalProducts = state.totalProducts,
                    showImpact = state.showImpact,
                    onHomeClick = onHomeClick,
                    onLogOutClick = onLogOutClick
                )
            }
        )
    }
}

@Composable
private fun SummaryContent(
    products: List<ProductUi>,
    totalImpact: String,
    totalProducts: String,
    showImpact: Boolean,
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit,
    onLogOutClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxHeight()
    ) {
        val lazyListState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalFadingEdge(
                    lazyListState = lazyListState,
                    height = 32.dp
                )
        ) {
            itemsIndexed(
                items = products,
                key = { _, product -> product.id }
            ) { i, product ->

                Box(contentAlignment = Alignment.BottomStart) {
                    ProductListItem(
                        product = product,
                        modifier = Modifier.height(80.dp)
                    )

                    if (i != products.lastIndex) {
                        HorizontalDivider(
                            color = color.outline.threeHundred,
                            thickness = 2.dp
                        )
                    }
                }
            }
        }

        Column {
            HorizontalDivider(
                color = color.outline.threeHundred,
                thickness = 2.dp
            )

            if (showImpact) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(horizontal = measurement.spacing.small)
                ) {
                    Text(
                        text = stringResource(R.string.log_waste_complete_total_impact),
                        style = type.bodyTypeStyle.body3Bold
                    )

                    Text(
                        text = totalImpact,
                        style = type.bodyTypeStyle.body3Bold
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                ) {
                    Text(
                        text = stringResource(R.string.total_products),
                        style = type.bodyTypeStyle.body3Bold
                    )

                    Text(
                        text = totalProducts,
                        style = type.bodyTypeStyle.body3Bold
                    )
                }
            }

            NavigationButtons(
                onHomeClick = onHomeClick,
                onLogOutClick = onLogOutClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun ProductListItem(product: ProductUi, modifier: Modifier = Modifier,) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        with(product) {
            ProductCell(
                prettyName = product.prettyName,
                antigenName = product.antigen,
                presentation = painterResource(presentationIcon),
                bottomText = lotsPreview,
                topText = null,
                modifier = Modifier
                    .padding(horizontal = measurement.spacing.small)
                    .weight(1f, fill = false)
            )

            Text(
                text = quantity.toString(),
                style = type.bodyTypeStyle.body3,
                modifier = Modifier.padding(end = measurement.spacing.small)
            )
        }
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
                .testTag(testTags.HOME_BUTTON)
        ) {
            Text(
                text = stringResource(DesignSystemR.string.back_to_home),
                style = type.bodyTypeStyle.body4Bold,
                textDecoration = TextDecoration.Underline
            )
        }

        PrimaryButton(
            onClick = onLogOutClick,
            modifier = Modifier
                .size(240.dp, 56.dp)
                .testTag(testTags.LOG_OUT_BUTTON)
        ) {
            Text(
                text = stringResource(DesignSystemR.string.log_out),
                style = type.bodyTypeStyle.body3Bold
            )
        }
    }
}

@FullDevicePreview
@Composable
private fun WithImpact() {
    LogWasteCompleteContent(
        state = LogWasteCompleteSampleData.WithImpact,
        {},
        {}
    )
}

@FullDevicePreview
@Composable
private fun NoImpact() {
    LogWasteCompleteContent(
        state = LogWasteCompleteSampleData.NoImpact,
        {},
        {}
    )
}
