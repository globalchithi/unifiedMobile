package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.complete

import androidx.compose.runtime.Immutable
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUi

sealed interface AddPublicCompleteIntent : UiIntent {
    data object LogOut : AddPublicCompleteIntent

    data object BackToHome : AddPublicCompleteIntent
}

sealed interface AddPublicCompleteEvent : UiEvent {
    data object NavigateToHome : AddPublicCompleteEvent
}

@Immutable
data class AddPublicCompleteState(
    val stockType: StockUi = StockUi.PRIVATE,
    val products: List<ProductUi> = emptyList(),
    val date: String = "",
    val totalProducts: String = "",
) : UiState
