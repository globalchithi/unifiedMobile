package com.vaxcare.unifiedhub.feature.transactions.ui.returns.complete

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.Icons
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.TransactionComplete
import com.vaxcare.unifiedhub.core.ui.component.VaxCareConfetti
import com.vaxcare.unifiedhub.core.ui.component.button.PrimaryButton
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.ProductCell
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.ext.verticalFadingEdge
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.ProductUi

const val CONTENT_PANEL_HEIGHT_PORTRAIT = 760
const val CONTENT_PANEL_HEIGHT_LANDSCAPE = 640

@Composable
fun ReturnsCompleteScreen(navigateBack: () -> Unit, viewModel: ReturnsCompleteViewModel = hiltViewModel()) {
    BaseMviScreen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                is ReturnsCompleteEvent.NavigateToHome -> {
                    navigateBack()
                }
            }
        }
    ) { state, handleIntent ->

        ReturnsCompleteContent(
            state = state,
            onHomeClick = {
                handleIntent(ReturnsCompleteIntent.BackToHome)
            },
            onLogOutClick = {
                handleIntent(ReturnsCompleteIntent.LogOut)
            }
        )

        VaxCareConfetti(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun ReturnsCompleteContent(
    state: ReturnsCompleteState,
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
            title = state.getTitle(),
            description = state.getDescription(),
            contentTitle = state.date,
            contentPanelHeight = contentPanelHeight,
            summaryContent = {
                SummaryContent(
                    products = state.products,
                    totalProducts = state.totalProducts,
                    shipDate = state.shipmentPickup,
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
    totalProducts: String,
    shipDate: String?,
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
                .testTag(TestTags.Returns.Complete.PRODUCT_SHEET_CONTAINER)
                .verticalFadingEdge(
                    lazyListState = lazyListState,
                    height = 32.dp
                ).weight(1f, fill = false)
        ) {
            items(
                items = products,
                key = { product -> product.lotNumber }
            ) { product ->
                ProductListItem(
                    product = product,
                    modifier = Modifier.height(80.dp)
                )

                HorizontalDivider(
                    color = color.outline.threeHundred,
                    thickness = 2.dp
                )
            }
        }

        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = measurement.spacing.small)
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

            HorizontalDivider(
                color = color.outline.threeHundred,
                thickness = 2.dp
            )

            if (shipDate != null) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(horizontal = measurement.spacing.small)
                ) {
                    Text(
                        text = stringResource(R.string.returns_complete_shipment_pickup),
                        style = type.bodyTypeStyle.body3Bold
                    )

                    Text(
                        text = shipDate,
                        style = type.bodyTypeStyle.body3
                    )
                }

                HorizontalDivider(
                    color = color.outline.threeHundred,
                    thickness = 2.dp
                )
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
private fun ProductListItem(product: ProductUi, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .testTag(TestTags.Returns.Complete.productSheetRow(product.antigen))
            .fillMaxWidth()
            .padding(horizontal = measurement.spacing.small)
    ) {
        with(product) {
            ProductCell(
                titleRow = {
                    ProductTitleLine(
                        leadingIcon = Icons.presentationIcon(presentation),
                        title = productInfoText(antigen, prettyName)
                    )
                },
                bottomContent = {
                    ProductCellText(text = getLotInfo())
                },
                modifier = Modifier
                    .padding(end = measurement.spacing.small)
                    .weight(1f, fill = false)
            )

            Text(
                text = quantity.toString(),
                style = type.bodyTypeStyle.body3,
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
                .testTag(TestTags.Returns.Complete.HOME_BUTTON)
        ) {
            Text(
                text = stringResource(com.vaxcare.unifiedhub.core.designsystem.R.string.back_to_home),
                style = type.bodyTypeStyle.body4Bold,
                textDecoration = TextDecoration.Underline
            )
        }

        PrimaryButton(
            onClick = onLogOutClick,
            modifier = Modifier
                .size(240.dp, 56.dp)
                .testTag(TestTags.Returns.Complete.LOG_OUT_BUTTON)
        ) {
            Text(
                text = stringResource(com.vaxcare.unifiedhub.core.designsystem.R.string.log_out),
                style = type.bodyTypeStyle.body3Bold
            )
        }
    }
}

@FullDevicePreview
@Composable
private fun Default() {
    ReturnsCompleteContent(
        state = ReturnsCompleteSampleData.Default,
        {},
        {}
    )
}
