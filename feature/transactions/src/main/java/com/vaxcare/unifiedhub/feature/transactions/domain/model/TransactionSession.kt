package com.vaxcare.unifiedhub.feature.transactions.domain.model

import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.feature.transactions.model.LotState
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.session.CountSession
import kotlinx.coroutines.flow.StateFlow

/**
 * Session of a transaction that is currently ongoing
 */
@Deprecated("Do not use TransactionSession")
interface TransactionSession {
    val transactionName: String

    /**
     * StockType selected for the session
     */
    val stockType: StockType

    /**
     * Key which needs to remain the same during session for backend submission
     */
    val transactionKey: String

    /**
     * GroupGuid which needs to remain the same during session for backend submission
     */
    val groupGuid: String

    /**
     * A [StateFlow] containing the **current state** of Lots on the present screen. Each recorded
     * [CountSession.LotState] is mapped by its lot number [String].
     */
    val lotState: StateFlow<Map<String, LotState>>

    /**
     * This contains the latest **committed state**. See [commitChanges]. Each recorded [CountSession.LotState]
     * is mapped by its lot number [String].
     */
    val committedLotState: Map<String, LotState>

    /**
     * A [StateFlow] containing any lots that were added to a product during the session. Each is
     * represented by a [Set] entry: { productId: [Int] to lotNumber: [String] }
     */
    val addedLots: StateFlow<Set<Pair<Int, String>>>

    /**
     * A [StateFlow] containing the most recently searched lot number ([String]) or `null`.
     *
     * This should be reset to `null` upon leaving the scope of the lot search that set it.
     */
    val searchedLot: StateFlow<String?>

    /**
     * The most recently selected product id ([Int]) or `null`.
     *
     * This should be reset to `null` upon leaving the scope that wrote the current value.
     */
    var productId: Int?

    fun setDelta(lotNumber: String, delta: Int)

    fun setDeleted(lotNumber: String, isDeleted: Boolean)

    fun setSearchedLot(lotNumber: String)

    /**
     * Add a new lot to [addedLots].
     *
     * @param productId id of the associated
     * @param lotNumber lot number of the lot being added
     */
    fun addLot(productId: Int?, lotNumber: String?)

    fun updateLot(lotNumber: String, mutator: (LotState?) -> LotState)

    fun confirmProduct(id: Int)

    fun containsUncommittedChanges(): Boolean

    fun containsSessionChanges(): Boolean

    /**
     * Set [committedLotState] to the current value of [lotState]
     */
    fun commitChanges()

    /**
     * Reset [lotState] to the most recent commit, which is stored in [committedLotState]
     *
     */
    fun resetLotState()

    fun clearSearchedLot()
}
