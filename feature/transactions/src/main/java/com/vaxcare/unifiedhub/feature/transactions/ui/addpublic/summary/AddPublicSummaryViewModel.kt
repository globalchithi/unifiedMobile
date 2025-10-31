package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.summary

import androidx.compose.ui.util.fastSumBy
import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.repository.AdjustmentRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.domain.SafeExpirationDateUseCase
import com.vaxcare.unifiedhub.core.model.AdjustmentType
import com.vaxcare.unifiedhub.core.model.inventory.AdjustmentEntry
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUi
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUiMapper
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.session.AddPublicSession
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.metric.AdjustmentFinishMetric
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = AddPublicSummaryViewModel.Factory::class)
class AddPublicSummaryViewModel @AssistedInject constructor(
    @Assisted private val session: AddPublicSession,
    private val analyticsRepository: AnalyticsRepository,
    private val adjustmentRepository: AdjustmentRepository,
    private val lotRepository: LotRepository,
    private val productUiMapper: ProductUiMapper,
    private val dispatcherProvider: DispatcherProvider,
    private val safeExpirationDate: SafeExpirationDateUseCase
) : BaseViewModel<AddPublicSummaryState, AddPublicSummaryEvent, AddPublicSummaryIntent>(
        initialState = AddPublicSummaryState()
    ) {
    @AssistedFactory
    interface Factory {
        fun create(session: AddPublicSession): AddPublicSummaryViewModel
    }

    override fun start() {
        viewModelScope.launch(dispatcherProvider.io) {
            val products = productUiMapper.sessionToUi(
                lotState = session.submittableLotState.first(),
                productState = session.productState.first(),
            )
            val total = products.fastSumBy(ProductUi::getTotal)

            setState {
                copy(
                    stock = StockUi.map(session.stockType),
                    total = total,
                    products = products
                )
            }
        }
    }

    override fun handleIntent(intent: AddPublicSummaryIntent) {
        when (intent) {
            AddPublicSummaryIntent.DismissDialog -> setState { copy(activeDialog = null) }
            AddPublicSummaryIntent.GoBack -> sendEvent(AddPublicSummaryEvent.NavigateBack)
            AddPublicSummaryIntent.RetrySubmission -> submitAdjustment(isRetry = true)
            AddPublicSummaryIntent.SubmitAddPublic -> submitAdjustment()
        }
    }

    private fun submitAdjustment(isRetry: Boolean = false) {
        setState {
            copy(
                isLoading = true,
                activeDialog = null
            )
        }

        viewModelScope.launch(dispatcherProvider.io) {
            val isSuccessful = if (isRetry) {
                adjustmentRepository.submitCachedAdjustment()
            } else {
                val lotState = session.submittableLotState.first()
                val lots = lotRepository.getLotsByNumber(lotState.keys.toList())
                adjustmentRepository.buildAndSubmitAdjustment(
                    key = session.transactionKey,
                    groupGuid = session.groupGuid,
                    stock = session.stockType,
                    type = AdjustmentType.ADD_PUBLIC,
                    entries = lots.mapNotNull { lot ->
                        val state = lotState[lot.lotNumber] ?: run {
                            Timber.e("Lot #${lot.lotNumber} not found in LotNumber table!")
                            return@mapNotNull null
                        }

                        AdjustmentEntry(
                            lotNumber = lot.lotNumber,
                            salesProductId = lot.salesProductId,
                            delta = state.count,
                            doseValue = 0f,
                            expiration = safeExpirationDate(lot.expiration).toString(),
                            receiptKey = state.receiptKey
                        )
                    },
                )
            }

            trackAdjustment(isSuccessful)
            if (!isSuccessful) {
                setState {
                    copy(
                        activeDialog = AddPublicSummaryDialog.SubmissionFailed,
                        isLoading = false
                    )
                }
                return@launch
            }

            sendEvent(AddPublicSummaryEvent.NavigateToAddPublicCompleted)
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
