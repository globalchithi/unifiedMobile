package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.session

import com.vaxcare.unifiedhub.core.data.model.inventory.enums.TransactionType
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.feature.transactions.domain.model.BaseTransactionSession
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LogWasteSession @Inject constructor() : BaseTransactionSession() {
    override val transactionName = TransactionType.LOSS_WASTE.name

    override var stockType: StockType = StockType.PRIVATE

    override fun confirmProduct(id: Int) {
        TODO("Not yet implemented")
    }

    private val _wasteReason = MutableStateFlow<LogWasteReason?>(null)
    val wasteReason = _wasteReason.asStateFlow()

    fun setWasteReason(reason: LogWasteReason) {
        _wasteReason.value = reason
    }

    fun addLotToWaste(productId: Int, lotNumber: String) {
        addLot(productId, lotNumber)
        setDelta(lotNumber, 1)
    }

    override fun containsSessionChanges(): Boolean = lotState.value.values.any { (it.delta ?: 0) > 0 }
}
