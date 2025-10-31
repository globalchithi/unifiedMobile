package com.vaxcare.unifiedhub.feature.transactions.ui.returns.window

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.model.PickupAvailability
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.core.ui.component.ButtonConfig
import com.vaxcare.unifiedhub.core.ui.component.LogoSpinner
import com.vaxcare.unifiedhub.core.ui.component.VCBasicDialog
import com.vaxcare.unifiedhub.core.ui.component.VCScaffold
import com.vaxcare.unifiedhub.core.ui.component.button.ElevatedIconButton
import com.vaxcare.unifiedhub.core.ui.component.button.TonalButton
import com.vaxcare.unifiedhub.core.ui.component.button.VCFloatingActionButton
import com.vaxcare.unifiedhub.core.ui.component.keypad.KeypadDialog
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.EditQuantityCell
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullscreenPreview
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.getDisplayText
import timber.log.Timber
import kotlin.math.min
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
fun ReturnsWindowScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSummary: (Int?) -> Unit,
    viewModel: ReturnsWindowViewModel = hiltViewModel()
) {
    BaseMviScreen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                ReturnsWindowEvent.NavigateBack -> onNavigateBack()
                is ReturnsWindowEvent.NavigateToSummary -> onNavigateToSummary(
                    event.noOfLabels,
                )
            }
        }
    ) { state, sendIntent ->

        ReturnsWindowContent(
            state = state,
            sendIntent = sendIntent
        )
    }
}

@Composable
private fun ReturnsWindowContent(state: ReturnsWindowState, sendIntent: (ReturnsWindowIntent) -> Unit) {
    Box {
        VCScaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                BaseTitleBar(
                    title = stringResource(R.string.returns_top_bar_title),
                    buttonIcon = DesignSystemR.drawable.ic_arrow_back,
                    onButtonClick = { sendIntent(ReturnsWindowIntent.NavigateBack) }
                )
            },
            fab = {
                VCFloatingActionButton(
                    modifier = Modifier.testTag(TestTags.LARGE_FAB),
                    enabled = state.canConfirm,
                    onClick = { sendIntent(ReturnsWindowIntent.Confirm) },
                    iconPainter = painterResource(DesignSystemR.drawable.ic_arrow_forward)
                )
            }
        ) {
            when (LocalConfiguration.current.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> ReturnsWindowPortraitBody(state, sendIntent)

                else -> ReturnsWindowLandscapeBody(state, sendIntent)
            }
        }
    }

    LoadingState(state.loading, modifier = Modifier.fillMaxSize())

    when (state.activeDialog) {
        ReturnsWindowDialog.Retry -> VCBasicDialog(
            title = stringResource(R.string.dialog_no_internet_title),
            text = stringResource(R.string.dialog_no_internet_description),
            primaryButtonConfig = ButtonConfig(
                text = stringResource(DesignSystemR.string.retry),
                onClick = { sendIntent(ReturnsWindowIntent.NoInternetConnectionIntent.TryAgain) }
            ),
            secondaryButtonConfig = ButtonConfig(
                text = stringResource(DesignSystemR.string.cancel),
                onClick = { sendIntent(ReturnsWindowIntent.NoInternetConnectionIntent.Cancel) }
            ),
            onDismissRequest = { sendIntent(ReturnsWindowIntent.NoInternetConnectionIntent.Cancel) }
        )

        ReturnsWindowDialog.EditShippingLabelQuantity -> {
            var inputKey by remember { mutableStateOf("") }

            val isConfirmButtonEnabled = remember(inputKey) {
                if (inputKey.isBlank()) {
                    false
                } else {
                    try {
                        val number = inputKey.toInt()
                        number >= ReturnsWindowState.MIN_SHIPPING_LABEL_QUANTITY &&
                            number <= ReturnsWindowState.MAX_SHIPPING_LABEL_QUANTITY
                    } catch (e: NumberFormatException) {
                        // This guards against very large numbers (beyond Int.MAX_VALUE)
                        // or if somehow non-digit characters get in (though onDigitClick prevents this)
                        Timber.e(e)
                        false
                    }
                }
            }

            KeypadDialog(
                dialogTitle = stringResource(R.string.enter_quantity),
                input = inputKey,
                onCloseClick = { sendIntent(ReturnsWindowIntent.CloseDialog) },
                onClearClick = { inputKey = "" },
                onDigitClick = {
                    if (inputKey.length < ReturnsWindowState.MAX_SHIPPING_LABEL_QUANTITY.toString().length) {
                        inputKey += it
                    }
                },
                onDeleteClick = { inputKey = inputKey.dropLast(1) },
                onSubmit = {
                    if (isConfirmButtonEnabled) {
                        sendIntent(ReturnsWindowIntent.SetLabelQuantity(inputKey.toInt()))
                    }
                },
                isConfirmEnabled = isConfirmButtonEnabled
            )
        }
    }
}

@Composable
private fun ReturnsWindowPortraitBody(state: ReturnsWindowState, sendIntent: (ReturnsWindowIntent) -> Unit) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(measurement.spacing.topBarXLargeY))

        Text(
            text = stringResource(id = R.string.returns_window_when_do_you_want_your_return_to_be_picked_up),
            style = type.bodyTypeStyle.body2
        )
        Spacer(Modifier.height(measurement.spacing.large))

        SelectPickUpPortrait(
            availablePickups = state.availablePickups,
            selectedIndex = state.selectedIndex,
            pageIndex = state.pageIndex,
            isPaginatedEnabled = state.isPaginationEnabled,
            onPickupSelected = { sendIntent(ReturnsWindowIntent.SelectPickUp(it)) },
            onGetPreviousPickups = { sendIntent(ReturnsWindowIntent.GetPreviousAvailablePickUps) },
            onGetNextPickups = { sendIntent(ReturnsWindowIntent.GetNextAvailablePickUps) }
        )
        Spacer(Modifier.height(measurement.spacing.xLarge))

        Spacer(Modifier.height(measurement.spacing.medium))

        if (state.selectedIndex != null) {
            HowManyShippingLabels(
                shippingLabelsQuantity = state.shippingLabels,
                canDecrementShippingLabels = state.canDecrementShippingLabels,
                canIncrementShippingLabels = state.canIncrementShippingLabels,
                onDecreaseLabels = { sendIntent(ReturnsWindowIntent.DecrementLabelQuantity) },
                onIncreaseLabels = { sendIntent(ReturnsWindowIntent.IncrementLabelQuantity) },
                onEditLabelQuantity = { sendIntent(ReturnsWindowIntent.EditLabelQuantity) }
            )
        }
    }
}

@Composable
private fun ReturnsWindowLandscapeBody(state: ReturnsWindowState, sendIntent: (ReturnsWindowIntent) -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(measurement.spacing.topBarLargeY))

        Text(
            text = stringResource(id = R.string.returns_window_when_do_you_want_your_return_to_be_picked_up),
            style = type.bodyTypeStyle.body2
        )
        Spacer(Modifier.height(measurement.spacing.large))

        SelectPickUpLandscape(
            availablePickups = state.availablePickups,
            selectedIndex = state.selectedIndex,
            pageIndex = state.pageIndex,
            onPickupSelected = { sendIntent(ReturnsWindowIntent.SelectPickUp(it)) },
            onGetPreviousPickups = { sendIntent(ReturnsWindowIntent.GetPreviousAvailablePickUps) },
            onGetNextPickups = { sendIntent(ReturnsWindowIntent.GetNextAvailablePickUps) }
        )
        Spacer(Modifier.height(measurement.spacing.xLarge))

        Spacer(Modifier.height(measurement.spacing.medium))

        if (state.selectedIndex != null) {
            HowManyShippingLabels(
                shippingLabelsQuantity = state.shippingLabels,
                canDecrementShippingLabels = state.canDecrementShippingLabels,
                canIncrementShippingLabels = state.canIncrementShippingLabels,
                onDecreaseLabels = { sendIntent(ReturnsWindowIntent.DecrementLabelQuantity) },
                onIncreaseLabels = { sendIntent(ReturnsWindowIntent.IncrementLabelQuantity) },
                onEditLabelQuantity = { sendIntent(ReturnsWindowIntent.EditLabelQuantity) }
            )
        }
    }
}

@Composable
private fun SelectPickUpPortrait(
    availablePickups: List<PickupAvailability>,
    selectedIndex: Int?,
    pageIndex: Int,
    isPaginatedEnabled: Boolean,
    onPickupSelected: (Int) -> Unit,
    onGetPreviousPickups: () -> Unit,
    onGetNextPickups: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedIconButton(
            enabled = isPaginatedEnabled,
            onClick = onGetPreviousPickups,
            iconDrawRes = DesignSystemR.drawable.ic_chevron_up
        )
        Spacer(Modifier.height(measurement.spacing.large))

        ShippingDates(
            availablePickups = availablePickups,
            selectedIndex = selectedIndex,
            pageIndex = pageIndex,
            onPickupSelected = onPickupSelected
        )

        Spacer(Modifier.height(measurement.spacing.large))
        ElevatedIconButton(
            enabled = isPaginatedEnabled,
            onClick = onGetNextPickups,
            iconDrawRes = DesignSystemR.drawable.ic_chevron_down
        )
    }
}

@Composable
private fun SelectPickUpLandscape(
    availablePickups: List<PickupAvailability>,
    selectedIndex: Int?,
    pageIndex: Int,
    onPickupSelected: (Int) -> Unit,
    onGetPreviousPickups: () -> Unit,
    onGetNextPickups: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 80.dp)
                .height(245.dp)
        ) {
            ShippingDates(
                availablePickups = availablePickups,
                selectedIndex = selectedIndex,
                pageIndex = pageIndex,
                onPickupSelected = onPickupSelected
            )
            Spacer(Modifier.width(measurement.spacing.large))
            Column {
                ElevatedIconButton(
                    onClick = onGetPreviousPickups,
                    iconDrawRes = DesignSystemR.drawable.ic_chevron_up
                )
                Spacer(Modifier.height(measurement.spacing.large))
                ElevatedIconButton(
                    onClick = onGetNextPickups,
                    iconDrawRes = DesignSystemR.drawable.ic_chevron_down
                )
            }
        }
    }
}

@Composable
private fun ShippingDates(
    availablePickups: List<PickupAvailability>,
    selectedIndex: Int?,
    pageIndex: Int,
    onPickupSelected: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(measurement.spacing.buttonLg),
        modifier = Modifier
            .padding(bottom = 5.dp)
            .size(width = 504.dp, height = 245.dp)
            .testTag(TestTags.Returns.Window.SHIPPING_DATE_CONTAINER)
    ) {
        val pageSlice = (pageIndex * ReturnsWindowState.PAGE_SIZE).let {
            it..<min(it + ReturnsWindowState.PAGE_SIZE, availablePickups.size)
        }

        availablePickups.slice(pageSlice).forEachIndexed { i, item ->
            val realIndex = i + pageSlice.start
            val isSelected = selectedIndex == realIndex
            val pickupText = item.getDisplayText()
            TonalButton(
                modifier = Modifier.testTag(TestTags.Returns.Window.shippingDateRow(pickupText)),
                onClick = { onPickupSelected(realIndex) },
                text = pickupText,
                highlight = isSelected,
                leadingIconRes = if (isSelected) {
                    DesignSystemR.drawable.ic_check
                } else {
                    null
                }
            )
        }
    }
}

@Composable
private fun HowManyShippingLabels(
    shippingLabelsQuantity: Int,
    canDecrementShippingLabels: Boolean,
    canIncrementShippingLabels: Boolean,
    onDecreaseLabels: (Int) -> Unit,
    onIncreaseLabels: (Int) -> Unit,
    onEditLabelQuantity: () -> Unit
) {
    Text(
        text = stringResource(id = R.string.returns_window_how_many_shipping_labels),
        style = type.bodyTypeStyle.body2
    )
    Spacer(Modifier.height(measurement.spacing.medium))
    EditQuantityCell(
        quantity = shippingLabelsQuantity,
        decrementEnabled = canDecrementShippingLabels,
        incrementEnabled = canIncrementShippingLabels,
        enabled = true,
        onDecrementClick = { onDecreaseLabels(1) },
        onDecrementLongClick = { onDecreaseLabels(5) },
        onIncrementClick = { onIncreaseLabels(1) },
        onIncrementLongClick = { onIncreaseLabels(5) },
        onInputNumberClick = onEditLabelQuantity,
    )
}

@Composable
private fun LoadingState(isLoading: Boolean, modifier: Modifier = Modifier) {
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
        ) {
            LogoSpinner(
                modifier = Modifier
                    .size(80.dp)
                    .testTag(TestTags.Counts.Submit.LOADING_SPINNER)
            )

            Text(
                text = stringResource(R.string.returns_window_finding_shipping_dates),
                style = type.bodyTypeStyle.body3Bold,
                modifier = Modifier.padding(top = measurement.spacing.large)
            )
        }
    }
}

@FullDevicePreview
@Composable
private fun Default() {
    FullscreenPreview {
        ReturnsWindowContent(
            state = ReturnsWindowState(
                availablePickups = PickupAvailability.Sample,
                loading = false,
                shippingLabels = 10
            ),
            sendIntent = {}
        )
    }
}

@FullDevicePreview
@Composable
private fun WithSelected() {
    FullscreenPreview {
        ReturnsWindowContent(
            state = ReturnsWindowState(
                availablePickups = PickupAvailability.Sample,
                selectedIndex = 5,
                pageIndex = 2,
                loading = false,
                shippingLabels = 10
            ),
            sendIntent = {}
        )
    }
}

@FullDevicePreview
@Composable
private fun PreviewReturnsWindowLoadingContent() {
    FullscreenPreview {
        ReturnsWindowContent(
            state = ReturnsWindowState(
                availablePickups = PickupAvailability.Sample,
                loading = true
            ),
            sendIntent = {}
        )
    }
}
