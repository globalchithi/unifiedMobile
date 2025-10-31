package com.vaxcare.unifiedhub.feature.home.ui.onhand

import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.extension.to
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.datastore.datasource.UsagePreferenceDataSource
import com.vaxcare.unifiedhub.core.domain.ValidateStockType
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.Icons
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.home.ui.onhand.model.GroupedProducts
import com.vaxcare.unifiedhub.feature.home.ui.onhand.model.OnHandSection
import com.vaxcare.unifiedhub.feature.home.ui.onhand.model.ProductUI
import com.vaxcare.unifiedhub.feature.home.ui.onhand.util.OnHandTokens.seasonalAntigens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OnHandViewModel @Inject constructor(
    private val validateStockType: ValidateStockType,
    private val locationRepository: LocationRepository,
    private val usagePrefs: UsagePreferenceDataSource,
    private val lotInventoryRepository: LotInventoryRepository,
    private val productRepository: ProductRepository,
    private val lotRepository: LotRepository,
    private val dispatcherProvider: DispatcherProvider
) : BaseViewModel<OnHandState, OnHandEvent, OnHandIntent>(
        initialState = OnHandState()
    ) {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun start() {
        super.start()
        viewModelScope.launch(dispatcherProvider.io) {
            locationRepository
                .getLocation()
                .filterNotNull()
                .map { it.stockTypes }
                .combine(
                    usagePrefs.lastSelectedStock.map { StockType.fromId(it) }
                ) { availableStockTypes, selectedStock: StockType ->
                    val lastSelectedStock = validateStockType(selectedStock, availableStockTypes)

                    lastSelectedStock to availableStockTypes
                }.flatMapLatest { (lastSelectedStock, availableStockTypes) ->
                    lotInventoryRepository
                        .getLotInventory(stockType = lastSelectedStock)
                        .map { lotInventory ->
                            lastSelectedStock to availableStockTypes to lotInventory
                        }
                }.distinctUntilChanged()
                .map { (lastSelectedStock, availableStockTypes, lotInventory) ->
                    val lotNumbers = lotInventory.map { it.lotNumber }
                    val lots = lotNumbers
                        .let { lotRepository.getLotsByNumber(it) }
                        .groupBy { it.productId }
                    val products = productRepository
                        .getProductsByLotNumber(lotNumbers)
                        .groupBy { it.inventoryGroup }
                    val inventoryByProductId = lotInventory.groupBy { it.productId }

                    val uiProducts = products.map { (invGroup, items) ->
                        val invGroupProducts = items
                            .map { item ->
                                val inv = inventoryByProductId[item.id]
                                val onHand = inv?.sumOf { it.onHand } ?: 0
                                val isExpired = lots[item.id]
                                    ?.map { it.expiration?.isBefore(LocalDate.now()) ?: false }
                                    ?.reduce { acc, isExpired ->
                                        acc && isExpired
                                    } ?: false

                                ProductUI(
                                    productId = item.id,
                                    antigen = item.antigen,
                                    prettyName = item.prettyName ?: item.displayName,
                                    onHand = onHand,
                                    isExpired = isExpired,
                                    presentationIcon = Icons.presentationIcon(item.presentation),
                                )
                            }.sortedBy { it.prettyName }

                        GroupedProducts(
                            inventoryGroup = invGroup,
                            antigen = items.first().antigen,
                            products = invGroupProducts,
                            onHand = invGroupProducts.sumOf { it.onHand }
                        )
                    }

                    // Finally ready for the UI to do its thing
                    StockUi.map(lastSelectedStock) to
                        availableStockTypes.map(StockUi::map) to
                        uiProducts.sortedBy { it.antigen }
                }.collectLatest { (lastSelectedStock, availableStocks, products) ->
                    val (seasonal, nonSeasonal) = products.partition {
                        it.antigen.uppercase() in seasonalAntigens
                    }

                    setState {
                        copy(
                            activeStock = lastSelectedStock,
                            availableStocks = availableStocks,
                            enableStockSelection = availableStocks.size > 1,
                            seasonalProducts = seasonal.sortedBy { it.antigen },
                            nonSeasonalProducts = nonSeasonal.sortedBy { it.antigen }
                        )
                    }
                }
        }
    }

    override fun handleIntent(intent: OnHandIntent) {
        when (intent) {
            OnHandIntent.DismissDialog -> {
                setState { copy(activeDialog = null) }
            }
            OnHandIntent.OpenStockSelector -> {
                setState {
                    copy(activeDialog = OnHandDialog.StockSelection)
                }
            }
            is OnHandIntent.SelectSection -> {
                when (intent.section) {
                    OnHandSection.NON_SEASONAL -> {
                        sendEvent(OnHandEvent.ScrollToItem(0))
                    }

                    OnHandSection.SEASONAL -> {
                        val firstSeasonalIndex = currentState().nonSeasonalProducts.lastIndex + 1
                        sendEvent(OnHandEvent.ScrollToItem(firstSeasonalIndex))
                    }
                }
            }
            is OnHandIntent.SelectStock -> {
                setState { copy(activeDialog = null) }
                viewModelScope.launch(dispatcherProvider.io) {
                    usagePrefs.setLastSelectedStock(intent.stock.toDomain().id)
                }
            }
        }
    }
}
