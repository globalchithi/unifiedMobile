package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.lotinteraction

import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.PackageRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.domain.ConvertHtmlUseCase
import com.vaxcare.unifiedhub.core.domain.PostNewLotUseCase
import com.vaxcare.unifiedhub.core.domain.ValidateScannedProductUseCase
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult
import com.vaxcare.unifiedhub.core.model.lot.LotNumberSource
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.ext.toAnnotatedString
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.AddedLotInventoryUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.session.AddPublicSession
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltViewModel(assistedFactory = AddPublicLotInteractionViewModel.Factory::class)
class AddPublicLotInteractionViewModel @AssistedInject constructor(
    @Assisted private val session: AddPublicSession,
    private val lotRepository: LotRepository,
    private val packageRepository: PackageRepository,
    private val productRepository: ProductRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val validateScannedProduct: ValidateScannedProductUseCase,
    private val postNewLot: PostNewLotUseCase,
    private val convertHtml: ConvertHtmlUseCase
) : BaseViewModel<AddPublicLotInteractionState, AddPublicLotInteractionEvent, AddPublicLotInteractionIntent>(
        AddPublicLotInteractionState()
    ) {
    @AssistedFactory
    interface Factory {
        fun create(session: AddPublicSession): AddPublicLotInteractionViewModel
    }

    private suspend fun loadScreen(productId: Int) {
        productRepository
            .getProduct(productId)
            .filterNotNull()
            .combine(session.lotState) { product, lotState ->
                product to lotState
            }.map { (product, lotStates) ->
                val lots = lotRepository
                    .getLotsByNumber(lotStates.keys.toList())
                    .filter { it.productId == productId }
                    .associateBy { it.lotNumber }

                val lotsUi = lotStates.mapNotNull { lotState ->
                    val lot = lots[lotState.key] ?: return@mapNotNull null
                    val isExpired = lot.expiration?.isBefore(LocalDate.now()) ?: false
                    val fmt = DateTimeFormatter.ofPattern(
                        if (isExpired) "MM/dd/yyyy" else "MM/yyyy"
                    )

                    AddedLotInventoryUi(
                        lotNumber = lotState.key,
                        expiration = lot.expiration?.format(fmt) ?: "",
                        isExpired = isExpired,
                        count = lotState.value.count,
                        isDeleted = lotState.value.isDeleted
                    )
                }

                ProductUi(
                    id = product.id,
                    inventory = lotsUi.reversed(),
                    antigen = product.antigen,
                    prettyName = product.prettyName ?: "",
                    cartonCount = packageRepository.getOneByProductId(productId).itemCount,
                    presentation = product.presentation
                )
            }.collectLatest { product ->
                setState {
                    copy(
                        isScannerActive = true,
                        product = product,
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
                stockType = StockUi.map(session.stockType),
                isScannerActive = false,
                error = null
            )
        }

        viewModelScope.launch(dispatcherProvider.default) {
            val lotNumberFromSearch = session.searchedLot.first()

            // get product ID from the session. If null, attempt to set it based on the lotNumber
            // provided by Lot Search.
            val productId = session.productId.run {
                if (this == null) {
                    val idByLotNumber = lotRepository.getProductIdByLotNumber(
                        lotNumberFromSearch
                    )
                    idByLotNumber.also {
                        session.productId = it
                    }
                } else {
                    this
                }
            } ?: return@launch

            loadScreen(productId)
        }
    }

    override fun handleIntent(intent: AddPublicLotInteractionIntent) {
        when (intent) {
            AddPublicLotInteractionIntent.CloseScreen -> {
                if (!session.containsUncommittedChanges()) {
                    session.clearSearchedLot()
                    sendEvent(AddPublicLotInteractionEvent.NavigateBack)
                } else {
                    setState {
                        copy(
                            isScannerActive = false,
                            activeDialog = AddPublicLotInteractionDialog.DiscardChanges
                        )
                    }
                }
            }

            AddPublicLotInteractionIntent.DiscardChanges -> {
                session.resetLotState()
                session.clearSearchedLot()
                sendEvent(AddPublicLotInteractionEvent.NavigateBack)
            }

            AddPublicLotInteractionIntent.Confirm -> {
                session.commitChanges()
                session.clearSearchedLot()
                sendEvent(AddPublicLotInteractionEvent.NavigateBack)
            }

            AddPublicLotInteractionIntent.DismissDialog -> {
                setState {
                    copy(
                        isScannerActive = true,
                        activeDialog = null
                    )
                }
            }

            AddPublicLotInteractionIntent.SearchLot -> {
                sendEvent(
                    AddPublicLotInteractionEvent.NavigateToLotSearch(
                        filterProductId = session.productId,
                        sourceId = session.stockType.id
                    )
                )
            }

            is AddPublicLotInteractionIntent.DeleteLot -> {
                session.setDeleted(
                    lotNumber = intent.lotNumber,
                    isDeleted = true
                )
            }

            is AddPublicLotInteractionIntent.UndoDelete -> {
                session.setDeleted(
                    lotNumber = intent.lotNumber,
                    isDeleted = false
                )
            }

            is AddPublicLotInteractionIntent.SubmitKeypadInput -> {
                session.setCount(
                    lotNumber = intent.lotNumber,
                    count = intent.count
                )

                setState {
                    copy(
                        isScannerActive = true,
                        activeDialog = null
                    )
                }
            }

            is AddPublicLotInteractionIntent.UpdateLotCount -> {
                session.createOrUpdateCount(
                    lotNumber = intent.lotNumber,
                    change = intent.change
                )
            }

            is AddPublicLotInteractionIntent.OpenKeypad -> {
                setState {
                    copy(
                        isScannerActive = false,
                        activeDialog = AddPublicLotInteractionDialog.Keypad(intent.lotNumber)
                    )
                }
            }

            is AddPublicLotInteractionIntent.ScanLot -> validateParsedBarcode(intent.barcode)
        }
    }

    private fun validateParsedBarcode(parsedBarcode: ParsedBarcode) {
        setState { copy(isScannerActive = false) }

        viewModelScope.launch(dispatcherProvider.default) {
            val result = validateScannedProduct(
                parsedBarcode = parsedBarcode,
                expectedProductId = session.productId,
                existingLotNumbers = currentState().product?.inventory?.map { it.lotNumber }
                    ?: emptyList(),
                analyticsScreenSource = "Lot Interaction: Add Public"
            )

            when (result) {
                is ScanValidationResult.Valid -> {
                    session.setCount(result.lotNumber, 1)
                }

                is ScanValidationResult.NewLot -> {
                    postNewLot(
                        lotNumber = result.lotNumber,
                        productId = session.productId,
                        expiration = result.expiration,
                        source = LotNumberSource.VaxHubScan
                    )

                    session.setCount(
                        lotNumber = result.lotNumber,
                        count = 1
                    )
                }

                is ScanValidationResult.DuplicateLot -> {
                    setState {
                        copy(
                            isScannerActive = true,
                            highlightedLot = result.lotNumber,
                            error = null
                        )
                    }
                }

                is ScanValidationResult.WrongProduct -> {
                    val annotatedErrorMessage = convertHtml(result.errorMessage).toAnnotatedString()

                    setState {
                        copy(
                            isScannerActive = false,
                            activeDialog = AddPublicLotInteractionDialog.WrongProduct(
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
                            activeDialog = AddPublicLotInteractionDialog.ExpiredDose,
                            error = null
                        )
                    }
                }

                ScanValidationResult.MismatchedProduct -> {
                    setState {
                        copy(
                            isScannerActive = false,
                            activeDialog = AddPublicLotInteractionDialog.MismatchedProduct,
                            error = null
                        )
                    }
                }

                ScanValidationResult.InvalidBarcode -> {
                    setState {
                        copy(
                            isScannerActive = true,
                            activeDialog = null,
                            error = AddPublicLotInteractionError.BadBarcodeScan
                        )
                    }
                }
            }
        }
    }
}
