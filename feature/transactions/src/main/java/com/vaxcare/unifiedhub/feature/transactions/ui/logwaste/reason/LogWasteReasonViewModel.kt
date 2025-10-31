package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason

import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.domain.model.TransactionSession
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.LogWasteReasonEvent.ReasonConfirmed
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.BROKEN_OR_CONTAMINATED
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.DELIVER_OUT_OF_TEMP
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.EXPIRED
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.OTHER
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.PREPPED_AND_NOT_ADMINISTERED
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.SPOILED_OR_OUT_OF_RANGE
import com.vaxcare.unifiedhub.feature.transactions.ui.metric.ReasonSelectedMetric
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = LogWasteReasonViewModel.Factory::class)
class LogWasteReasonViewModel @AssistedInject constructor(
    val analyticsRepository: AnalyticsRepository,
    val dispatcherProvider: DispatcherProvider,
    @Assisted private val transactionSession: TransactionSession,
) : BaseViewModel<LogWasteReasonState, LogWasteReasonEvent, LogWasteReasonIntent>(
        LogWasteReasonState()
    ) {
    @AssistedFactory
    interface Factory {
        fun create(session: TransactionSession): LogWasteReasonViewModel
    }

    override fun start() {
        setState { copy(stock = StockUi.map(transactionSession.stockType)) }
    }

    override fun handleIntent(intent: LogWasteReasonIntent) {
        when (intent) {
            is LogWasteReasonIntent.SelectReason -> {
                val reason = intent.reason
                viewModelScope.launch(dispatcherProvider.io) {
                    analyticsRepository.track(
                        ReasonSelectedMetric(
                            reasonContext = "LogWaste",
                            reason = reason.metricText
                        )
                    )
                }

                when (reason) {
                    SPOILED_OR_OUT_OF_RANGE,
                    BROKEN_OR_CONTAMINATED,
                    PREPPED_AND_NOT_ADMINISTERED,
                    OTHER -> setState { copy(selectedReason = intent.reason) }

                    EXPIRED -> setState { copy(activeDialog = LogWasteReasonDialog.ReturnExpiredProducts) }
                    DELIVER_OUT_OF_TEMP -> setState {
                        copy(
                            activeDialog = LogWasteReasonDialog.ReturnProductsDeliveredOutOfTemp
                        )
                    }
                }
            }

            is LogWasteReasonIntent.ConfirmReason -> {
                val reason = currentState().selectedReason ?: return

                when (reason) {
                    SPOILED_OR_OUT_OF_RANGE,
                    BROKEN_OR_CONTAMINATED,
                    PREPPED_AND_NOT_ADMINISTERED,
                    OTHER -> sendEvent(ReasonConfirmed(reason))

                    else -> return
                }
            }

            LogWasteReasonIntent.GoBack -> sendEvent(LogWasteReasonEvent.NavigateBack)
            LogWasteReasonIntent.CloseDialog -> setState { copy(activeDialog = null) }
        }
    }
}
