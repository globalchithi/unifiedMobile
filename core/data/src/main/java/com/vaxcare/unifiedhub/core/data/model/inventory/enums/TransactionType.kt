package com.vaxcare.unifiedhub.core.data.model.inventory.enums

import com.vaxcare.unifiedhub.core.database.model.enums.InventorySource

enum class TransactionType(
    val id: Int,
    val label: String,
    val header: String,
    val columnText: String,
    val lotColumnText: String,
    val delta: Int
) {
    TRANSFER_IN(11, "Transfer In", "Transfer In from %1\$s", "Transferred", "Transfer", 1),
    RECEIVE_DELIVERY(15, "Add Doses - Receive Shipment", "Add Doses - Receive Shipment", "Received", "Receive", 1),
    VAX_BUY(31, "VaxBuy", "VaxBuy", "Added", "Add", 1),
    TRANSFER_OUT(12, "Transfer Out", "Transfer Out to %1\$s", "Transferred", "Transfer", -1),
    LOSS_WASTE(13, "Loss/Waste", "Loss/Waste", "Loss/Waste", "Loss/Waste", -1),
    RETURN(16, "Return Doses", "Return Doses", "Returned", "Return", -1),
    FIRST_COUNT(0, "Count", "Count Confirmation", "Confirmed", "Confirm", 1),
    COUNT(0, "Count", "Count Confirmation", "Confirmed", "Confirm", 1),
    BUY_BACK(34, "BuyBack", "BuyBack", "Added", "Add", 1),
    ADD_DOSES_FROM_MANUFACTURER(
        35,
        "Add Doses - Directly From Manufacturer",
        "Add Doses - Directly From Manufacturer",
        "Added",
        "Add",
        1
    );

    fun isCount() = this == COUNT || this == FIRST_COUNT

    fun isTransferBetweenStocks(destination: InventorySource?) =
        isTransfer() &&
            destination !in listOf(
                InventorySource.ANOTHER_LOCATION,
                InventorySource.ANOTHER_STOCK
            )

    fun isTransfer() = this == TRANSFER_IN || this == TRANSFER_OUT

    fun isAdd() = this in listOf(RECEIVE_DELIVERY, VAX_BUY, BUY_BACK, ADD_DOSES_FROM_MANUFACTURER)

    /**
     * Implement this in the following flows:
     * Add Doses / Buyback / Transfer Doses (location, stock) / Counts
     *
     * but not in these flows:
     * Log Waste / Return Doses
     */
    fun isHistorical() = this !in listOf(LOSS_WASTE, RETURN)

    companion object {
        fun getFromId(id: Int) = entries.firstOrNull { it.id == id } ?: TRANSFER_IN
    }
}
