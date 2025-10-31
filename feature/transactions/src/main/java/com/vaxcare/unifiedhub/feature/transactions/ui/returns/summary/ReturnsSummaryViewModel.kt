package com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary

import androidx.compose.ui.util.fastSumBy
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.repository.AdjustmentRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ReturnRepository
import com.vaxcare.unifiedhub.core.domain.SafeExpirationDateUseCase
import com.vaxcare.unifiedhub.core.model.AdjustmentType
import com.vaxcare.unifiedhub.core.model.inventory.AdjustmentEntry
import com.vaxcare.unifiedhub.core.model.inventory.ReturnedLot
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.navigation.ReturnsSummaryRoute
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.metric.AdjustmentFinishMetric
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.ProductUi
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.ProductUiMapper
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason.ReturnReasonUi
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.session.ReturnsSession
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary.ReturnsSummaryDialog.SubmissionFailed
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary.ReturnsSummaryEvent.NavigateBack
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary.ReturnsSummaryEvent.NavigateToReturnCompleted
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary.ReturnsSummaryIntent.DismissDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary.ReturnsSummaryIntent.GoBack
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary.ReturnsSummaryIntent.RetrySubmission
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary.ReturnsSummaryIntent.SubmitReturn
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = ReturnsSummaryViewModel.Factory::class)
class ReturnsSummaryViewModel @AssistedInject constructor(
    @Assisted private val session: ReturnsSession,
    private val analyticsRepository: AnalyticsRepository,
    private val adjustmentRepository: AdjustmentRepository,
    private val returnRepository: ReturnRepository,
    private val lotRepository: LotRepository,
    private val productUiMapper: ProductUiMapper,
    private val dispatcherProvider: DispatcherProvider,
    private val safeExpirationDate: SafeExpirationDateUseCase,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<ReturnsSummaryState, ReturnsSummaryEvent, ReturnsSummaryIntent>(
        initialState = ReturnsSummaryState()
    ) {
    @AssistedFactory
    interface Factory {
        fun create(session: ReturnsSession): ReturnsSummaryViewModel
    }

    private val args
        get() = savedStateHandle.toRoute<ReturnsSummaryRoute>()

    override fun start() {
        viewModelScope.launch(dispatcherProvider.io) {
            val products = productUiMapper.sessionToUi(lotState = session.submittableLotState.first())
            val total = products.fastSumBy(ProductUi::quantity)

            setState {
                copy(
                    stock = StockUi.map(args.stockType),
                    reason = ReturnReasonUi.fromDomain(args.reason),
                    total = total,
                    products = products
                )
            }
        }
    }

    override fun handleIntent(intent: ReturnsSummaryIntent) {
        when (intent) {
            DismissDialog -> {
                setState { copy(activeDialog = null) }
            }

            GoBack -> {
                sendEvent(NavigateBack)
            }

            RetrySubmission -> {
                submitReturn(isRetry = true)
            }

            SubmitReturn -> {
                submitReturn()
            }
        }
    }

    private fun submitReturn(isRetry: Boolean = false) {
        setState {
            copy(
                isLoading = true,
                activeDialog = null
            )
        }

        viewModelScope.launch(dispatcherProvider.io) {
            val isSuccessful = when (val stockType = args.stockType) {
                StockType.PRIVATE -> {
                    submitPrivateStockReturn(isRetry)
                }

                else -> {
                    submitPublicStockReturn(isRetry, stockType)
                }
            }

            trackAdjustment(isSuccessful)
            if (!isSuccessful) {
                setState {
                    copy(
                        activeDialog = SubmissionFailed,
                        isLoading = false
                    )
                }
                return@launch
            }

            sendEvent(NavigateToReturnCompleted)
        }
    }

    private suspend fun submitPrivateStockReturn(isRetry: Boolean): Boolean {
        return if (isRetry) {
            returnRepository.submitCachedReturn()
        } else {
            val lotState = session.submittableLotState.first()
            val lots = lotRepository.getLotsByNumber(lotState.keys.toList())
            returnRepository.buildAndSubmitReturn(
                groupGuid = session.groupGuid,
                stock = StockType.PRIVATE,
                reason = args.reason,
                noOfLabels = args.noOfLabels,
                pickup = session.pickup,
                returnedLots = lots.mapNotNull { lot ->
                    val state = lotState[lot.lotNumber] ?: run {
                        Timber.e("Lot #${lot.lotNumber} not found in LotNumber table!")
                        return@mapNotNull null
                    }

                    ReturnedLot(
                        lotNumber = lot.lotNumber,
                        productId = lot.productId,
                        count = state.count,
                        expirationDate = safeExpirationDate(lot.expiration)
                            .toLocalDate(),
                        receiptKey = state.receiptKey
                    )
                },
            )
        }
    }

    private suspend fun submitPublicStockReturn(isRetry: Boolean, stockType: StockType): Boolean {
        return if (isRetry) {
            adjustmentRepository.submitCachedAdjustment()
        } else {
            val lotState = session.submittableLotState.first()
            val lots = lotRepository.getLotsByNumber(lotState.keys.toList())
            adjustmentRepository.buildAndSubmitAdjustment(
                key = session.transactionKey,
                groupGuid = session.groupGuid,
                stock = stockType,
                type = AdjustmentType.RETURN,
                reason = args.reason,
                entries = lots.mapNotNull { lot ->
                    val state = lotState[lot.lotNumber] ?: run {
                        Timber.e("Lot #${lot.lotNumber} not found in LotNumber table!")
                        return@mapNotNull null
                    }

                    AdjustmentEntry(
                        lotNumber = lot.lotNumber,
                        salesProductId = lot.salesProductId,
                        delta = state.count * -1, // backend wants a negative number here
                        doseValue = 0f,
                        expiration = safeExpirationDate(lot.expiration).toString(),
                        receiptKey = state.receiptKey
                    )
                },
            )
        }
    }

    private suspend fun trackAdjustment(isSuccessful: Boolean) {
        analyticsRepository.track(
            AdjustmentFinishMetric(
                result = if (isSuccessful) {
                    AdjustmentFinishMetric.AdjustmentResult.SUBMITTED
                } else {
                    AdjustmentFinishMetric.AdjustmentResult.ERROR
                },
                productCount = currentState().products.size,
                doseCount = currentState().total * -1,
                financialImpact = 0.0
            )
        )
    }
}
