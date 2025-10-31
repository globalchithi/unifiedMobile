package com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit

import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.common.ext.toUSD
import com.vaxcare.unifiedhub.core.data.repository.CountRepository
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.model.Count
import com.vaxcare.unifiedhub.core.model.inventory.LotInventory
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.Icons
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.metric.CountFinishMetric
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.model.CountTotals
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.session.CountSession
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitEvent.NavigateBack
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitEvent.NavigateToConfirmation
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitIntent.ConfirmCount
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitIntent.DismissDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitIntent.GoBack
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitIntent.RetrySubmit
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.model.ProductUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.util.CountsTokens.seasonalAntigens
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.math.abs

@HiltViewModel(assistedFactory = CountsSubmitViewModel.Factory::class)
class CountsSubmitViewModel @AssistedInject constructor(
    @Assisted private val countSession: CountSession,
    private val countRepository: CountRepository,
    private val lotRepository: LotRepository,
    private val lotInventoryRepository: LotInventoryRepository,
    private val productRepository: ProductRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val analyticsRepository: AnalyticsRepository
) : BaseViewModel<CountsSubmitState, CountsSubmitEvent, CountsSubmitIntent>(
        initialState = CountsSubmitState()
    ) {
    @AssistedFactory
    interface Factory {
        fun create(session: CountSession): CountsSubmitViewModel
    }

    lateinit var totals: CountTotals

    override fun start() {
        viewModelScope.launch(dispatcherProvider.io) {
            setState { copy(showImpact = countSession.stockType == StockType.PRIVATE) }

            lotInventoryRepository
                .getLotInventory(countSession.stockType)
                .combine(countSession.addedLots) { lotInventory, addedLots ->
                    lotInventory + addedLots.map { (_, lotNumber) ->
                        LotInventory(
                            lotNumber = lotNumber,
                            onHand = 0,
                            inventorySourceId = countSession.stockType.id
                        )
                    }
                }.combine(countSession.confirmedIds) { lotInventory, confirmedIds ->
                    lotInventory to confirmedIds
                }.combine(countSession.lotState) { (lotInventory, confirmedIds), lotState ->

                    val lotNumbers = lotInventory.map { it.lotNumber }
                    val lots = lotNumbers
                        .let { lotRepository.getLotsByNumber(it) }
                        .groupBy { it.productId }
                    val confirmedProducts = productRepository
                        .getProductsByLotNumber(lotNumbers)
                        .filter { confirmedIds.contains(it.id) }
                    val inventoryByLotNumber = lotInventory.associateBy {
                        it.lotNumber
                    }

                    var addedUnits = 0
                    var addedUnitImpact = 0F
                    var missingUnits = 0
                    var missingUnitImpact = 0F
                    val result = confirmedProducts.map { product ->

                        val productLots = lots[product.id] ?: listOf()
                        val productInventory = productLots.map { lot ->
                            inventoryByLotNumber[lot.lotNumber]!!
                        }

                        var onHand = 0
                        var delta = 0
                        productInventory.forEach {
                            val state = lotState[it.lotNumber]
                            delta += if (state?.isDeleted == true) {
                                -it.onHand
                            } else {
                                lotState[it.lotNumber]?.delta ?: 0
                            }
                            onHand += it.onHand
                        }

                        val impact = (product.lossFee ?: 0F) * delta
                        if (delta < 0) {
                            missingUnits += abs(delta)
                            missingUnitImpact += impact
                        } else {
                            addedUnits += delta
                            addedUnitImpact += impact
                        }

                        val impactText = if (impact == 0F) {
                            "--"
                        } else {
                            impact.toUSD()
                        }
                        ProductUi(
                            id = product.id,
                            antigen = product.antigen,
                            prettyName = product.displayName,
                            quantity = onHand,
                            unitPrice = (product.lossFee ?: 0F).toUSD(),
                            delta = delta,
                            impact = impactText,
                            presentationIcon = Icons.presentationIcon(product.presentation),
                        )
                    } to (addedUnitImpact + missingUnitImpact)

                    // cache the totals so we can pass them along to the complete screen
                    totals = when {
                        // no count variance
                        addedUnits == 0 && missingUnits == 0 -> {
                            CountTotals(
                                products = result.first.size,
                                units = result.first.sumOf { it.quantity },
                            )
                        }

                        // variance with monetary impact
                        countSession.stockType == StockType.PRIVATE -> {
                            CountTotals(
                                addedUnits = addedUnits,
                                addedImpact = addedUnitImpact,
                                missingUnits = missingUnits,
                                missingImpact = missingUnitImpact
                            )
                        }

                        // variance with no monetary impact
                        else -> {
                            CountTotals(
                                addedUnits = addedUnits,
                                missingUnits = missingUnits,
                            )
                        }
                    }

                    return@combine result
                }.collectLatest { (products, totalImpact) ->
                    val (seasonal, nonSeasonal) = products.partition {
                        it.antigen.uppercase() in seasonalAntigens
                    }

                    setState {
                        copy(
                            stockType = StockUi.map(countSession.stockType),
                            nonSeasonalProducts = nonSeasonal,
                            seasonalProducts = seasonal,
                            subTotal = totalImpact.toUSD()
                        )
                    }
                }
        }
    }

    override fun handleIntent(intent: CountsSubmitIntent) {
        when (intent) {
            is ConfirmCount, RetrySubmit -> {
                submitCount()
            }

            is GoBack -> {
                sendEvent(NavigateBack)
            }

            is DismissDialog -> {
                setState {
                    copy(activeDialog = null)
                }
            }
        }
    }

    private fun submitCount() {
        viewModelScope.launch(dispatcherProvider.io) {
            setState {
                copy(
                    isLoading = true,
                    activeDialog = null
                )
            }

            // call the repository with the required data
            val stock = countSession.stockType
            val count = Count(
                stock = stock,
                lotEntries = countSession.fullLotState.value.mapValues {
                    if (it.value.isDeleted) {
                        -(lotInventoryRepository.getLotInventoryAsync(it.key, stock)?.onHand ?: 0)
                    } else {
                        it.value.delta
                    } to it.value.receiptKey
                },
                countGuid = countSession.countGuid,
                groupGuid = countSession.groupGuid,
                transactionKey = countSession.transactionKey
            )
            val successful = countRepository.submitCount(
                count = count,
                confirmedIds = countSession.confirmedIds.value
            )

            trackAdjustmentFinished(successful, count)
            if (!successful) {
                setState {
                    copy(
                        isLoading = false,
                        activeDialog = CountsSubmitDialog.SubmissionFailed
                    )
                }
                return@launch
            }

            sendEvent(event = NavigateToConfirmation(totals))
        }
    }

    private suspend fun trackAdjustmentFinished(isSuccessful: Boolean, count: Count) {
        analyticsRepository.track(
            CountFinishMetric(
                result = if (isSuccessful) {
                    CountFinishMetric.CountResult.SUBMITTED
                } else {
                    CountFinishMetric.CountResult.ERROR
                },
                absoluteVariance = count.lotEntries.entries.sumOf { it.value.first ?: 0 }
            )
        )
    }
}
