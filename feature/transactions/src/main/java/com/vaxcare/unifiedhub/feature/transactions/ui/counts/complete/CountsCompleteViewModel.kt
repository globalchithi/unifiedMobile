package com.vaxcare.unifiedhub.feature.transactions.ui.counts.complete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.common.ext.toStandardDate
import com.vaxcare.unifiedhub.core.common.ext.toUSD
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.domain.ClearUserSessionUseCase
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.navigation.CountsCompleteRoute
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.complete.CountsCompleteEvent.NavigateToHome
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.complete.CountsCompleteIntent.BackToHome
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.complete.CountsCompleteIntent.LogOut
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@HiltViewModel
class CountsCompleteViewModel @Inject constructor(
    private val clearUserSessionUseCase: ClearUserSessionUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val lotInventoryRepository: LotInventoryRepository,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<CountsCompleteState, CountsCompleteEvent, CountsCompleteIntent>(
        initialState = CountsCompleteState()
    ) {
    private val args
        get() = savedStateHandle.toRoute<CountsCompleteRoute>()

    override fun start() {
        viewModelScope.launch(dispatcherProvider.io) {
            val inventoryBalance = lotInventoryRepository
                .getLotInventoryTotalValue(args.stockType)

            setState {
                val defaultState = copy(
                    stockType = StockUi.map(args.stockType),
                    date = LocalDate.now().toStandardDate(),
                )

                val isPrivate = args.stockType == StockType.PRIVATE
                val hasVariance = ((args.addedUnits ?: 0) + (args.missingUnits ?: 0)) > 0
                when {
                    isPrivate && hasVariance -> {
                        val addedImpact = args.addedImpact ?: 0F
                        val missingImpact = args.missingImpact ?: 0F
                        val totalImpact = addedImpact + missingImpact
                        val disclaimerRes = if (totalImpact > 0) {
                            DesignSystemR.string.payment_disclaimer
                        } else {
                            DesignSystemR.string.invoice_disclaimer
                        }

                        defaultState.copy(
                            showImpact = true,
                            showVariance = true,
                            disclaimerRes = disclaimerRes,
                            inventoryBalance = (inventoryBalance + totalImpact).toUSD(),
                            addedUnits = args.addedUnits,
                            addedImpact = addedImpact.toUSD(),
                            missingUnits = args.missingUnits,
                            missingImpact = missingImpact.toUSD(),
                            totalImpact = totalImpact.toUSD()
                        )
                    }

                    hasVariance -> {
                        defaultState.copy(
                            showImpact = false,
                            showVariance = true,
                            inventoryBalance = inventoryBalance.toUSD(),
                            addedUnits = args.addedUnits,
                            missingUnits = args.missingUnits,
                        )
                    }

                    else -> {
                        defaultState.copy(
                            showImpact = false,
                            showVariance = false,
                            inventoryBalance = inventoryBalance.toUSD(),
                            totalProducts = args.products,
                            totalUnits = args.units,
                        )
                    }
                }
            }
        }
    }

    override fun handleIntent(intent: CountsCompleteIntent) {
        when (intent) {
            is LogOut -> {
                viewModelScope.launch(dispatcherProvider.io) {
                    clearUserSessionUseCase()

                    withContext(dispatcherProvider.main) {
                        sendEvent(NavigateToHome)
                    }
                }
            }

            is BackToHome -> {
                sendEvent(NavigateToHome)
            }
        }
    }
}
