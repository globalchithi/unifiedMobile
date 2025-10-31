package com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.common.ext.toLocalDateString
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.core.ui.component.LogoSpinnerWithExit
import com.vaxcare.unifiedhub.core.ui.component.ModalStaticSheet
import com.vaxcare.unifiedhub.core.ui.component.ModalStaticSheetNavigationOption
import com.vaxcare.unifiedhub.core.ui.component.SingleSelectionMenuItem
import com.vaxcare.unifiedhub.core.ui.component.TextField
import com.vaxcare.unifiedhub.core.ui.component.TextFieldState
import com.vaxcare.unifiedhub.core.ui.component.VCDatePickerDialog
import com.vaxcare.unifiedhub.core.ui.component.VCScaffold
import com.vaxcare.unifiedhub.core.ui.component.button.TonalButton
import com.vaxcare.unifiedhub.core.ui.component.button.VCFloatingActionButton
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.LotForm
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.PresentationUI
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.ProductUI
import java.time.LocalDate
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
fun AddLotScreen(
    viewModel: AddLotViewModel = hiltViewModel(),
    onConfirm: (newLotNumber: String) -> Unit,
    onClose: () -> Unit
) {
    BaseMviScreen<AddLotState, AddLotEvent, AddLotIntent>(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                AddLotEvent.Close -> onClose()
                is AddLotEvent.ConfirmLot -> onConfirm(event.newLot)
            }
        }
    ) { state, handleIntent ->
        AddLotContent(onClose, state, handleIntent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLotContent(
    onClose: () -> Unit,
    state: AddLotState,
    handleIntent: (AddLotIntent) -> Unit
) {
    if (state.loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = VaxCareTheme.color.surface.surfaceBright)
        ) {
            var isAnimating by remember { mutableStateOf(true) }
            if (isAnimating) {
                LogoSpinnerWithExit(
                    isLoading = true,
                    onFinish = { isAnimating = false },
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
    VCScaffold(
        topBar = {
            BaseTitleBar(
                buttonIcon = DesignSystemR.drawable.ic_close,
                onButtonClick = onClose,
                title = stringResource(
                    R.string.add_new_lot_top_bar_title,
                    state.form.lotNumber ?: ""
                )
            )
        },
        fab = {
            VCFloatingActionButton(
                onClick = { handleIntent(AddLotIntent.CreateLot) },
                iconPainter = painterResource(DesignSystemR.drawable.ic_check),
                enabled = state.isCheckEnabled,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = VaxCareTheme.measurement.spacing.topBarLargeY),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = VaxCareTheme.measurement.size.buttonsWizard),
                verticalArrangement = Arrangement.spacedBy(VaxCareTheme.measurement.spacing.buttonLg),
            ) {
                TonalButton(
                    modifier = Modifier.height(62.dp),
                    text = state.form.antigen
                        ?: stringResource(R.string.add_new_lot_select_antigen),
                    onClick = { handleIntent(AddLotIntent.OpenAntigenPicker) },
                    leadingIconRes = DesignSystemR.drawable.ic_edit,
                    highlight = !state.form.antigen.isNullOrEmpty(),
                    enabled = !state.form.isPreSelected
                )
                TonalButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp),
                    text = state.form.product?.name
                        ?: stringResource(R.string.add_new_lot_select_product),
                    onClick = { handleIntent(AddLotIntent.OpenProductPicker) },
                    enabled = !state.form.antigen.isNullOrEmpty() && !state.form.isPreSelected,
                    leadingIconRes = DesignSystemR.drawable.ic_edit,
                    highlight = state.form.product != null
                )
                TonalButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp),
                    text = stringResource(
                        state.form.presentation?.name ?: R.string.add_new_lot_select_presentation
                    ),
                    onClick = { handleIntent(AddLotIntent.OpenPresentationPicker) },
                    enabled = !state.form.product
                        ?.name
                        .isNullOrEmpty() &&
                        !state.form.isPreSelected,
                    leadingIconRes = DesignSystemR.drawable.ic_edit,
                    highlight = state.form.presentation != null
                )
            }

            Spacer(modifier = Modifier.height(VaxCareTheme.measurement.spacing.xLarge))

            TextField(
                modifier = Modifier
                    .widthIn(max = VaxCareTheme.measurement.size.buttonsWizard),
                label = stringResource(R.string.add_new_lot_expiration),
                value = state.form.expirationDate?.toLocalDateString() ?: "",
                placeholder = stringResource(R.string.add_new_lot_set_date),
                trailingDrawRes = DesignSystemR.drawable.ic_calendar,
                state = when {
                    state.form.presentation == null -> TextFieldState.DISABLED
                    state.form.expirationDate != null && !state.form.isValidExpiration -> TextFieldState.ERROR
                    else -> TextFieldState.ENABLED
                },
                supportingText = if (state.form.expirationDate != null && !state.form.isValidExpiration) {
                    stringResource(
                        R.string.add_new_lot_invalid_expiration_date
                    )
                } else {
                    null
                },
                readOnly = true,
                onClick = { handleIntent(AddLotIntent.OpenExpirationPicker) },
                trailingIconClick = { handleIntent(AddLotIntent.OpenExpirationPicker) },
                onValueChange = { /* Read only */ }
            )
        }
    }
    when (state.activeDialog) {
        is AddLotDialog.AntigenPicker -> {
            var preSelectedAntigen: String? by rememberSaveable { mutableStateOf(state.form.antigen) }

            ModalStaticSheet(
                title = stringResource(R.string.add_new_lot_select_antigen),
                cancelButtonText = stringResource(DesignSystemR.string.cancel),
                confirmButtonText = stringResource(DesignSystemR.string.save),
                confirmButtonEnabled = !preSelectedAntigen.isNullOrEmpty(),
                navigationOption = ModalStaticSheetNavigationOption.CLOSE,
                onCancelRequest = { handleIntent(AddLotIntent.CloseDialog) },
                onConfirmRequest = {
                    preSelectedAntigen?.let {
                        handleIntent(AddLotIntent.AntigenPicked(it))
                    }
                },
                onDismissRequest = { handleIntent(AddLotIntent.CloseDialog) },
                onNavigateBackRequest = { handleIntent(AddLotIntent.CloseDialog) }
            ) {
                val antigens = state.activeDialog.options
                LazyColumn {
                    items(antigens.size) { index ->
                        val currentAntigen = antigens[index]
                        SingleSelectionMenuItem(
                            currentAntigen,
                            selected = preSelectedAntigen == currentAntigen,
                            onClick = { preSelectedAntigen = currentAntigen }
                        )
                    }
                }
            }
        }

        is AddLotDialog.ProductPicker -> {
            var preSelectedProductId: Int? by rememberSaveable { mutableStateOf(state.form.product?.id) }

            ModalStaticSheet(
                title = stringResource(R.string.add_new_lot_select_product),
                cancelButtonText = stringResource(DesignSystemR.string.cancel),
                confirmButtonText = stringResource(DesignSystemR.string.save),
                confirmButtonEnabled = preSelectedProductId != null,
                navigationOption = ModalStaticSheetNavigationOption.CLOSE,
                onCancelRequest = { handleIntent(AddLotIntent.CloseDialog) },
                onConfirmRequest = {
                    state.activeDialog.options.find { it.id == preSelectedProductId }?.let {
                        handleIntent(AddLotIntent.ProductPicked(it))
                    }
                },
                onDismissRequest = { handleIntent(AddLotIntent.CloseDialog) },
                onNavigateBackRequest = { handleIntent(AddLotIntent.CloseDialog) }
            ) {
                val products = state.activeDialog.options
                LazyColumn {
                    items(products.size, key = { index -> products[index].id }) { index ->
                        val currentProduct = products[index]
                        SingleSelectionMenuItem(
                            currentProduct.name,
                            selected = preSelectedProductId == currentProduct.id,
                            onClick = { preSelectedProductId = currentProduct.id }
                        )
                    }
                }
            }
        }

        is AddLotDialog.PresentationPicker -> {
            var preSelectedPresentation: Int? by rememberSaveable {
                mutableStateOf(state.form.presentation?.name)
            }

            ModalStaticSheet(
                title = stringResource(R.string.add_new_lot_select_presentation),
                cancelButtonText = stringResource(DesignSystemR.string.cancel),
                confirmButtonText = stringResource(DesignSystemR.string.save),
                confirmButtonEnabled = preSelectedPresentation != null,
                navigationOption = ModalStaticSheetNavigationOption.CLOSE,
                onCancelRequest = { handleIntent(AddLotIntent.CloseDialog) },
                onConfirmRequest = {
                    state.activeDialog.options
                        .find { it.name == preSelectedPresentation }
                        ?.let { presentation ->
                            handleIntent(AddLotIntent.PresentationPicked(presentation))
                        }
                },
                onDismissRequest = { handleIntent(AddLotIntent.CloseDialog) },
                onNavigateBackRequest = { handleIntent(AddLotIntent.CloseDialog) }
            ) {
                val presentations = state.activeDialog.options
                LazyColumn {
                    items(presentations.size, key = { index -> presentations[index].name }) { index ->
                        val currentPresentation = presentations[index]
                        SingleSelectionMenuItem(
                            title = stringResource(currentPresentation.name),
                            trailingIconDrawRes = currentPresentation.iconRes,
                            selected = preSelectedPresentation == currentPresentation.name,
                            onClick = { preSelectedPresentation = currentPresentation.name }
                        )
                    }
                }
            }
        }

        is AddLotDialog.ExpirationPicker -> {
            VCDatePickerDialog(
                initialSelectedDate = state.activeDialog.expirationDate,
                onDismissRequest = { handleIntent(AddLotIntent.CloseDialog) },
                onDateSelected = { expirationSelected ->
                    handleIntent(
                        AddLotIntent.ExpirationPicked(
                            expirationSelected
                        )
                    )
                }
            )
        }

        null -> {
            // Show nothing
        }
    }
}

@FullDevicePreview
@Composable
fun PreviewAddLotContent() {
    VaxCareTheme {
        AddLotContent(
            onClose = {},
            state = AddLotState(
                loading = true,
                form = LotForm(
                    lotNumber = "AE94250730",
                    antigen = "Hep-B",
                    product = ProductUI(1, "Hep-B"),
                    presentation = PresentationUI(
                        id = 0,
                        R.string.add_new_lot_expiration,
                        DesignSystemR.drawable.ic_presentation_single_tube
                    ),
                    expirationDate = LocalDate.now()
                ),
                activeDialog = null
            ),
            handleIntent = {}
        )
    }
}

@FullDevicePreview
@Composable
fun PreviewPortraitAddLotContentWithDialog() {
    VaxCareTheme {
        AddLotContent(
            onClose = {},
            state = AddLotState(
                loading = false,
                form = LotForm(
                    lotNumber = "AE94250730",
                    antigen = "Hep-B",
                    product = ProductUI(1, "Hep-B"),
                    presentation = PresentationUI(
                        id = 0,
                        R.string.add_new_lot_expiration,
                        DesignSystemR.drawable.ic_presentation_single_tube
                    ),
                    expirationDate = LocalDate.now()
                ),
                activeDialog = AddLotDialog.AntigenPicker(
                    listOf(
                        "Hep-B",
                        "VCastillo",
                        "T-DAP"
                    )
                )
            ),
            handleIntent = {}
        )
    }
}

@FullDevicePreview
@Composable
fun PreviewPortraitAddLotContentWithDatePicker() {
    VaxCareTheme {
        AddLotContent(
            onClose = {},
            state = AddLotState(
                loading = false,
                form = LotForm(
                    lotNumber = "AE94250730",
                    antigen = "Hep-B",
                    product = ProductUI(1, "Hep-B"),
                    presentation = PresentationUI(
                        id = 0,
                        R.string.add_new_lot_expiration,
                        DesignSystemR.drawable.ic_presentation_single_tube
                    ),
                    expirationDate = LocalDate.now()
                ),
                activeDialog = AddLotDialog.ExpirationPicker(
                    LocalDate.now()
                )
            ),
            handleIntent = {}
        )
    }
}
