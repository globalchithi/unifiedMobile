package com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.search

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.common.ext.isFlu
import com.vaxcare.unifiedhub.core.common.ext.isToday
import com.vaxcare.unifiedhub.core.common.ext.toLocalDateString
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.core.network.model.ProductCategoryDTO
import com.vaxcare.unifiedhub.core.ui.Icons
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.feature.transactions.navigation.LotSearchRoute
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.analytics.events.common.LotAddMetric
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

const val SEARCH_TERM_MAX_LENGTH = 12

@HiltViewModel
class LotSearchViewModel @Inject constructor(
    private val lotRepository: LotRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val locationRepository: LocationRepository,
    private val productRepository: ProductRepository,
    private val analyticsRepository: AnalyticsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<LotSearchState, LotSearchEvent, LotSearchIntent>(initialState = LotSearchState()) {
    private val routeArgs: LotSearchRoute = savedStateHandle.toRoute()
    private val selectedProductId: Int? = routeArgs.filterProductId
    private val sourceId: Int = routeArgs.sourceId

    private val searchedLotNumber = MutableStateFlow(TextFieldValue())

    override fun start() {
        super.start()
        viewModelScope.launch(dispatcherProvider.default) {
            val flags = locationRepository.getFeatureFlagsAsync()
            val isLarcsEnabled = flags.any { it.featureFlagName == "LARCsEnabled" }
            val isLarcsOnly = flags.any { it.featureFlagName == "LARCsOnly" }

            setState { copy(addNewLotEnabled = routeArgs.addNewLotEnabled) }

            getLots()
                .map { lots ->
                    if (routeArgs.filterExpiredLots) {
                        lots.filter {
                            it.expiration == null ||
                                it.expiration?.isToday() == true ||
                                it.expiration?.isAfter(LocalDate.now()) == true
                        }
                    } else {
                        lots
                    }
                }.combine(
                    getAcceptableProducts(
                        isLarcsEnabled = isLarcsEnabled,
                        isLarcsOnly = isLarcsOnly
                    )
                ) { lots, prods -> lots to prods }
                .combine(searchedLotNumber, ::extractSearchedLots)
                .collectLatest(::setStateList)
        }
    }

    private fun getAcceptableProducts(isLarcsEnabled: Boolean, isLarcsOnly: Boolean) =
        productRepository.getAllProducts().map { products ->
            products
                .filter { isLarcsEnabled || it.categoryId != ProductCategoryDTO.LARC.id }
                .filter { !isLarcsOnly || it.categoryId == ProductCategoryDTO.LARC.id }
        }

    private fun setStateList(list: List<SelectedLot>) {
        setState {
            copy(selectedLots = list)
        }
    }

    private fun extractSearchedLots(pair: Pair<List<Lot>, List<Product>>, search: TextFieldValue,): List<SelectedLot> {
        val (allLots, prods) = pair
        val searchedLot = search.text
        val acceptableProductIds = prods.map { it.id }
        return allLots
            .filter { it.productId in acceptableProductIds }
            .filter {
                searchedLot.length > 2 &&
                    it.lotNumber
                        .uppercase()
                        .contains(searchedLot)
            }.mapNotNull { lot ->
                prods.firstOrNull { it.id == lot.productId }?.let {
                    lot to it
                }
            }.map { (lot, product) ->
                val lotNumber = buildLotNumberAnnotatedString(
                    lotNumber = lot.lotNumber,
                    searchedLot = searchedLot
                )
                val productName = buildProductNameAnnotatedString(product)

                SelectedLot(
                    lotNumber = lotNumber,
                    productName = productName,
                    presentationIcon = Icons.presentationIcon(product.presentation)
                )
            }
    }

    private fun getLots() =
        selectedProductId?.let { lotRepository.getAllLotsByProductId(it) }
            ?: run { lotRepository.getAllLots() }

    private fun buildProductNameAnnotatedString(product: Product) =
        buildAnnotatedString {
            val antigen = product.antigen + " "
            val fullString =
                antigen + if (antigen.isFlu()) {
                    product.displayName
                } else {
                    "(${product.prettyName})"
                }
            append(fullString)
            addStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                ),
                start = 0,
                end = antigen.length
            )
        }

    private fun buildLotNumberAnnotatedString(lotNumber: String, searchedLot: String) =
        buildAnnotatedString {
            append(lotNumber)
            val split = if (searchedLot.isNotEmpty()) {
                lotNumber.indexOf(searchedLot.uppercase())
            } else {
                -1
            }
            if (split != -1) {
                addStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                    ),
                    start = split,
                    end = split + searchedLot.length
                )
            }
        }

    override fun handleIntent(intent: LotSearchIntent) {
        when (intent) {
            LotSearchIntent.CloseScreen -> {
                sendEvent(LotSearchEvent.NavigateBack)
            }

            LotSearchIntent.CancelAddNewLot -> setState { copy(activeDialog = null) }
            is LotSearchIntent.SearchLot -> onKeywordSearch(intent.enteredLotNumber)
            is LotSearchIntent.AddNewLot -> setState {
                copy(
                    activeDialog = LotSearchDialog.ConfirmLotNumber(intent.enteredLotNumber)
                )
            }

            is LotSearchIntent.SelectLot -> {
                viewModelScope.launch {
                    val lotNumber = intent.selectedLot.lotNumber.text
                    getProductInfoAndSendProductSelectionMetric(intent.selectedLot)
                    sendEvent(
                        LotSearchEvent.NavigateWithSelectedLot(
                            selectedLotNumber = lotNumber,
                            sourceId = sourceId
                        )
                    )
                }
            }

            is LotSearchIntent.ConfirmedAddNewLot -> {
                setState { copy(activeDialog = null) }
                sendEvent(
                    LotSearchEvent.NavigateToAddLot(
                        lotNumber = intent.enteredLotNumber,
                        productId = selectedProductId
                    )
                )
            }
        }
    }

    private suspend fun getProductInfoAndSendProductSelectionMetric(selectedLot: SelectedLot) {
        lotRepository.getLotByNumberAsync(selectedLot.lotNumber.text)?.let { lot ->
            analyticsRepository.track(
                LotAddMetric(
                    screenSource = "Lot Search: ${routeArgs.transactionName}",
                    productSource = "Manual Lot Selection",
                    scannerType = "Manual",
                    productId = lot.productId,
                    productName = selectedLot.productName.text,
                    lotNumber = selectedLot.lotNumber.text,
                    expirationDate = lot.expiration?.toLocalDateString() ?: "",
                )
            )
        }
    }

    private fun onKeywordSearch(enteredLotNumber: TextFieldValue) {
        if (enteredLotNumber.text.length < SEARCH_TERM_MAX_LENGTH) {
            setState { copy(searchTerm = enteredLotNumber) }
            searchedLotNumber.update { enteredLotNumber }
        }
    }
}
