package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary

import androidx.compose.ui.util.fastSumBy
import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.common.ext.toUSD
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import com.vaxcare.unifiedhub.core.domain.SafeExpirationDateUseCase
import com.vaxcare.unifiedhub.core.model.AdjustmentType
import com.vaxcare.unifiedhub.core.model.inventory.Adjustment
import com.vaxcare.unifiedhub.core.model.inventory.AdjustmentEntry
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.model.LotState
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.session.LogWasteSession
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.metric.AdjustmentFinishMetric
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.model.ProductUi
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import com.vaxcare.unifiedhub.core.designsystem.R as DesignR

private fun Presentation.getResourceId(): Int =
    when (this) {
        Presentation.SINGLE_DOSE_VIAL -> DesignR.drawable.ic_presentation_single_vial
        Presentation.SINGLE_DOSE_TUBE -> DesignR.drawable.ic_presentation_single_tube
        Presentation.MULTI_DOSE_VIAL -> DesignR.drawable.ic_presentation_multi_vial
        Presentation.PREFILLED_SYRINGE -> DesignR.drawable.ic_presentation_syringe
        Presentation.NASAL_SPRAY -> DesignR.drawable.ic_presentation_nasal
        Presentation.NASAL_SYRINGE -> DesignR.drawable.ic_presentation_nasal
        Presentation.IUD -> DesignR.drawable.ic_presentation_iud
        Presentation.IMPLANT -> DesignR.drawable.ic_presentation_implant
        Presentation.UNKNOWN -> DesignR.drawable.ic_presentation_syringe
    }

@HiltViewModel(assistedFactory = LogWasteSummaryViewModel.Factory::class)
class LogWasteSummaryViewModel @AssistedInject constructor(
    @Assisted private val session: LogWasteSession,
    private val analyticsRepository: AnalyticsRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val lotRepository: LotRepository,
    private val productRepository: ProductRepository,
    private val lotInventoryRepository: LotInventoryRepository,
    private val userSession: UserSessionPreferenceDataSource,
    private val safeExpirationDate: SafeExpirationDateUseCase
) : BaseViewModel<LogWasteSummaryState, LogWasteSummaryEvent, LogWasteSummaryIntent>(
        initialState = LogWasteSummaryState()
    ) {
    private var adjustment: Adjustment? = null

    @AssistedFactory
    interface Factory {
        fun create(session: LogWasteSession): LogWasteSummaryViewModel
    }

    override fun start() {
        super.start()
        setState { copy(stock = StockUi.map(session.stockType)) }
        viewModelScope.launch(dispatcherProvider.io) {
            session.lotState
                .combine(session.wasteReason, ::Pair)
                .collectLatest { (lotsInSession, reason) ->
                    val isPrivate = session.stockType == StockType.PRIVATE
                    val lotNumbers = lotsInSession.filter { !it.value.isDeleted }.keys.toList()
                    val lotDetails = lotRepository.getLotsByNumber(lotNumbers)
                    val productIds = lotDetails.map { it.productId }.distinct()
                    val productDetails =
                        productRepository.getProductsByIds(productIds)
                    adjustment = adjustment ?: Adjustment(
                        key = session.transactionKey,
                        groupGuid = session.groupGuid,
                        adjustmentReasonType = reason!!.reasonType,
                        type = AdjustmentType.LOG_WASTE,
                        adjustments = emptyList(),
                        stockId = session.stockType.id.toString(),
                        userId = userSession.userId.first(),
                        userName = userSession.userName.first()
                    )

                    val productList = mapToUiModelAndPopulateAdjustmentEntries(
                        lotsInSession = lotsInSession,
                        lotDetails = lotDetails,
                        productDetails = productDetails,
                        isPrivate = isPrivate
                    )

                    val total = productList.map { it.valueFloat }.sum() * -1

                    setState {
                        copy(
                            reason = reason!!,
                            products = productList,
                            isLoading = false,
                            activeDialog = null,
                            total = if (isPrivate) total.toUSD() else total.toInt().toString()
                        )
                    }
                }
        }
    }

    private fun mapToUiModelAndPopulateAdjustmentEntries(
        lotsInSession: Map<String, LotState>,
        lotDetails: List<Lot>,
        productDetails: List<Product>,
        isPrivate: Boolean
    ): List<ProductUi> {
        val lotsMap = lotDetails.associateBy { it.lotNumber }
        val productUiAndAdjustmentEntries = productDetails.map { product ->
            val lots = lotsMap.filter { it.value.productId == product.id }.map { it.value }
            val quantity = lots.fastSumBy { (lotsInSession[it.lotNumber]?.delta ?: 0) }
            val value = if (isPrivate) {
                (product.lossFee ?: 0f) * quantity.toFloat()
            } else {
                quantity.toFloat()
            }
            val previewSuffix = if (lots.size == 1) {
                ""
            } else {
                "& ${lots.size - 1} more"
            }

            val productUi = ProductUi(
                id = product.id,
                antigen = product.antigen,
                prettyName = "(${product.prettyName ?: product.displayName})",
                quantity = quantity,
                unitPrice = if (isPrivate) "${product.lossFee?.toUSD()} ea." else "",
                value = if (isPrivate) value.toUSD() else value.toString(),
                lotsPreview = "${lots.first().lotNumber} $previewSuffix",
                valueFloat = value,
                presentationIcon = product.presentation.getResourceId()
            )

            val adjustmentEntries = lots.map { lot ->
                val edited = lotsInSession[lot.lotNumber]
                AdjustmentEntry(
                    lotNumber = lot.lotNumber,
                    salesProductId = lot.salesProductId,
                    delta = (edited?.delta ?: 0) * -1,
                    doseValue = if (isPrivate) {
                        product.lossFee ?: 0f
                    } else {
                        0f
                    },
                    expiration = safeExpirationDate(lot.expiration).toString(),
                    receiptKey = edited?.receiptKey ?: UUID.randomUUID().toString()
                )
            }

            productUi to adjustmentEntries
        }

        val productUiList = productUiAndAdjustmentEntries.map { it.first }
        val adjustmentEntries = productUiAndAdjustmentEntries.flatMap { it.second }
        adjustment = adjustment?.copy(adjustments = adjustmentEntries)
        return productUiList.sortedBy { it.antigen }
    }

    override fun handleIntent(intent: LogWasteSummaryIntent) {
        when (intent) {
            LogWasteSummaryIntent.DismissDialog -> setState { copy(activeDialog = null) }
            LogWasteSummaryIntent.GoBack -> sendEvent(LogWasteSummaryEvent.NavigateBack)
            LogWasteSummaryIntent.RetrySubmission -> startSubmittingTransaction()
            LogWasteSummaryIntent.SubmitLogWaste -> startSubmittingTransaction()
        }
    }

    private fun startSubmittingTransaction() {
        setState { copy(activeDialog = null, isLoading = true) }
        viewModelScope.launch(dispatcherProvider.io) {
            try {
                adjustment?.let {
                    lotInventoryRepository.postInventoryAdjustments(it)
                } ?: throw Exception("Impossible exception: Adjustments are null")

                trackAdjustmentFinished(true)
                sendEvent(LogWasteSummaryEvent.NavigateToLogWasteCompleted)
            } catch (e: Exception) {
                Timber.e(e)
                setState {
                    copy(
                        activeDialog = LogWasteSummaryDialog.SubmissionFailed,
                        isLoading = false
                    )
                }
                trackAdjustmentFinished(false)
            }
        }
    }

    private suspend fun trackAdjustmentFinished(isSuccessful: Boolean) {
        val productCount = adjustment?.adjustments?.groupBy { it.salesProductId }?.size ?: 0
        val doseCount = adjustment?.adjustments?.fastSumBy { it.delta } ?: 0
        analyticsRepository.track(
            AdjustmentFinishMetric(
                result = if (isSuccessful) {
                    AdjustmentFinishMetric.AdjustmentResult.SUBMITTED
                } else {
                    AdjustmentFinishMetric.AdjustmentResult.ERROR
                },
                productCount = productCount,
                doseCount = doseCount * -1,
                financialImpact = if (session.stockType == StockType.PRIVATE) {
                    currentState()
                        .total
                        .replace("$", "")
                        .replace(",", "")
                        .toFloat()
                } else {
                    0.0
                }
            )
        )
    }
}
