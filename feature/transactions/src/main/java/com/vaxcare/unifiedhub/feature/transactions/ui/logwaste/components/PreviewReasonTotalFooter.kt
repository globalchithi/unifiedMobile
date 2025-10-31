package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.BROKEN_OR_CONTAMINATED
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.DELIVER_OUT_OF_TEMP
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.EXPIRED
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.OTHER
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.PREPPED_AND_NOT_ADMINISTERED
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.SPOILED_OR_OUT_OF_RANGE

@Composable
fun ReasonAndTotalFooter(
    reason: LogWasteReason,
    total: Int,
    stockName: String,
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
                .padding(start = measurement.spacing.large, end = 184.dp),
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

                val reason = stringResource(
                    when (reason) {
                        SPOILED_OR_OUT_OF_RANGE -> R.string.log_waste_reason_spoiled_or_out_of_range
                        BROKEN_OR_CONTAMINATED -> R.string.log_waste_reason_broken_or_contaminated
                        PREPPED_AND_NOT_ADMINISTERED -> R.string.log_waste_reason_prepped_and_not_administered
                        EXPIRED -> R.string.expired
                        DELIVER_OUT_OF_TEMP -> R.string.log_waste_reason_delivered_out_of_temp
                        OTHER -> R.string.log_waste_reason_other
                    }
                )
                Text(
                    stringResource(
                        R.string.log_waste_footer_reason,
                        reason,
                        stockName
                    ),
                    style = type.bodyTypeStyle.body3
                )
            }

            Spacer(modifier = Modifier.width(measurement.spacing.medium))

            Column(
                verticalArrangement = Arrangement.spacedBy(measurement.spacing.xSmall),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    stringResource(R.string.total),
                    style = type.bodyTypeStyle.label
                )

                AnimatedContent(
                    targetState = total,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInVertically { -it } togetherWith slideOutVertically { it }
                        } else {
                            slideInVertically { it } togetherWith slideOutVertically { -it }
                        }
                    }
                ) { total ->
                    Text(
                        total.toString(),
                        style = type.headerTypeStyle.headlineMediumBold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 704, heightDp = 104)
@Composable
private fun PreviewReasonTotalFooter() {
    VaxCareTheme {
        ReasonAndTotalFooter(
            reason = PREPPED_AND_NOT_ADMINISTERED,
            total = 6,
            stockName = StockUi.PRIVATE.prettyName,
        )
    }
}
