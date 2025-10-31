package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.complete

import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.common.ext.toStandardDate
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUiMapper
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.session.AddPublicSession
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.fold

@HiltViewModel(assistedFactory = AddPublicCompleteViewModel.Factory::class)
class AddPublicCompleteViewModel @AssistedInject constructor(
    @Assisted private val session: AddPublicSession,
    private val productUiMapper: ProductUiMapper,
    private val dispatcherProvider: DispatcherProvider,
) : BaseViewModel<AddPublicCompleteState, AddPublicCompleteEvent, AddPublicCompleteIntent>(
        AddPublicCompleteState()
    ) {
    @AssistedFactory
    interface Factory {
        fun create(session: AddPublicSession): AddPublicCompleteViewModel
    }

    override fun start() {
        viewModelScope.launch(dispatcherProvider.io) {
            val products = productUiMapper.sessionToUi(
                lotState = session.submittableLotState.first(),
                productState = session.productState.first(),
            )

            setState {
                val totalProducts: Int = products.fold(0) { acc, product ->
                    acc + product.getTotal()
                }

                copy(
                    stockType = StockUi.map(session.stockType),
                    date = LocalDate.now().toStandardDate(),
                    products = products,
                    totalProducts = totalProducts.toString()
                )
            }
        }
    }

    override fun handleIntent(intent: AddPublicCompleteIntent) {
        when (intent) {
            AddPublicCompleteIntent.BackToHome, AddPublicCompleteIntent.LogOut -> {
                sendEvent(AddPublicCompleteEvent.NavigateToHome)
            }
        }
    }
}
