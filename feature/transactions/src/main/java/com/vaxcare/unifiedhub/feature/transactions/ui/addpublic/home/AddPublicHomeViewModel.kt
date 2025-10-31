package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home

import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.domain.ConvertHtmlUseCase
import com.vaxcare.unifiedhub.core.domain.CreateProductListFromLotsUseCase
import com.vaxcare.unifiedhub.core.domain.PostNewLotUseCase
import com.vaxcare.unifiedhub.core.domain.ValidateScannedProductUseCase
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult.Expired
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult.InvalidBarcode
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult.NewLot
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult.Valid
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult.WrongProduct
import com.vaxcare.unifiedhub.core.model.lot.LotNumberSource
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.ext.toAnnotatedString
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeDialog.ExpiredProductScanned
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeDialog.WrongProductScanned
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeEvent.NavigateBack
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeEvent.NavigateToLotInteraction
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeEvent.NavigateToLotSearch
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeEvent.NavigateToSummary
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeIntent.CloseScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeIntent.DeleteProduct
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeIntent.DiscardChanges
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeIntent.DismissDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeIntent.EditProduct
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeIntent.ProceedToSummary
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeIntent.RestoreProduct
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeIntent.ScanLot
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeIntent.SearchLots
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.model.LotInventoryUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.model.ProductUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.session.AddPublicSession
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddPublicHomeViewModel.Factory::class)
class AddPublicHomeViewModel @AssistedInject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val createProductListFromLotsUseCase: CreateProductListFromLotsUseCase,
    private val validateScannedProduct: ValidateScannedProductUseCase,
    private val postNewLot: PostNewLotUseCase,
    private val convertHtml: ConvertHtmlUseCase,
    @Assisted private val addPublicSession: AddPublicSession,
) : BaseViewModel<AddPublicHomeState, AddPublicHomeEvent, AddPublicHomeIntent>(
        initialState = AddPublicHomeState()
    ) {
    @AssistedFactory
    interface Factory {
        fun create(session: AddPublicSession): AddPublicHomeViewModel
    }

    override fun start() {
        super.start()
        viewModelScope.launch(dispatcherProvider.io) {
            setState {
                copy(stockType = StockUi.map(addPublicSession.stockType))
            }

            val currentProducts = addPublicSession
                .lotState
                .map { lotState ->
                    lotState.map { (lotNumber, lotState) ->
                        LotInventoryUi(
                            lotNumber = lotNumber,
                            quantity = lotState.count,
                            isDeleted = lotState.isDeleted
                        )
                    }
                }.map { lotInventory ->
                    lotInventory.filter { !it.isDeleted }
                }.combine(addPublicSession.productState) { lotInventory, productStates ->
                    val lotNumbers = lotInventory.map { it.lotNumber }
                    val inventoryByLotNumber = lotInventory.associateBy { it.lotNumber }

                    createProductListFromLotsUseCase(
                        lotNumbers,
                        inventoryByLotNumber
                    ) { product, productInventory, _ ->
                        ProductUi(
                            productId = product.id,
                            isDeleted = productStates[product.id]?.isDeleted ?: false,
                            inventory = productInventory,
                            antigen = product.antigen,
                            prettyName = product.prettyName ?: product.displayName,
                            presentation = product.presentation
                        )
                    }
                }

            currentProducts.collectLatest { products ->
                setState {
                    copy(
                        isScannerActive = true,
                        products = products.reversed()
                    )
                }
            }
        }
    }

    override fun handleIntent(intent: AddPublicHomeIntent) {
        when (intent) {
            CloseScreen -> {
                if (currentState().products.isNotEmpty()) {
                    setState { copy(activeDialog = AddPublicHomeDialog.DiscardChanges) }
                } else {
                    cleanStateAndNavigate(NavigateBack)
                }
            }

            is DeleteProduct -> {
                addPublicSession.setDeletedProduct(intent.product.productId, true)
            }

            DiscardChanges -> {
                cleanStateAndNavigate(NavigateBack)
            }

            DismissDialog -> {
                setState {
                    copy(
                        activeDialog = null,
                        isScannerActive = true
                    )
                }
            }

            is EditProduct -> {
                addPublicSession.productId = intent.product.productId
                cleanStateAndNavigate(NavigateToLotInteraction)
            }

            ProceedToSummary -> {
                cleanStateAndNavigate(NavigateToSummary)
            }

            SearchLots -> {
                addPublicSession.productId = null
                cleanStateAndNavigate(NavigateToLotSearch(sourceId = addPublicSession.stockType.id))
            }

            is RestoreProduct -> {
                addPublicSession.setDeletedProduct(intent.product.productId, false)
            }

            is ScanLot -> {
                validateParsedBarcode(intent.barcode)
            }
        }
    }

    private fun cleanStateAndNavigate(navigateEvent: AddPublicHomeEvent) {
        setState {
            copy(
                isScannerActive = false,
                activeDialog = null
            )
        }
        sendEvent(navigateEvent)
    }

    private fun validateParsedBarcode(parsedBarcode: ParsedBarcode) {
        setState { copy(isInvalidScan = false) }

        viewModelScope.launch(dispatcherProvider.default) {
            val result = validateScannedProduct(
                parsedBarcode = parsedBarcode,
                expectedProductId = null,
                existingLotNumbers = emptyList(),
                analyticsScreenSource = "Add Public: Home"
            )

            when (result) {
                is Valid -> {
                    addPublicSession.createOrIncrementCount(result.lotNumber)
                    addPublicSession.productId = result.productId
                    sendEvent(NavigateToLotInteraction)
                }

                is NewLot -> {
                    postNewLot(
                        lotNumber = result.lotNumber,
                        productId = result.productId,
                        expiration = result.expiration,
                        source = LotNumberSource.VaxHubScan
                    )
                    addPublicSession.createOrIncrementCount(result.lotNumber)
                    addPublicSession.productId = result.productId
                    sendEvent(NavigateToLotInteraction)
                }

                is Expired -> {
                    setState {
                        copy(
                            isScannerActive = false,
                            activeDialog = ExpiredProductScanned,
                        )
                    }
                }

                InvalidBarcode -> {
                    setState {
                        copy(
                            isScannerActive = true,
                            activeDialog = null,
                            isInvalidScan = true,
                        )
                    }
                }

                is WrongProduct -> {
                    val annotatedErrorMessage = convertHtml(result.errorMessage).toAnnotatedString()

                    setState {
                        copy(
                            isScannerActive = false,
                            activeDialog = WrongProductScanned(annotatedErrorMessage),
                        )
                    }
                }

                // These should never happen from this screen
                is ScanValidationResult.DuplicateLot,
                ScanValidationResult.MismatchedProduct -> Unit
            }
        }
    }
}
