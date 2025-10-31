package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.AddPublicSectionRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddPublicSession @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    /**
     *  Minimum value for a lot count
     */
    private val minCount = 1

    var stockType = savedStateHandle.toRoute<AddPublicSectionRoute>().stockType

    // Transaction keys for backend submission
    val transactionKey: String = UUID.randomUUID().toString()
    val groupGuid: String = UUID.randomUUID().toString()

    /**
     * A [StateFlow] containing the **current state** of Lots on the present screen. Each recorded
     * [LotState] is mapped by its lot number [String].
     */
    private val _lotState: MutableStateFlow<Map<String, LotState>> = MutableStateFlow(mapOf())
    val lotState: StateFlow<Map<String, LotState>>
        get() = _lotState.asStateFlow()
    val submittableLotState
        get() = lotState.onEach { emission ->
            emission.filter { !it.value.isDeleted }
        }

    /**
     * This contains the latest **committed state**. See [commitChanges]. Each recorded [LotState]
     * is mapped by its lot number [String].
     */
    private val _committedLotState = mutableMapOf<String, LotState>()
    val committedLotState: Map<String, LotState> // source of truth
        get() = _committedLotState

    /**
     * A [StateFlow] containing the most recently searched lot number ([String]) or `null`.
     *
     * This should be reset to `null` upon leaving the scope of the lot search that set it.
     */
    private val _searchedLot: MutableStateFlow<String?> = MutableStateFlow(null)
    val searchedLot: StateFlow<String?>
        get() = _searchedLot.asStateFlow()

    /**
     * The most recently selected product id ([Int]) or `null`.
     *
     * This should be reset to `null` upon leaving the scope that wrote the current value.
     */
    var productId: Int? = null

    /**
     * A [StateFlow] containing the **current state** of Products on the present screen. Each recorded
     * [ProductState] is mapped by its product id [Int].
     */
    private val _productState: MutableStateFlow<Map<Int, ProductState>> = MutableStateFlow(mapOf())
    val productState: StateFlow<Map<Int, ProductState>>
        get() = _productState.asStateFlow()

    fun setCount(lotNumber: String, count: Int) {
        updateLot(lotNumber) {
            it?.copy(
                count = floorCount(count),
            ) ?: LotState(
                count = floorCount(count),
            )
        }
    }

    /**
     * Increments [LotState.count] for [lotNumber] by 1. Sets [LotState.count]. If
     * [LotState] does not exist for [lotNumber], a new [LotState] is created with
     * [LotState.count] set to 1.
     */
    fun createOrIncrementCount(lotNumber: String) {
        createOrUpdateCount(lotNumber, 1)
    }

    /**
     * Change [LotState.count] for [lotNumber] by value of [change] (this can be positive or
     * negative). If [LotState] does not exist for [lotNumber], a new [LotState] is created
     * with [LotState.count] set to 1.
     */
    fun createOrUpdateCount(lotNumber: String, change: Int) {
        updateLot(lotNumber) {
            it?.copy(
                count = floorCount(it.count + change)
            ) ?: LotState(
                count = floorCount(change)
            )
        }
    }

    fun setDeleted(lotNumber: String, isDeleted: Boolean) {
        updateLot(lotNumber) {
            it?.copy(
                isDeleted = isDeleted,
            ) ?: LotState(
                isDeleted = isDeleted
            )
        }
    }

    fun setSearchedLot(lotNumber: String) {
        _searchedLot.update { lotNumber }
    }

    fun setDeletedProduct(productId: Int, isDeleted: Boolean) {
        updateProduct(productId) {
            it?.copy(
                isDeleted = isDeleted
            ) ?: ProductState(
                isDeleted = isDeleted
            )
        }
    }

    /**
     * @return Whether or not changes have been made since beginning the session.
     */
    fun containsSessionChanges(): Boolean = committedLotState.isNotEmpty()

    /**
     * @return Whether or not there are changes that have not yet been saved in [committedLotState]
     */
    fun containsUncommittedChanges(): Boolean {
        val currentState = _lotState.value

        // check for mismatched keys
        val keysChanged = currentState.keys
            .intersect(_committedLotState.keys)
            .size != _committedLotState.size

        if (keysChanged) return true

        // if no new entries, check existing values
        return currentState.any { (lotNumber, state) ->
            val committed = _committedLotState[lotNumber]
            state.isDeleted != committed?.isDeleted || state.count != committed.count
        }
    }

    /**
     * Set [committedLotState] to the current value of [lotState]
     */
    fun commitChanges() {
        _committedLotState.putAll(lotState.value)
        resetLotState()
    }

    /**
     * Reset [lotState] to the most recent commit, which is stored in [committedLotState]
     */
    fun resetLotState() {
        _lotState.update { committedLotState }
    }

    fun clearSearchedLot() {
        _searchedLot.update { null }
    }

    /**
     * Enforce floor for a count value
     * @return [count] or [minCount] (if [count] is less than [minCount])
     */
    private fun floorCount(count: Int): Int = if (count >= minCount) count else minCount

    private fun updateLot(lotNumber: String, mutator: (LotState?) -> LotState) {
        _lotState.update { session ->
            val countLot = mutator(session[lotNumber])
            session + mutableMapOf(lotNumber to countLot).toMutableMap()
        }
    }

    private fun updateProduct(productId: Int, mutator: (ProductState?) -> ProductState) {
        _productState.update { session ->
            val productState = mutator(session[productId])
            session + mutableMapOf(productId to productState).toMutableMap()
        }
    }

    data class LotState(
        val count: Int = 1,
        val isDeleted: Boolean = false,
        val receiptKey: String = UUID.randomUUID().toString()
    )

    data class ProductState(val isDeleted: Boolean = false)
}
