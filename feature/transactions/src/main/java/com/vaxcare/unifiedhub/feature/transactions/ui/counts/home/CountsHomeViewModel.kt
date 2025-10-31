package com.vaxcare.unifiedhub.feature.transactions.ui.counts.home

import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.domain.CreateProductListFromLotsUseCase
import com.vaxcare.unifiedhub.core.domain.UpdateConnectivityStatusUseCase
import com.vaxcare.unifiedhub.core.model.ConnectivityStatus
import com.vaxcare.unifiedhub.core.model.inventory.LotInventory
import com.vaxcare.unifiedhub.core.ui.Icons
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.model.CountsSection
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.model.LotInventoryUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.model.ProductUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.session.CountSession
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.util.CountsTokens.seasonalAntigens
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = CountsHomeViewModel.Factory::class)
class CountsHomeViewModel @AssistedInject constructor(
    private val lotInventoryRepository: LotInventoryRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val createProductListFromLotsUseCase: CreateProductListFromLotsUseCase,
    private val updateConnectivityStatus: UpdateConnectivityStatusUseCase,
    @Assisted private val countSession: CountSession,
) : BaseViewModel<CountsHomeState, CountsHomeEvent, CountsHomeIntent>(
        initialState = CountsHomeState()
    ) {
    override fun start() {
        viewModelScope.launch(dispatcherProvider.io) {
            setState {
                copy(stockType = StockUi.map(countSession.stockType))
            }

            attemptInitialization()
        }
    }

    override fun handleIntent(intent: CountsHomeIntent) {
        when (intent) {
            is CountsHomeIntent.SearchLots -> {
                countSession.productId = null
                sendEvent(CountsHomeEvent.NavigateToLotSearch(sourceId = countSession.stockType.id))
            }

            is CountsHomeIntent.CloseScreen -> {
                if (countSession.containsSessionChanges()) {
                    setState {
                        copy(activeDialog = CountsHomeDialog.DiscardChanges)
                    }
                } else {
                    sendEvent(CountsHomeEvent.NavigateBack)
                }
            }

            is CountsHomeIntent.DiscardChanges -> {
                sendEvent(CountsHomeEvent.NavigateBack)
            }

            is CountsHomeIntent.ConfirmProduct -> {
                countSession.confirmProduct(intent.product.productId)
            }

            is CountsHomeIntent.EditProduct -> {
                countSession.productId = intent.product.productId
                sendEvent(CountsHomeEvent.NavigateToLotInteraction)
            }

            is CountsHomeIntent.ProceedToSummary -> {
                if (getFirstActionRequiredIndex() != -1) {
                    setState { copy(activeDialog = CountsHomeDialog.ActionRequired) }
                } else {
                    sendEvent(CountsHomeEvent.NavigateToSummary)
                }
            }

            is CountsHomeIntent.DismissDialog -> {
                setState {
                    if (activeDialog == CountsHomeDialog.ActionRequired) {
                        val firstIndex = getFirstActionRequiredIndex()
                        sendEvent(CountsHomeEvent.ScrollToItem(firstIndex))
                    }
                    copy(activeDialog = null)
                }
            }

            is CountsHomeIntent.SelectSection -> {
                when (intent.section) {
                    CountsSection.NON_SEASONAL -> {
                        sendEvent(CountsHomeEvent.ScrollToItem(0))
                    }

                    CountsSection.SEASONAL -> {
                        val firstSeasonalIndex = currentState().nonSeasonalProducts.lastIndex + 1
                        sendEvent(CountsHomeEvent.ScrollToItem(firstSeasonalIndex))
                    }
                }
            }

            is CountsHomeIntent.NoInternetTryAgain -> {
                viewModelScope.launch(dispatcherProvider.io) {
                    setState {
                        copy(activeDialog = null)
                    }

                    attemptInitialization()
                }
            }
        }
    }

    /**
     * We REQUIRE a sync before beginning a Count. This method attempts that sync and, if
     * successful, begins collecting data from our Room instance. If unsuccessful, display a dialog.
     *
     * @return `true` if data collection has begun, otherwise `false`
     */
    private suspend fun attemptInitialization(): Boolean =
        checkConnectionAndSync().also {
            if (it) beginCollectingState() else showNoInternetDialog()
        }

    /**
     * Check the connectivity status of the device. If connected, request an inventory sync.
     *
     * @return `true` if the sync was successful, otherwise `false`
     */
    private suspend fun checkConnectionAndSync(): Boolean =
        if (updateConnectivityStatus() == ConnectivityStatus.CONNECTED) {
            try {
                lotInventoryRepository.syncLotInventory()
                true
            } catch (e: Exception) {
                Timber.e("Encountered an exception while attempting to sync lot inventory: \n$e")
                false
            }
        } else {
            false
        }

    private suspend fun beginCollectingState() {
        val currentProducts = lotInventoryRepository
            .getLotInventory(stockType = countSession.stockType)
            .onEach {
                countSession.initializeLotState(it.map { it.lotNumber })
            }.combine(countSession.addedLots) { lotInventory, addedLots ->
                val existingLotNumbers = lotInventory.map { it.lotNumber }.toSet()
                val newLotsToAdd = addedLots
                    .filterNot { (_, lotNumber) -> existingLotNumbers.contains(lotNumber) }
                    .map { (_, lotNumber) ->
                        LotInventory(
                            lotNumber = lotNumber,
                            onHand = 0,
                            inventorySourceId = countSession.stockType.id
                        )
                    }
                lotInventory + newLotsToAdd
            }.combine(countSession.lotState) { lotInventory, lotState ->
                lotInventory.map {
                    val state = lotState[it.lotNumber]
                    if (state?.isDeleted == true) {
                        LotInventoryUi(
                            lotNumber = it.lotNumber,
                            initialQuantity = it.onHand,
                            delta = -it.onHand
                        )
                    } else {
                        LotInventoryUi(
                            lotNumber = it.lotNumber,
                            initialQuantity = it.onHand,
                            delta = state?.delta,
                        )
                    }
                }
            }.combine(countSession.confirmedIds) { lotInventoryUis, confirmedIds ->
                val lotNumbers = lotInventoryUis.map { it.lotNumber }
                val inventoryByLotNumber = lotInventoryUis.associateBy { it.lotNumber }

                createProductListFromLotsUseCase(
                    lotNumbers,
                    inventoryByLotNumber,
                    { product, productInventory, firstPackage ->
                        ProductUi(
                            productId = product.id,
                            isConfirmed = confirmedIds.contains(product.id),
                            inventory = productInventory,
                            antigen = product.antigen,
                            prettyName = product.prettyName ?: product.displayName,
                            cartonCount = firstPackage.itemCount,
                            presentationIcon = Icons.presentationIcon(product.presentation)
                        )
                    }
                )
            }

        currentProducts.collectLatest { products ->
            val (seasonal, nonSeasonal) = products.partition {
                it.antigen.uppercase() in seasonalAntigens
            }

            setState {
                copy(
                    isLoading = false,
                    seasonalProducts = seasonal.sortedBy { it.antigen },
                    nonSeasonalProducts = nonSeasonal.sortedBy { it.antigen }
                )
            }
        }
    }

    private fun showNoInternetDialog() {
        setState {
            copy(activeDialog = CountsHomeDialog.NoInternet)
        }
    }

    private fun getFirstActionRequiredIndex(): Int {
        with(currentState()) {
            val index = (nonSeasonalProducts + seasonalProducts)
                .indexOfFirst { it.isActionRequired() }

            return if (index > nonSeasonalProducts.lastIndex) {
                // adjust for the "seasonal" section header
                index + 1
            } else {
                index
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(session: CountSession): CountsHomeViewModel
    }
}
