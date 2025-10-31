package com.vaxcare.unifiedhub.feature.transactions.ui.returns.complete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.vaxcare.unifiedhub.core.designsystem.theme.FontWeights
import com.vaxcare.unifiedhub.core.model.inventory.ReturnReason
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.ProductUi

sealed interface ReturnsCompleteIntent : UiIntent {
    data object LogOut : ReturnsCompleteIntent

    data object BackToHome : ReturnsCompleteIntent
}

sealed interface ReturnsCompleteEvent : UiEvent {
    data object NavigateToHome : ReturnsCompleteEvent
}

@Immutable
data class ReturnsCompleteState(
    val stockType: StockUi = StockUi.PRIVATE,
    val reason: ReturnReason = ReturnReason.EXPIRED,
    val products: List<ProductUi> = emptyList(),
    val date: String = "",
    val shipmentPickup: String? = null,
    val totalProducts: String = "",
) : UiState {
    @Composable
    fun getTitle(): String =
        if (stockType == StockUi.PRIVATE) {
            stringResource(R.string.returns_complete_almost_done)
        } else {
            stringResource(R.string.returns_complete_nice_work)
        }

    @Composable
    fun getDescription(): AnnotatedString {
        val isPrivate = stockType == StockUi.PRIVATE
        val (line1, line2, line1Bold) = when (reason) {
            ReturnReason.EXPIRED -> {
                if (isPrivate) {
                    Triple(
                        first = R.string.returns_complete_check_your_email,
                        second = R.string.returns_complete_products_removed_private_expired,
                        third = true
                    )
                } else {
                    Triple(
                        first = R.string.returns_complete_inventory_will_be_updated,
                        second = R.string.returns_complete_follow_state_guidelines_expired_doses,
                        third = false
                    )
                }
            }

            ReturnReason.EXCESS_INVENTORY,
            ReturnReason.DELIVER_OUT_OF_TEMP,
            ReturnReason.RECALLED_BY_MANUFACTURER,
            ReturnReason.DAMAGED_IN_TRANSIT -> {
                if (isPrivate) {
                    Triple(
                        first = R.string.returns_complete_vxc_will_reach_out,
                        second = R.string.returns_complete_product_removed_private,
                        third = false
                    )
                } else {
                    Triple(
                        first = R.string.returns_complete_inventory_will_be_updated,
                        second = R.string.returns_complete_follow_state_guidelines_products,
                        third = false
                    )
                }
            }

            ReturnReason.FRIDGE_OUT_OF_TEMP -> {
                if (isPrivate) {
                    Triple(
                        first = R.string.returns_complete_check_your_email,
                        second = R.string.returns_complete_product_removed_private,
                        third = false
                    )
                } else {
                    Triple(
                        first = R.string.returns_complete_inventory_will_be_updated,
                        second = R.string.returns_complete_follow_state_guidelines_products,
                        third = false
                    )
                }
            }
        }

        return buildAnnotatedString {
            if (line1Bold) pushStyle(SpanStyle(fontWeight = FontWeights.WeightSemiBold))
            append(stringResource(line1))
            if (line1Bold) pop()
            append("\n\n")
            append(stringResource(line2))
        }
    }
}
