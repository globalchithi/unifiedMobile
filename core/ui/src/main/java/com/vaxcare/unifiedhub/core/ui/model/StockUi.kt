package com.vaxcare.unifiedhub.core.ui.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

enum class StockUi(
    val prettyName: String,
    @DrawableRes val icon: Int,
) {
    PRIVATE("Private", DesignSystemR.drawable.ic_large_syringe_purple),
    VFC("VFC", DesignSystemR.drawable.ic_large_syringe_green),
    STATE("State", DesignSystemR.drawable.ic_large_syringe_pink),
    THREE_SEVENTEEN("317", DesignSystemR.drawable.ic_large_syringe_blue);

    val colors: StockColors
        @Composable get() = with(VaxCareTheme.color.container) {
            when (this@StockUi) {
                PRIVATE -> StockColors(secondaryContainer, inversePrimary)
                VFC -> StockColors(stockVfcContainer, successContainer)
                STATE -> StockColors(stockStateContainer, stockStateContainerLight)
                THREE_SEVENTEEN -> StockColors(stockThreeSevenTeenContainer, stockThreeSevenTeenContainerLight)
            }
        }

    fun toDomain(): StockType =
        when (this) {
            PRIVATE -> StockType.PRIVATE
            VFC -> StockType.VFC
            STATE -> StockType.STATE
            THREE_SEVENTEEN -> StockType.THREE_SEVENTEEN
        }

    companion object {
        fun map(value: StockType): StockUi =
            when (value) {
                StockType.PRIVATE -> PRIVATE
                StockType.VFC -> VFC
                StockType.STATE -> STATE
                StockType.THREE_SEVENTEEN -> THREE_SEVENTEEN
                else -> PRIVATE
            }
    }
}
