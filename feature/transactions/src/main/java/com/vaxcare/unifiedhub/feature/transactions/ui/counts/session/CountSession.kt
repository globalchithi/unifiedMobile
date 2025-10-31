package com.vaxcare.unifiedhub.feature.transactions.ui.counts.session

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.vaxcare.unifiedhub.core.common.ext.mapSync
import com.vaxcare.unifiedhub.core.data.model.inventory.enums.TransactionType
import com.vaxcare.unifiedhub.feature.transactions.domain.model.BaseTransactionSession
import com.vaxcare.unifiedhub.feature.transactions.model.LotState
import com.vaxcare.unifiedhub.feature.transactions.navigation.CountsSectionRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CountSession @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseTransactionSession() {
    private val _confirmedIds: MutableStateFlow<List<Int>> = MutableStateFlow(listOf())
    val confirmedIds = _confirmedIds.asStateFlow()

    override val transactionName = TransactionType.COUNT.name

    override var stockType = savedStateHandle.toRoute<CountsSectionRoute>().stockType

    val countGuid = UUID.randomUUID().toString()

    val fullLotState: StateFlow<Map<String, LotState>>
        get() = super.lotState

    override val lotState: StateFlow<Map<String, LotState>>
        get() = super.lotState.mapSync { it.filter { it.value.delta != null || it.value.isDeleted } }

    override fun confirmProduct(id: Int) {
        _confirmedIds.update {
            it + id
        }
    }

    fun initializeLotState(lotNumbers: List<String>) {
        lotNumbers.forEach {
            updateLot(it) {
                it ?: LotState(
                    delta = null, // Unconfirmed count state
                )
            }
        }
    }

    override fun containsSessionChanges(): Boolean = committedLotState.isNotEmpty() || confirmedIds.value.isNotEmpty()
}
