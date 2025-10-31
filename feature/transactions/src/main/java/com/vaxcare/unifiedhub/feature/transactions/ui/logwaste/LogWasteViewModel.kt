package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste

import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.domain.PostNewLotUseCase
import com.vaxcare.unifiedhub.core.domain.ValidateScannedProductUseCase
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.lot.LotNumberSource
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.EditQuantityUi
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.ProductInfoUi
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.SubtitleLine
import com.vaxcare.unifiedhub.core.ui.ext.toAnnotatedString
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.model.LotState
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.components.EditProductLotQuantityUi
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.session.LogWasteSession
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = LogWasteViewModel.Factory::class)
class LogWasteViewModel @AssistedInject constructor(
    private val productRepository: ProductRepository,
    private val lotRepository: LotRepository,
    private val dispatcherProvider: DispatcherProvider,
    @Assisted private val session: LogWasteSession,
    private val validateScannedProductUseCase: ValidateScannedProductUseCase,
    private val postNewLotUseCase: PostNewLotUseCase
) : BaseViewModel<LogWasteState, LogWasteEvent, LogWasteIntent>(initialState = LogWasteState()) {
    @AssistedFactory
    interface Factory {
        fun create(session: LogWasteSession): LogWasteViewModel
    }

    override fun start() {
        setState { copy(stockUi = StockUi.map(session.stockType)) }

        viewModelScope.launch(dispatcherProvider.io) {
            session.lotState
                .combine(session.searchedLot) { lots, searchedLot -> lots to searchedLot }
                .combine(session.wasteReason) { (lots, searchedLot), reason ->
                    Triple(lots, searchedLot, reason)
                }.collectLatest { (lotsInSession, searchedLotNumber, reason) ->

                    if (searchedLotNumber != null) {
                        val lot = lotRepository.getLotByNumberAsync(searchedLotNumber)
                        if (lot != null) {
                            session.addLotToWaste(lot.productId, lot.lotNumber)
                        } else {
                            Timber.e("Lot from search not found: $searchedLotNumber")
                        }
                        session.clearSearchedLot()
                    }

                    val lotNumbers = lotsInSession.keys.toList()
                    if (lotNumbers.isEmpty()) {
                        setState {
                            copy(
                                isLoading = false,
                                wastedProductsUi = emptyList(),
                                total = 0,
                                reason =
                                    reason ?: LogWasteReason.OTHER
                            )
                        }
                        return@collectLatest
                    }

                    val lotDetails = lotRepository.getLotsByNumber(lotNumbers)
                    val productDetails =
                        productRepository.getProductsByIds(
                            lotDetails
                                .map { it.productId }
                                .distinct()
                        )

                    val uiList = mapToUiModel(lotsInSession, lotDetails, productDetails)

                    val totalWaste = uiList
                        .filterNot { it.productInfoUi.isDeleted }
                        .sumOf { it.editQuantityUi.quantity }

                    setState {
                        copy(
                            isLoading = false,
                            wastedProductsUi = uiList,
                            total = totalWaste,
                            reason = reason ?: LogWasteReason.OTHER
                        )
                    }
                }
        }
    }

    private fun mapToUiModel(
        lotsInSession: Map<String, LotState>,
        lotDetails: List<Lot>,
        productDetails: List<Product>
    ): List<EditProductLotQuantityUi> {
        val lotsMap = lotDetails.associateBy { it.lotNumber }
        val productsMap = productDetails.associateBy { it.id }

        return lotsInSession.mapNotNull { (lotNumber, lotState) ->
            val lot = lotsMap[lotNumber] ?: return@mapNotNull null
            val product = productsMap[lot.productId] ?: return@mapNotNull null

            EditProductLotQuantityUi(
                productInfoUi = with(product) {
                    ProductInfoUi(
                        presentation = presentation,
                        mainTextBold = antigen,
                        mainTextRegular = "(${prettyName ?: displayName})",
                        subtitleLines = listOf(
                            SubtitleLine(text = "LOT# ${lot.lotNumber}")
                        ),
                        isDeleted = lotState.isDeleted,
                    )
                },
                editQuantityUi = EditQuantityUi(
                    quantity = lotState.delta ?: 0,
                    decrementEnabled = (lotState.delta ?: 0) > 1,
                    enabled = !lotState.isDeleted,
                    onDecrementClick = { handleIntent(LogWasteIntent.DecrementLotAmount(lotNumber)) },
                    onIncrementClick = { handleIntent(LogWasteIntent.IncrementLotAmount(lotNumber)) },
                    onInputNumberClick = { handleIntent(LogWasteIntent.EnterLotQuantity(lotNumber)) },
                    onDecrementLongClick = { handleIntent(LogWasteIntent.DecrementLotAmount(lotNumber, 5)) },
                    onIncrementLongClick = { handleIntent(LogWasteIntent.IncrementLotAmount(lotNumber, 5)) }
                ),
                onDeleteClick = { handleIntent(LogWasteIntent.RemoveLot(lotNumber)) },
                onUndoClick = { handleIntent(LogWasteIntent.UndoLotRemoved(lotNumber)) }
            )
        }
    }

    override fun handleIntent(intent: LogWasteIntent) {
        when (intent) {
            is LogWasteIntent.NavigateBackClicked -> onNavigateBack()
            is LogWasteIntent.DiscardChangesConfirmed -> {
                session.resetLotState()
                sendEvent(LogWasteEvent.GoBack)
            }
            is LogWasteIntent.CloseDialog -> setState { copy(activeDialog = null) }

            is LogWasteIntent.BarcodeScanned -> onBarcodeScanned(intent.barcode)
            LogWasteIntent.SearchLot -> sendEvent(LogWasteEvent.GoToLotSearch)

            is LogWasteIntent.DecrementLotAmount -> onDecrementWaste(intent.lotNumber, intent.amount)
            is LogWasteIntent.IncrementLotAmount -> onIncrementWaste(intent.lotNumber, intent.amount)
            is LogWasteIntent.RemoveLot -> onRemoveLot(intent.lotNumber)
            is LogWasteIntent.UndoLotRemoved -> onUndoRemovedLot(intent.lotNumber)

            LogWasteIntent.ConfirmWastedProducts -> sendEvent(LogWasteEvent.GoToSummary)
            is LogWasteIntent.EnterLotQuantity -> setState {
                copy(
                    activeDialog = LogWasteDialog.EnterQuantity(
                        intent.lotNumber
                    )
                )
            }

            is LogWasteIntent.LotQuantityEntered -> {
                session.setDelta(
                    lotNumber = intent.lotNumber,
                    delta = intent.quantity
                )
                setState { copy(activeDialog = null) }
            }
        }
    }

    private fun onNavigateBack() {
        if (session.containsSessionChanges()) {
            setState { copy(activeDialog = LogWasteDialog.DiscardChanges) }
        } else {
            sendEvent(LogWasteEvent.GoBack)
        }
    }

    private fun onBarcodeScanned(barcode: ParsedBarcode) {
        setState { copy(isInvalidProductScanned = false) }

        viewModelScope.launch(dispatcherProvider.io) {
            val scanValidationResult = validateScannedProductUseCase(
                parsedBarcode = barcode,
                expectedProductId = null,
                existingLotNumbers = currentState().wastedProductsUi.map { it.productInfoUi.mainTextBold },
                analyticsScreenSource = session.transactionName,
            )

            when (scanValidationResult) {
                is ScanValidationResult.Valid -> {
                    session.addLotToWaste(
                        scanValidationResult.productId,
                        scanValidationResult.lotNumber
                    )
                }

                is ScanValidationResult.NewLot -> {
                    postNewLotUseCase(
                        lotNumber = scanValidationResult.lotNumber,
                        productId = scanValidationResult.productId,
                        expiration = scanValidationResult.expiration,
                        source = LotNumberSource.VaxHubScan
                    )

                    session.addLotToWaste(
                        scanValidationResult.productId,
                        scanValidationResult.lotNumber
                    )
                }

                is ScanValidationResult.DuplicateLot -> {
                    onIncrementWaste(lotNumber = scanValidationResult.lotNumber, amount = 1)
                }

                is ScanValidationResult.WrongProduct -> {
                    val annotatedErrorMessage = HtmlCompat
                        .fromHtml(
                            scanValidationResult.errorMessage,
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        ).toAnnotatedString()

                    setState {
                        copy(
                            activeDialog = LogWasteDialog.WrongProduct(
                                annotatedErrorMessage
                            ),
                        )
                    }
                }

                is ScanValidationResult.Expired -> setState { copy(activeDialog = LogWasteDialog.ExpiredProduct) }

                ScanValidationResult.InvalidBarcode -> {
                    setState { copy(isInvalidProductScanned = true) }
                }

                ScanValidationResult.MismatchedProduct -> {
                    // Not a valid scenario; there's no expected product in this screen.
                }
            }
        }
    }

    private fun onIncrementWaste(lotNumber: String, amount: Int) {
        val currentDelta = session.lotState.value[lotNumber]?.delta ?: 0
        session.setDelta(lotNumber, currentDelta + amount)
    }

    private fun onDecrementWaste(lotNumber: String, amount: Int) {
        val currentDelta = session.lotState.value[lotNumber]?.delta ?: 0
        if (currentDelta > 1) {
            session.setDelta(lotNumber, maxOf(1, currentDelta - amount))
        }
    }

    private fun onRemoveLot(lotNumber: String) {
        session.setDeleted(lotNumber, true)
    }

    private fun onUndoRemovedLot(lotNumber: String) {
        session.setDeleted(lotNumber, false)
    }
}
