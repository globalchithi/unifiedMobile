package com.vaxcare.unifiedhub.feature.transactions.ui.common.confirmstock

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.common.confirmstock.ConfirmStockEvent.StockConfirmed
import com.vaxcare.unifiedhub.feature.transactions.ui.common.confirmstock.metrics.StockConfirmedMetric
import com.vaxcare.unifiedhub.feature.transactions.ui.common.confirmstock.navigation.ConfirmStockRoute
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConfirmStockViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val dispatchers: DispatcherProvider,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<ConfirmStockState, ConfirmStockEvent, ConfirmStockIntent>(ConfirmStockState()) {
    private val args = savedStateHandle.toRoute<ConfirmStockRoute>()

    override fun start() {
        viewModelScope.launch(dispatchers.io) {
            locationRepository
                .getStockTypes()
                .catch { Timber.e(it) }
                .take(1)
                .collect { stocks ->
                    val stocksToDisplay = if (args.publicStocksOnly) {
                        stocks.filter { it != StockType.PRIVATE }
                    } else {
                        stocks
                    }
                    setState {
                        copy(
                            title = args.title,
                            subtitle = args.subtitle,
                            stocks = stocksToDisplay.map(StockUi::map),
                            selectedStock = StockUi.map(args.preselectedStock)
                        )
                    }
                }
        }
    }

    override fun handleIntent(intent: ConfirmStockIntent) {
        when (intent) {
            is ConfirmStockIntent.StockSelected -> setState {
                copy(selectedStock = intent.stock)
            }

            ConfirmStockIntent.ConfirmStock -> {
                val selectedStock = currentState().selectedStock.toDomain()
                viewModelScope.launch(dispatchers.io) {
                    analyticsRepository.track(
                        StockConfirmedMetric(
                            stock = selectedStock
                        )
                    )
                }

                sendEvent(StockConfirmed(selectedStock))
            }

            ConfirmStockIntent.Close -> sendEvent(ConfirmStockEvent.GoBack)
        }
    }
}
