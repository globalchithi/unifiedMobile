package com.vaxcare.unifiedhub.feature.transactions.ui.returns.complete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.common.ext.toStandardDate
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.navigation.ReturnsCompleteRoute
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.ProductUiMapper
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.getDisplayText
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.session.ReturnsSession
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel(assistedFactory = ReturnsCompleteViewModel.Factory::class)
class ReturnsCompleteViewModel @AssistedInject constructor(
    @Assisted private val session: ReturnsSession,
    private val productUiMapper: ProductUiMapper,
    private val dispatcherProvider: DispatcherProvider,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<ReturnsCompleteState, ReturnsCompleteEvent, ReturnsCompleteIntent>(
        ReturnsCompleteState()
    ) {
    @AssistedFactory
    interface Factory {
        fun create(session: ReturnsSession): ReturnsCompleteViewModel
    }

    private val args
        get() = savedStateHandle.toRoute<ReturnsCompleteRoute>()

    override fun start() {
        viewModelScope.launch(dispatcherProvider.io) {
            val products = productUiMapper.sessionToUi(
                lotState = session.submittableLotState.first(),
            )

            setState {
                val totalProducts: Int = products.fold(0) { acc, product ->
                    acc + product.quantity
                }

                copy(
                    stockType = StockUi.map(args.stockType),
                    reason = args.reason,
                    date = LocalDate.now().toStandardDate(),
                    shipmentPickup = session.pickup?.getDisplayText(),
                    products = products,
                    totalProducts = totalProducts.toString(),
                )
            }
        }
    }

    override fun handleIntent(intent: ReturnsCompleteIntent) {
        when (intent) {
            ReturnsCompleteIntent.BackToHome, ReturnsCompleteIntent.LogOut -> {
                sendEvent(ReturnsCompleteEvent.NavigateToHome)
            }
        }
    }
}
