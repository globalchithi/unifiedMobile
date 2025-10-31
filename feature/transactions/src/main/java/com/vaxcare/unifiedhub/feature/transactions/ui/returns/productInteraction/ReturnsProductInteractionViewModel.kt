package com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.domain.ConvertHtmlUseCase
import com.vaxcare.unifiedhub.core.domain.PostNewLotUseCase
import com.vaxcare.unifiedhub.core.domain.ValidateScannedProductUseCase
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult
import com.vaxcare.unifiedhub.core.model.inventory.LotInventory
import com.vaxcare.unifiedhub.core.model.inventory.ReturnReason
import com.vaxcare.unifiedhub.core.model.lot.LotNumberSource
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.ext.toAnnotatedString
import com.vaxcare.unifiedhub.feature.transactions.navigation.ReturnProductInteractionRoute
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.ProductLotUiMapper
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionEvent.NavigateBack
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionEvent.NavigateToLotSearch
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionEvent.NextScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.CloseScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.ConfirmDiscardChanges
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.DeleteLot
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.DismissDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.GoForward
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.OpenKeypad
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.ScanLot
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.SearchLot
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.SubmitKeypadInput
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.UndoDelete
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent.UpdateLotCount
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason.ReturnReasonUi
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.session.ReturnsSession
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate

@HiltViewModel(assistedFactory = ReturnsProductInteractionViewModel.Factory::class)
class ReturnsProductInteractionViewModel @AssistedInject constructor(
    @Assisted private val session: ReturnsSession,
    private val lotInventoryRepository: LotInventoryRepository,
    private val lotRepository: LotRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val mapper: ProductLotUiMapper,
    private val validateScannedProduct: ValidateScannedProductUseCase,
    private val postNewLot: PostNewLotUseCase,
    private val convertHtml: ConvertHtmlUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val locationRepository: LocationRepository
) : BaseViewModel<ReturnsProductInteractionState, ReturnsProductInteractionEvent, ReturnsProductInteractionIntent>(
        ReturnsProductInteractionState()
    ) {
    @AssistedFactory
    interface Factory {
        fun create(session: ReturnsSession): ReturnsProductInteractionViewModel
    }

    companion object {
        private const val DISABLE_AUTOMATED_RETURNS_FF_NAME = "DisableAutomatedReturns"
    }

    private val args
        get() = savedStateHandle.toRoute<ReturnProductInteractionRoute>()

    var isAutomatedReturnsEnabled: Boolean? = null
        private set

    override fun start() {
        viewModelScope.launch(dispatcherProvider.io) {
            if (args.reason == ReturnReason.EXPIRED) {
                populateExpiredInventoryToSession()
            }

            isAutomatedReturnsEnabled = !locationRepository
                .getFeatureFlagsAsync()
                .any { it.featureFlagName == DISABLE_AUTOMATED_RETURNS_FF_NAME }

            session.lotState
                .map(mapper::sessionToUi)
                .collectLatest {
                    setState {
                        copy(
                            isLoading = false,
                            isScannerActive = true,
                            lots = it,
                            reason = ReturnReasonUi.fromDomain(args.reason),
                        )
                    }
                }
        }
    }

    private suspend fun populateExpiredInventoryToSession() {
        getExpiredInventory()
            .sortedBy { it.antigen?.lowercase() + it.lotNumber }
            .associateBy(
                keySelector = { it.lotNumber },
                valueTransform = {
                    ReturnsSession.LotState(count = it.onHand)
                }
            ).let(session::populateLotState)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getExpiredInventory(): List<LotInventory> =
        lotRepository
            .getLotsExpiringBefore(LocalDate.now().plusDays(7))
            .flatMapLatest { expiredLots ->
                val lotNumbers = expiredLots.map { it.lotNumber }
                lotInventoryRepository
                    .getLotInventory(lotNumbers, args.stockType)
                    .map { inventory ->
                        inventory.filter { it.onHand > 0 }
                    }
            }.first()

    override fun handleIntent(intent: ReturnsProductInteractionIntent) {
        with(intent) {
            when (this) {
                CloseScreen -> {
                    if (session.containsSessionChanges()) {
                        setState {
                            copy(
                                isScannerActive = false,
                                activeDialog = ReturnsProductInteractionDialog.DiscardChanges,
                            )
                        }
                    } else {
                        session.hardReset()
                        sendEvent(NavigateBack)
                    }
                }

                ConfirmDiscardChanges -> {
                    session.hardReset()
                    sendEvent(NavigateBack)
                }

                is DeleteLot -> {
                    session.setDeleted(lotNumber, true)
                }

                DismissDialog -> {
                    setState {
                        copy(
                            isScannerActive = true,
                            activeDialog = null,
                        )
                    }
                }

                GoForward -> {
                    sendEvent(NextScreen)
                }

                is OpenKeypad -> {
                    setState {
                        copy(
                            isScannerActive = false,
                            activeDialog = ReturnsProductInteractionDialog.Keypad(lotNumber)
                        )
                    }
                }

                is ScanLot -> {
                    validateParsedBarcode(barcode)
                }

                SearchLot -> {
                    sendEvent(NavigateToLotSearch(args.stockType.id))
                }

                is SubmitKeypadInput -> {
                    session.setCount(lotNumber, count)
                    setState {
                        copy(
                            isScannerActive = true,
                            activeDialog = null
                        )
                    }
                }

                is UndoDelete -> {
                    session.setDeleted(lotNumber, false)
                }

                is UpdateLotCount -> {
                    session.adjustCount(lotNumber, change)
                }
            }
        }
    }

    private fun validateParsedBarcode(parsedBarcode: ParsedBarcode) {
        setState {
            copy(isScannerActive = false)
        }

        viewModelScope.launch(dispatcherProvider.default) {
            val result = validateScannedProduct(
                parsedBarcode = parsedBarcode,
                existingLotNumbers = session.lotState.first().map { it.key },
                analyticsScreenSource = "Product Interaction: Returns"
            )

            when (result) {
                is ScanValidationResult.Expired,
                is ScanValidationResult.Valid -> {
                    session.setCount(result.lotNumber!!, 1)
                }

                is ScanValidationResult.NewLot -> {
                    postNewLot(
                        lotNumber = result.lotNumber,
                        productId = result.productId,
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
                            error = null
                        )
                    }
                }

                is ScanValidationResult.WrongProduct -> {
                    val annotatedErrorMessage = convertHtml(result.errorMessage).toAnnotatedString()

                    setState {
                        copy(
                            isScannerActive = false,
                            activeDialog = ReturnsProductInteractionDialog.WrongProduct(
                                annotatedErrorMessage
                            ),
                            error = null
                        )
                    }
                }

                ScanValidationResult.InvalidBarcode -> {
                    setState {
                        copy(
                            isScannerActive = true,
                            activeDialog = null,
                            error = ReturnsProductInteractionError.BadBarcodeScan
                        )
                    }
                }

                // the compiler thinks 'MismatchedProduct' is possible, but it is not. Adding a log
                // just in case.
                else -> {
                    Timber.e(
                        "Impossible state reached: Scan result was 'MismatchedProduct' in a product-agnostic context."
                    )
                }
            }
        }
    }
}
