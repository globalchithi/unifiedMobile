package com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.core.ui.component.VCScaffold
import com.vaxcare.unifiedhub.core.ui.component.button.TonalButton
import com.vaxcare.unifiedhub.core.ui.component.button.VCFloatingActionButton
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
fun ReturnReasonScreen(
    reasonConfirmed: (returnReason: ReturnReasonUi) -> Unit,
    navigateBack: () -> Unit,
    viewModel: ReturnsReasonViewModel = hiltViewModel(),
) {
    BaseMviScreen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                is ReturnReasonEvent.ReasonConfirmed -> reasonConfirmed(event.reason)
                ReturnReasonEvent.NavigateBack -> navigateBack()
            }
        }
    ) { state, sendIntent ->
        ProvideStock(state.stock) {
            ReturnReasonContent(state, sendIntent)
        }
    }
}

@Composable
fun ReturnReasonContent(state: ReturnReasonState, handleIntent: (ReturnReasonIntent) -> Unit) {
    VCScaffold(
        topBar = {
            BaseTitleBar(
                title = R.string.return_products,
                buttonIcon = DesignSystemR.drawable.ic_close,
                onButtonClick = { handleIntent(ReturnReasonIntent.GoBack) }
            )
        },
        fab = {
            VCFloatingActionButton(
                modifier = Modifier.testTag(TestTags.Returns.Reasons.NEXT_BUTTON),
                onClick = {
                    state.selectedReason?.let { selectedReason ->
                        handleIntent(ReturnReasonIntent.ConfirmReason)
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
            when (screenOrientation) {
                Configuration.ORIENTATION_PORTRAIT -> {
                    ReturnReasonsPortrait(
                        modifier = Modifier
                            .width(measurement.size.buttonsWizard)
                            .align(Alignment.CenterHorizontally),
                        selectedReason = state.selectedReason,
                        reasons = ReturnReasonUi.entries,
                        onReasonClick = { handleIntent(ReturnReasonIntent.SelectReason(it)) }
                    )
                }

                else -> {
                    ReturnReasonsLandscape(
                        selectedReason = state.selectedReason,
                        reasons = ReturnReasonUi.entries,
                        onReasonClick = { handleIntent(ReturnReasonIntent.SelectReason(it)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReturnReasonsPortrait(
    selectedReason: ReturnReasonUi?,
    reasons: List<ReturnReasonUi>,
    onReasonClick: (ReturnReasonUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.width(552.dp)
    ) {
        Text(
            text = stringResource(R.string.return_reason_title),
            style = type.bodyTypeStyle.body2
        )

        Spacer(modifier = Modifier.height(measurement.spacing.medium))

        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(measurement.spacing.buttonLg)
        ) {
            reasons.forEach { reason ->
                val isSelected = selectedReason == reason
                val text = stringResource(reason.menuTextRes)
                TonalButton(
                    modifier = Modifier
                        .testTag(TestTags.Returns.Reasons.reasonButton(text)),
                    onClick = { onReasonClick(reason) },
                    leadingIconRes = if (isSelected) {
                        DesignSystemR.drawable.ic_check
                    } else {
                        null
                    },
                    text = text,
                    highlight = isSelected
                )
            }
        }
    }
}

@Composable
private fun ReturnReasonsLandscape(
    selectedReason: ReturnReasonUi?,
    reasons: List<ReturnReasonUi>,
    onReasonClick: (ReturnReasonUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.width(1104.dp)
    ) {
        Text(
            text = stringResource(R.string.return_reason_title),
            style = type.bodyTypeStyle.body2,
            modifier = Modifier.padding(start = measurement.spacing.medium)
        )

        Spacer(modifier = Modifier.height(measurement.spacing.large))
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        reasons.chunked(3).forEach { partialReasons ->
            Column(
                Modifier
                    .width(measurement.size.buttonsWizard)
            ) {
                partialReasons.forEach { reason ->
                    val isSelected = selectedReason == reason
                    val label: String = stringResource(
                        when (reason) {
                            ReturnReasonUi.EXPIRED -> R.string.return_reason_expired
                            ReturnReasonUi.EXCESS_INVENTORY -> R.string.return_reason_excess
                            ReturnReasonUi.FRIDGE_OUT_OF_TEMP -> R.string.return_reason_fridge_out_of_temp
                            ReturnReasonUi.DELIVER_OUT_OF_TEMP -> R.string.return_reason_delivered_out_of_temp
                            ReturnReasonUi.RECALLED_BY_MANUFACTURER -> R.string.return_reason_recalled
                            ReturnReasonUi.DAMAGED_IN_TRANSIT -> R.string.return_reason_damaged
                        }
                    )
                    val text = stringResource(reason.menuTextRes)
                    TonalButton(
                        modifier = Modifier
                            .testTag(TestTags.Returns.Reasons.reasonButton(text)),
                        onClick = { onReasonClick(reason) },
                        text = text,
                        leadingIconRes = if (isSelected) {
                            DesignSystemR.drawable.ic_check
                        } else {
                            null
                        },
                        highlight = isSelected
                    )
                    Spacer(Modifier.height(measurement.spacing.buttonLg))
                }
            }
        }
    }
}

@FullDevicePreview
@Composable
fun PreviewReturnReasonContent() {
    VaxCareTheme {
        ReturnReasonContent(
            state = ReturnReasonState(selectedReason = ReturnReasonUi.EXCESS_INVENTORY),
            handleIntent = {}
        )
    }
}
