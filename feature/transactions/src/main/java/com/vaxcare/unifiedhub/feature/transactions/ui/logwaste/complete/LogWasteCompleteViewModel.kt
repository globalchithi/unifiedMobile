package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.complete

import androidx.compose.ui.util.fastSumBy
import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.common.ext.toStandardDate
import com.vaxcare.unifiedhub.core.common.ext.toUSD
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.Icons
import com.vaxcare.unifiedhub.core.ui.Icons.presentationIcon
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.model.LotState
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.session.LogWasteSession
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.model.ProductUi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel(assistedFactory = LogWasteCompleteViewModel.Factory::class)
class LogWasteCompleteViewModel @AssistedInject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val lotRepository: LotRepository,
    private val productRepository: ProductRepository,
    @Assisted private val session: LogWasteSession,
) : BaseViewModel<LogWasteCompleteState, LogWasteCompleteEvent, LogWasteCompleteIntent>(
        LogWasteCompleteState()
    ) {
    @AssistedFactory
    interface Factory {
        fun create(session: LogWasteSession): LogWasteCompleteViewModel
    }

    override fun start() {
        viewModelScope.launch(dispatcherProvider.io) {
            val lotState = session.lotState.first().filter { !it.value.isDeleted }
            val isPrivate = session.stockType == StockType.PRIVATE
            val products = mapToProductUi(lotState, isPrivate)

            setState {
                val newState = copy(
                    stockType = StockUi.map(session.stockType),
                    date = LocalDate.now().toStandardDate(),
                    products = products,
                )

                if (isPrivate) {
                    val totalImpact: Float = products.fold(0f) { acc, product ->
                        acc + product.valueFloat
                    } * -1

                    newState.copy(
                        showImpact = true,
                        totalImpact = totalImpact.toUSD()
                    )
                } else {
                    val totalProducts: Int = products.fold(0) { acc, product ->
                        acc + product.quantity
                    }

                    newState.copy(
                        showImpact = false,
                        totalProducts = totalProducts.toString()
                    )
                }
            }
        }
    }

    private suspend fun mapToProductUi(lotState: Map<String, LotState>, isPrivate: Boolean): List<ProductUi> {
        val lotNumbers = lotState.keys.toList()
        val lots = lotNumbers.let { lotRepository.getLotsByNumber(it) }
        val products = productRepository.getProductsByLotNumber(lotNumbers)

        return products
            .map { product ->
                val productLots = lots.filter { it.productId == product.id }
                val quantity = productLots.fastSumBy { lotState[it.lotNumber]?.delta ?: 0 }
                val value = if (isPrivate) {
                    (product.lossFee ?: 0f) * quantity.toFloat()
                } else {
                    quantity.toFloat()
                }
                val previewSuffix = if (productLots.size == 1) {
                    ""
                } else {
                    " & ${productLots.size - 1} more"
                }

                ProductUi(
                    id = product.id,
                    antigen = product.antigen,
                    prettyName = product.prettyName ?: product.displayName,
                    quantity = quantity,
                    unitPrice = if (isPrivate) "${product.lossFee?.toUSD()} ea." else "",
                    value = if (isPrivate) value.toUSD() else value.toString(),
                    lotsPreview = "LOT# " + productLots.first().lotNumber + previewSuffix,
                    valueFloat = value,
                    presentationIcon = Icons.presentationIcon(product.presentation)
                )
            }.sortedBy {
                it.antigen
            }
    }

    override fun handleIntent(intent: LogWasteCompleteIntent) {
        when (intent) {
            LogWasteCompleteIntent.BackToHome, LogWasteCompleteIntent.LogOut -> {
                sendEvent(LogWasteCompleteEvent.NavigateToHome)
            }
        }
    }
}
