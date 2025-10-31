package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.core.ui.component.VCScaffold
import com.vaxcare.unifiedhub.core.ui.component.button.TonalButton
import com.vaxcare.unifiedhub.core.ui.component.button.VCFloatingActionButton
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.dialog.ReturnExpiredProductsDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.dialog.ReturnProductsDeliveredOutOfTempDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.BROKEN_OR_CONTAMINATED
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.DELIVER_OUT_OF_TEMP
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.EXPIRED
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.OTHER
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.PREPPED_AND_NOT_ADMINISTERED
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.SPOILED_OR_OUT_OF_RANGE
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
fun LogWasteReasonScreen(
    reasonConfirmed: (logWasteReason: LogWasteReason) -> Unit,
    navigateBack: () -> Unit,
    returnProducts: (LogWasteReason) -> Unit,
    viewModel: LogWasteReasonViewModel = hiltViewModel(),
) {
    BaseMviScreen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                is LogWasteReasonEvent.ReasonConfirmed -> reasonConfirmed(event.reason)
                LogWasteReasonEvent.NavigateBack -> navigateBack()
            }
        }
    ) { state, sendIntent ->
        ProvideStock(state.stock) {
            LogWasteReasonContent(state, sendIntent)
        }
    }

    when (viewModel.currentState().activeDialog) {
        LogWasteReasonDialog.ReturnExpiredProducts -> ReturnExpiredProductsDialog(
            onCancel = {
                viewModel.handleIntent(LogWasteReasonIntent.CloseDialog)
            },
            onReturnDoses = {
                returnProducts(EXPIRED)
            }
        )
        LogWasteReasonDialog.ReturnProductsDeliveredOutOfTemp -> ReturnProductsDeliveredOutOfTempDialog(
            onCancel = {
                viewModel.handleIntent(LogWasteReasonIntent.CloseDialog)
            },
            onReturnProducts = {
                returnProducts(DELIVER_OUT_OF_TEMP)
            }
        )
        null -> {
            // Do nothing
        }
    }
}

@Composable
fun LogWasteReasonContent(state: LogWasteReasonState, handleIntent: (LogWasteReasonIntent) -> Unit) {
    VCScaffold(
        topBar = {
            BaseTitleBar(
                title = R.string.log_waste,
                buttonIcon = DesignSystemR.drawable.ic_close,
                onButtonClick = { handleIntent(LogWasteReasonIntent.GoBack) }
            )
        },
        fab = {
            VCFloatingActionButton(
                onClick = {
                    state.selectedReason?.let { selectedReason ->
                        handleIntent(LogWasteReasonIntent.ConfirmReason)
                    }
                },
                iconPainter = painterResource(DesignSystemR.drawable.ic_arrow_forward),
                enabled = state.selectedReason != null
            )
        }
    ) {
        val screenOrientation = LocalConfiguration.current.orientation
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = when (screenOrientation) {
                        Configuration.ORIENTATION_PORTRAIT -> {
                            measurement.spacing.topBarXLargeY
                        }

                        Configuration.ORIENTATION_LANDSCAPE -> {
                            measurement.spacing.topBarLargeY
                        }

                        else -> measurement.spacing.topBarMediumY
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.width(552.dp)
            ) {
                Text(
                    text = stringResource(R.string.log_waste_reason_title),
                    style = type.bodyTypeStyle.body2
                )

                Spacer(modifier = Modifier.padding(measurement.spacing.small))

                LogWasteReasons(
                    modifier = Modifier
                        .width(measurement.size.buttonsWizard)
                        .align(Alignment.CenterHorizontally),
                    selectedReason = state.selectedReason,
                    reasons = LogWasteReason.entries,
                    onReasonClick = { handleIntent(LogWasteReasonIntent.SelectReason(it)) }
                )
            }
        }
    }
}

@Composable
private fun LogWasteReasons(
    selectedReason: LogWasteReason?,
    reasons: List<LogWasteReason>,
    onReasonClick: (LogWasteReason) -> Unit,
    modifier: Modifier = Modifier
) {
    val screenOrientation = LocalConfiguration.current.orientation

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            when (screenOrientation) {
                Configuration.ORIENTATION_PORTRAIT -> {
                    measurement.spacing.buttonLg
                }

                Configuration.ORIENTATION_LANDSCAPE -> {
                    measurement.spacing.buttonSmallY
                }

                else -> measurement.spacing.buttonSmallY
            }
        )
    ) {
        reasons.forEach { reason ->
            val label: String = stringResource(
                when (reason) {
                    SPOILED_OR_OUT_OF_RANGE -> R.string.log_waste_reason_spoiled_or_out_of_range
                    BROKEN_OR_CONTAMINATED -> R.string.log_waste_reason_broken_or_contaminated
                    PREPPED_AND_NOT_ADMINISTERED -> R.string.log_waste_reason_prepped_and_not_administered
                    EXPIRED -> R.string.log_waste_reason_expired
                    DELIVER_OUT_OF_TEMP -> R.string.log_waste_reason_delivered_out_of_temp
                    OTHER -> R.string.log_waste_reason_other
                }
            )
            val isSelected = selectedReason == reason
            TonalButton(
                onClick = { onReasonClick(reason) },
                text = label,
                highlight = isSelected,
                leadingIconRes = if (isSelected) DesignSystemR.drawable.ic_check else null,
            )
        }
    }
}

@FullDevicePreview
@Composable
fun PreviewLogWasteReasonContent() {
    VaxCareTheme {
        LogWasteReasonContent(
            state = LogWasteReasonState(),
            handleIntent = {}
        )
    }
}
