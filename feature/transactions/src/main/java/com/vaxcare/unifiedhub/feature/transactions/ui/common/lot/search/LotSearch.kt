package com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.search

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.core.ui.component.button.PrimaryButton
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.search.dialog.ConfirmLotNumberDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.search.input.LotSearchInputField
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

/**
 * @param onNavigateBack navigate back callback with the Lot
 * @param viewModel
 */
@Composable
fun LotSearchScreen(
    onNavigateBack: () -> Unit,
    onLotSelected: (String, Int) -> Unit,
    onAddNewLot: (lotNumber: String, productId: Int?) -> Unit,
    viewModel: LotSearchViewModel = hiltViewModel()
) {
    BaseMviScreen<LotSearchState, LotSearchEvent, LotSearchIntent>(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                LotSearchEvent.NavigateBack -> onNavigateBack()
                is LotSearchEvent.NavigateWithSelectedLot -> {
                    onLotSelected(event.selectedLotNumber, event.sourceId)
                }

                is LotSearchEvent.NavigateToAddLot -> {
                    onAddNewLot(event.lotNumber, event.productId)
                }
            }
        }
    ) { state, handleIntent ->
        LotSearchContent(
            state = state,
            orientation = LocalConfiguration.current.orientation,
            onAddNewLot = { handleIntent(LotSearchIntent.AddNewLot(state.searchTerm.text)) },
            onNavigateBack = { handleIntent(LotSearchIntent.CloseScreen) },
            onTextChanged = { term -> handleIntent(LotSearchIntent.SearchLot(term)) },
            onLotSelected = { lot -> handleIntent(LotSearchIntent.SelectLot(lot)) },
            onClearClicked = { handleIntent(LotSearchIntent.SearchLot(TextFieldValue())) },
        )

        when (state.activeDialog) {
            is LotSearchDialog.ConfirmLotNumber -> VaxCareTheme.ConfirmLotNumberDialog(
                lotNumber = state.activeDialog.enteredLotNumber,
                onConfirmClick = { lot -> handleIntent(LotSearchIntent.ConfirmedAddNewLot(lot)) },
                onCancelClick = { handleIntent(LotSearchIntent.CancelAddNewLot) }
            )
        }
    }
}

@Composable
fun LotSearchContent(
    modifier: Modifier = Modifier,
    state: LotSearchState,
    orientation: Int,
    onNavigateBack: () -> Unit,
    onAddNewLot: () -> Unit,
    onTextChanged: (TextFieldValue) -> Unit,
    onLotSelected: (SelectedLot) -> Unit,
    onClearClicked: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            BaseTitleBar(
                modifier = Modifier,
                title = R.string.lot_search_title,
                buttonIcon = DesignSystemR.drawable.ic_close,
                onButtonClick = onNavigateBack
            )
        },
        containerColor = VaxCareTheme.color.surface.surface,
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        val focusRequester = remember { FocusRequester() }
        with(VaxCareTheme) {
            when (orientation) {
                Configuration.ORIENTATION_LANDSCAPE ->
                    LotSearchLandscape(
                        modifier = Modifier.padding(paddingValues),
                        state = state,
                        focusRequester = focusRequester,
                        onTextChanged = onTextChanged,
                        onClearClicked = onClearClicked,
                        onLotSelected = onLotSelected,
                        onAddNewLot = onAddNewLot
                    )

                else ->
                    LotSearchPortrait(
                        modifier = Modifier.padding(paddingValues),
                        state = state,
                        focusRequester = focusRequester,
                        onTextChanged = onTextChanged,
                        onClearClicked = onClearClicked,
                        onLotSelected = onLotSelected,
                        onAddNewLot = onAddNewLot
                    )
            }

            LaunchedEffect(orientation) { focusRequester.requestFocus() }
        }
    }
}

@Composable
private fun VaxCareTheme.LotSearchLandscape(
    modifier: Modifier = Modifier,
    state: LotSearchState,
    focusRequester: FocusRequester,
    onTextChanged: (TextFieldValue) -> Unit,
    onClearClicked: () -> Unit,
    onLotSelected: (SelectedLot) -> Unit,
    onAddNewLot: () -> Unit
) {
    Column(
        modifier = modifier
            .imePadding()
            .fillMaxSize()
            .padding(top = measurement.spacing.topBarMediumY),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        LotSearchInputField(
            initialLabel = stringResource(R.string.lot_search_title),
            value = state.searchTerm,
            modifier = Modifier
                .focusRequester(focusRequester),
            isEnabled = true,
            onTextChanged = onTextChanged,
            onClearClick = onClearClicked
        )

        if (state.selectedLots.isNotEmpty()) {
            Spacer(Modifier.height(measurement.spacing.large))
        }

        Column(
            modifier = Modifier
                .width(measurement.size.cardLarge)
                .clip(RoundedCornerShape(measurement.radius.cardMedium))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .heightIn(
                        min = if (state.selectedLots.isEmpty()) {
                            0.dp
                        } else {
                            337.dp
                        }
                    ).width(measurement.size.cardLarge),
                shape = RoundedCornerShape(measurement.radius.cardMedium),
                color = color.container.primaryContainer,
            ) {
                LazyColumn(Modifier.heightIn(max = 337.dp)) {
                    items(
                        items = state.selectedLots,
                        key = { it.lotNumber.text }
                    ) { lot ->
                        LotSearchItem(
                            selectedLot = lot,
                            onLotItemClick = { onLotSelected(lot) }
                        )
                    }
                }
            }

            if (state.searchTerm.text.length > 2 && state.addNewLotEnabled) {
                Spacer(Modifier.height(measurement.spacing.large))
                Column(
                    modifier = Modifier.width(605.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.lot_search_not_existing_lot),
                        style = type.bodyTypeStyle.body4
                    )
                    Spacer(Modifier.height(measurement.spacing.small))
                    PrimaryButton(
                        modifier = Modifier.size(width = 296.dp, height = 60.dp),
                        onClick = onAddNewLot
                    ) {
                        Text(
                            text = stringResource(R.string.lot_search_add_new_lot_label),
                            style = type.bodyTypeStyle.body3Bold
                        )
                    }
                    Text(
                        modifier = Modifier.padding(
                            vertical = 28.dp,
                            horizontal = 4.dp
                        ),
                        text = stringResource(R.string.lot_search_add_new_lot_disclaimer),
                        style = type.bodyTypeStyle.body5,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun VaxCareTheme.LotSearchPortrait(
    modifier: Modifier = Modifier,
    state: LotSearchState,
    focusRequester: FocusRequester,
    onTextChanged: (TextFieldValue) -> Unit,
    onClearClicked: () -> Unit,
    onLotSelected: (SelectedLot) -> Unit,
    onAddNewLot: () -> Unit
) {
    Column(
        modifier = modifier
            .imePadding()
            .fillMaxSize()
            .padding(top = measurement.spacing.topBarMediumY),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        LotSearchInputField(
            initialLabel = stringResource(R.string.lot_search_title),
            value = state.searchTerm,
            modifier = Modifier
                .focusRequester(focusRequester),
            isEnabled = true,
            onTextChanged = onTextChanged,
            onClearClick = onClearClicked
        )

        if (state.selectedLots.isNotEmpty()) {
            Spacer(Modifier.height(measurement.spacing.xLarge))
        }

        Column(
            modifier = Modifier
                .width(measurement.size.cardLarge)
                .clip(RoundedCornerShape(measurement.radius.cardMedium))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .heightIn(
                        min = if (state.selectedLots.isEmpty()) {
                            0.dp
                        } else {
                            557.dp
                        }
                    ).width(measurement.size.cardFull),
                shape = RoundedCornerShape(measurement.radius.cardMedium),
                color = color.container.primaryContainer,
            ) {
                LazyColumn(
                    Modifier.heightIn(max = 557.dp)
                ) {
                    items(
                        items = state.selectedLots,
                        key = { it.lotNumber.text }
                    ) { lot ->
                        LotSearchItem(
                            selectedLot = lot,
                            onLotItemClick = { onLotSelected(lot) }
                        )
                    }
                }
            }

            if (state.searchTerm.text.length > 2 && state.addNewLotEnabled) {
                Spacer(Modifier.height(measurement.spacing.xLarge))
                Column(
                    modifier = Modifier.width(296.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.lot_search_not_existing_lot),
                        style = type.bodyTypeStyle.body4
                    )
                    Spacer(Modifier.height(measurement.spacing.small))
                    PrimaryButton(
                        modifier = Modifier.size(width = 296.dp, height = 60.dp),
                        onClick = onAddNewLot
                    ) {
                        Text(
                            text = stringResource(R.string.lot_search_add_new_lot_label),
                            style = type.bodyTypeStyle.body3Bold
                        )
                    }
                    Text(
                        modifier = Modifier.padding(
                            vertical = 28.dp,
                            horizontal = 4.dp
                        ),
                        text = stringResource(R.string.lot_search_add_new_lot_disclaimer),
                        style = type.bodyTypeStyle.body5,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun LotSearchItem(selectedLot: SelectedLot, onLotItemClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .clickable(onClick = onLotItemClick)
            .padding(horizontal = measurement.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.size(width = 136.dp, height = 24.dp),
            text = selectedLot.lotNumber,
            style = type.bodyTypeStyle.body4
        )
        Spacer(Modifier.width(16.dp))
        Icon(
            painter = painterResource(selectedLot.presentationIcon),
            contentDescription = ""
        )
        Spacer(Modifier.width(measurement.spacing.xSmall))
        Text(selectedLot.productName)
    }
}

@Preview(
    device = "spec:width=1280dp,height=800dp,dpi=320",
    showBackground = true
)
@Composable
private fun PreviewLandscapeLotSearch() {
    VaxCareTheme {
        LotSearchContent(
            state = LotSearchState(),
            orientation = Configuration.ORIENTATION_LANDSCAPE,
            onTextChanged = {},
            onAddNewLot = {},
            onLotSelected = {},
            onNavigateBack = {},
            onClearClicked = {},
        )
    }
}

@Preview(
    device = "spec:width=1280dp,height=800dp,dpi=320",
    showBackground = true
)
@Composable
private fun PreviewLandscapeWithItemsLotSearch() {
    VaxCareTheme {
        LotSearchContent(
            state = LotSearchState(
                selectedLots = listOf(
                    SelectedLot(
                        lotNumber = AnnotatedString("TESTLOT123"),
                        presentationIcon = DesignSystemR.drawable.ic_presentation_syringe,
                        productName = AnnotatedString("Product Name Here")
                    )
                ),
                searchTerm = TextFieldValue("TEST")
            ),
            orientation = Configuration.ORIENTATION_LANDSCAPE,
            onTextChanged = {},
            onAddNewLot = {},
            onLotSelected = {},
            onNavigateBack = {},
            onClearClicked = {},
        )
    }
}

@Preview(
    device = "spec:width=1280dp,height=800dp,dpi=320,orientation=portrait",
    showBackground = true
)
@Composable
private fun PreviewPortraitLotSearch() {
    VaxCareTheme {
        LotSearchContent(
            state = LotSearchState(),
            orientation = Configuration.ORIENTATION_PORTRAIT,
            onTextChanged = {},
            onAddNewLot = {},
            onLotSelected = {},
            onNavigateBack = {},
            onClearClicked = {},
        )
    }
}

@Preview(
    device = "spec:width=1280dp,height=800dp,dpi=320,orientation=portrait",
    showBackground = true
)
@Composable
private fun PreviewPortraitWithItemsLotSearch() {
    VaxCareTheme {
        LotSearchContent(
            state = LotSearchState(
                selectedLots = samples,
                searchTerm = TextFieldValue("TEST")
            ),
            orientation = Configuration.ORIENTATION_PORTRAIT,
            onTextChanged = {},
            onAddNewLot = {},
            onLotSelected = {},
            onNavigateBack = {},
            onClearClicked = {},
        )
    }
}

private val samples = listOf(
    SelectedLot(
        lotNumber = AnnotatedString("TESTLOT123"),
        presentationIcon = DesignSystemR.drawable.ic_presentation_syringe,
        productName = AnnotatedString("Product Name Here")
    ),
)
