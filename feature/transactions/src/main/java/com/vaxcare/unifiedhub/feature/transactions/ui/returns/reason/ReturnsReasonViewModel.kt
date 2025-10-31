package com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.navigation.ReturnReasonRoute
import com.vaxcare.unifiedhub.feature.transactions.ui.metric.ReasonSelectedMetric
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReturnsReasonViewModel @Inject constructor(
    val analyticsRepository: AnalyticsRepository,
    val dispatcherProvider: DispatcherProvider,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<ReturnReasonState, ReturnReasonEvent, ReturnReasonIntent>(
        ReturnReasonState()
    ) {
    private val args = savedStateHandle.toRoute<ReturnReasonRoute>()

    override fun start() {
        setState { copy(stock = StockUi.map(args.stockType)) }
    }

    override fun handleIntent(intent: ReturnReasonIntent) {
        when (intent) {
            is ReturnReasonIntent.SelectReason -> {
                val metricText = intent.reason.toDomain().metricText

                viewModelScope.launch(dispatcherProvider.io) {
                    analyticsRepository.track(
                        ReasonSelectedMetric(
                            reasonContext = "ReturnReason",
                            reason = metricText
                        )
                    )
                }
                setState { copy(selectedReason = intent.reason) }
            }

            is ReturnReasonIntent.ConfirmReason -> {
                val reason = currentState().selectedReason ?: return
                sendEvent(ReturnReasonEvent.ReasonConfirmed(reason))
            }

            ReturnReasonIntent.GoBack -> sendEvent(ReturnReasonEvent.NavigateBack)
        }
    }
}
