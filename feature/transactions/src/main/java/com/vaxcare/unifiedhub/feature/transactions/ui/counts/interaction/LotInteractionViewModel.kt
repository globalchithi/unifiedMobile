package com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction

import androidx.compose.ui.util.fastSumBy
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.extension.toShorthand
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.PackageRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.domain.PostNewLotUseCase
import com.vaxcare.unifiedhub.core.domain.ValidateScannedProductUseCase
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult
import com.vaxcare.unifiedhub.core.model.inventory.LotInventory
import com.vaxcare.unifiedhub.core.model.lot.LotNumberSource
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.ext.toAnnotatedString
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.domain.model.TransactionSession
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model.LotInventoryUi
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model.mapper.ProductAndLotInventoryMapper
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = LotInteractionViewModel.Factory::class)
class LotInteractionViewModel @AssistedInject constructor(
    private val lotInventoryRepository: LotInventoryRepository,
    private val productRepository: ProductRepository,
    private val lotRepository: LotRepository,
    private val packageRepository: PackageRepository,
    private val mapper: ProductAndLotInventoryMapper,
    private val dispatcherProvider: DispatcherProvider,
    @Assisted private val transactionSession: TransactionSession,
    private val validateScannedProductUseCase: ValidateScannedProductUseCase,
    private val postNewLot: PostNewLotUseCase
) : BaseViewModel<LotInteractionState, LotInteractionEvent, LotInteractionIntent>(
        LotInteractionState()
    ) {
    @AssistedFactory
    interface Factory {
        fun create(session: TransactionSession): LotInteractionViewModel
    }

    private val stockTypeId
        get() = transactionSession.stockType.id

    private fun getLotInventory(productId: Int?) =
        lotInventoryRepository.getLotInventoryByProductAndSourceId(productId, stockTypeId)

    private suspend fun loadScreen(productId: Int) {
        productRepository
            .getProduct(productId)
            .filterNotNull()
            .combine(getLotInventory(productId)) { product, lots ->
                product to lots
            }.combine(transactionSession.searchedLot) { (product, inventory), searchedLot ->
                val alreadyExists = inventory.any { it.lotNumber == searchedLot }
                if (!alreadyExists) {
                    transactionSession.addLot(productId, searchedLot)
                }
                product to inventory
            }.combine(transactionSession.addedLots) { (product, lots), addedLots ->
                product to lots + addedLots
                    .filter { it.first == productId }
                    .map { (_, lotNumber) ->
                        LotInventory(
                            lotNumber = lotNumber,
                            onHand = 0,
                            inventorySourceId = stockTypeId
                        )
                    }
            }.combine(transactionSession.lotState) { (product, lots), deltas ->
                product to lots.map {
                    val lotState = deltas[it.lotNumber] ?: return@map it
                    it.copy(
                        delta = lotState.delta,
                        isDeleted = lotState.isDeleted
                    )
                }
            }.map { (product, lots) ->
                val lotNumbers = lots.map { it.lotNumber }
                val lotList = lotRepository.getLotsByNumber(lotNumbers)
                val lotsUi = lots.mapNotNull { lotInventoryItem ->
                    mapper.lotDomainToLotUi(
                        lotInventory = lotInventoryItem,
                        lot = lotList.firstOrNull { it.lotNumber == lotInventoryItem.lotNumber }
                    )
                }
                val productUi = mapper.productDomainToProductInteractionUi(
                    data = product,
                    pkg = packageRepository.getOneByProductId(product.id)
                )

                productUi to lotsUi
            }.collectLatest { (productUi, lotsUi) ->
                setState {
                    copy(
                        isScannerActive = true,
                        lots = lotsUi,
                        lotCountTotal = lotsUi.getDeltaSum(),
                        lotCountOriginal = lotsUi.getOriginalSum(),
                        isActionRequired = lotsUi.any {
                            it.isActionRequired || ((it.adjustment ?: it.onHand) < 0 && !it.isDeleted)
                        },
                        antigen = productUi.antigen,
                        prettyName = productUi.prettyName,
                        cartonCount = productUi.cartonCount,
                        presentation = productUi.presentation,
                        error = null,
                        activeDialog = null
                    )
                }
            }
    }

    override fun start() {
        super.start()
        setState {
            copy(
                stockType = StockUi.map(transactionSession.stockType),
                isScannerActive = false,
                error = null
            )
        }
        viewModelScope.launch(dispatcherProvider.default) {
            val lotNumberFromSearch = transactionSession.searchedLot.first()

            // get product ID from the session. If null, attempt to set it based on the lotNumber
            // provided by Lot Search.
            val productId = transactionSession.productId.run {
                if (this == null) {
                    val idByLotNumber = lotRepository.getProductIdByLotNumber(
                        lotNumberFromSearch
                    )
                    idByLotNumber.also {
                        transactionSession.productId = it
                    }
                } else {
                    this
                }
            }

            if (productId != null) {
                viewModelScope.launch {
                    transactionSession
                        .searchedLot
                        .filterNotNull()
                        .collectLatest {
                            setState {
                                copy(searchedLot = it)
                            }
                        }
                }

                loadScreen(productId)
            } else {
                setState {
                    copy(
                        isScannerActive = true,
                        error = LotInteractionError.ProductNotFound
                    )
                }
            }
        }
    }

    private fun List<LotInventoryUi>.getDeltaSum(): String =
        fastSumBy {
            if (it.isDeleted) {
                0
            } else {
                it.adjustment ?: it.onHand
            }
        }.toShorthand()

    private fun List<LotInventoryUi>.getOriginalSum(): String? {
        val hasDifferences = any {
            it.isDeleted || (it.delta != null && it.adjustment != it.onHand)
        }
        return if (hasDifferences) {
            fastSumBy { it.onHand }.toShorthand()
        } else {
            null
        }
    }

    override fun handleIntent(intent: LotInteractionIntent) {
        when (intent) {
            LotInteractionIntent.CloseScreen -> {
                if (!transactionSession.containsUncommittedChanges()) {
                    transactionSession.clearSearchedLot()
                    sendEvent(LotInteractionEvent.NavigateBack)
                } else {
                    setState {
                        copy(
                            isScannerActive = false,
                            activeDialog = LotInteractionDialog.SaveOrDiscardChanges
                        )
                    }
                }
            }

            LotInteractionIntent.ConfirmDiscardChanges -> {
                transactionSession.resetLotState()
                transactionSession.clearSearchedLot()
                sendEvent(LotInteractionEvent.NavigateBack)
            }

            LotInteractionIntent.ConfirmLotInventory -> {
                transactionSession.commitChanges()
                transactionSession.clearSearchedLot()
                sendEvent(LotInteractionEvent.NavigateBack)
            }

            LotInteractionIntent.CloseCurrentDialog -> {
                setState {
                    copy(
                        isScannerActive = true,
                        activeDialog = null
                    )
                }
            }

            LotInteractionIntent.SearchLot -> {
                transactionSession.clearSearchedLot()
                sendEvent(
                    LotInteractionEvent.NavigateToLotSearch(
                        filterProductId = transactionSession.productId,
                        sourceId = stockTypeId
                    )
                )
            }

            is LotInteractionIntent.ToggleDelete -> {
                transactionSession.setDeleted(
                    lotNumber = intent.lotNumber,
                    isDeleted = !intent.isCurrentlyDeleted,
                )
            }

            is LotInteractionIntent.NumPadEntry -> {
                val item = currentState().lots.first { it.lotNumber == intent.lotNumber }
                transactionSession.setDelta(
                    lotNumber = intent.lotNumber,
                    delta = (intent.delta - item.onHand)
                )

                setState {
                    copy(
                        isScannerActive = true,
                        activeDialog = null
                    )
                }
            }

            is LotInteractionIntent.UpdateLotDelta -> {
                val prevDelta = intent.lotInventory.delta ?: 0
                val changeInDelta = intent.delta

                transactionSession.setDelta(
                    lotNumber = intent.lotInventory.lotNumber,
                    delta = floorDeltaChange(
                        newDelta = prevDelta + changeInDelta,
                        onHand = intent.lotInventory.onHand
                    )
                )
            }

            is LotInteractionIntent.OpenNumPad -> {
                setState {
                    copy(
                        isScannerActive = false,
                        activeDialog = LotInteractionDialog.NumPadEntry(intent.lotNumber)
                    )
                }
            }

            is LotInteractionIntent.ScanLot -> validateParsedBarcode(intent.barcode)

            is LotInteractionIntent.HighlightComplete -> {
                setState {
                    copy(searchedLot = null)
                }
            }
        }
    }

    /**
     * Floor the result of the change in delta so that, when added to the current on hand, the sum
     * is no less than zero.
     */
    private fun floorDeltaChange(onHand: Int, newDelta: Int): Int {
        val newOnHand = onHand + newDelta
        return if (newOnHand < 0) {
            -onHand
        } else {
            newDelta
        }
    }

    private fun validateParsedBarcode(parsedBarcode: ParsedBarcode) {
        setState { copy(isScannerActive = false) }

        viewModelScope.launch(dispatcherProvider.io) {
            val result = validateScannedProductUseCase(
                parsedBarcode = parsedBarcode,
                expectedProductId = transactionSession.productId,
                existingLotNumbers = currentState().lots.map { it.lotNumber },
                analyticsScreenSource = "Lot Interaction: ${transactionSession.transactionName}"
            )

            when (result) {
                is ScanValidationResult.Valid -> {
                    transactionSession.addLot(transactionSession.productId, result.lotNumber)
                    transactionSession.setDelta(result.lotNumber, 0)
                }

                is ScanValidationResult.NewLot -> {
                    postNewLot(
                        lotNumber = result.lotNumber,
                        expiration = result.expiration,
                        productId = transactionSession.productId,
                        source = LotNumberSource.VaxHubScan
                    )

                    val lot = lotRepository
                        .getLotByNumber(result.lotNumber)
                        .filterNotNull()
                        .first()

                    transactionSession.addLot(lot.productId, lot.lotNumber)
                    transactionSession.setDelta(lot.lotNumber, 0)
                }

                is ScanValidationResult.DuplicateLot -> {
                    setState {
                        copy(isScannerActive = true, searchedLot = result.lotNumber, error = null)
                    }
                }

                is ScanValidationResult.WrongProduct -> {
                    // ViewModel is now responsible for UI transformation
                    val annotatedErrorMessage = HtmlCompat
                        .fromHtml(
                            result.errorMessage,
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        ).toAnnotatedString()

                    setState {
                        copy(
                            isScannerActive = false,
                            activeDialog = LotInteractionDialog.WrongProductScanned(
                                annotatedErrorMessage
                            ),
                            error = null
                        )
                    }
                }

                is ScanValidationResult.Expired -> {
                    setState {
                        copy(
                            isScannerActive = false,
                            activeDialog = LotInteractionDialog.ExpiredProductScanned,
                            error = null
                        )
                    }
                }

                ScanValidationResult.MismatchedProduct -> {
                    setState {
                        copy(
                            isScannerActive = false,
                            activeDialog = LotInteractionDialog.MismatchedProduct,
                            error = null
                        )
                    }
                }

                ScanValidationResult.InvalidBarcode -> {
                    setState {
                        copy(
                            isScannerActive = true,
                            activeDialog = null,
                            error = LotInteractionError.BadBarcodeScan
                        )
                    }
                }
            }
        }
    }
}
