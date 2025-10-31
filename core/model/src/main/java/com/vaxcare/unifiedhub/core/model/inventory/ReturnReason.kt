package com.vaxcare.unifiedhub.core.model.inventory

enum class ReturnReason(val metricText: String) : AdjustmentReason {
    EXPIRED(metricText = "Expired"),
    EXCESS_INVENTORY(metricText = "Excess Inventory"),
    FRIDGE_OUT_OF_TEMP(metricText = "Fridge Out of Temp Range"),
    DELIVER_OUT_OF_TEMP(metricText = "Delivered Out of Temp Range"),
    RECALLED_BY_MANUFACTURER(metricText = "Recalled by Manufacturer"),
    DAMAGED_IN_TRANSIT(metricText = "Damaged in Transit");

    override fun getReasonString(): String =
        when (this) {
            EXPIRED -> "Expired doses"
            EXCESS_INVENTORY -> "Excess doses"
            FRIDGE_OUT_OF_TEMP -> "Doses from fridge out of temp. range"
            DELIVER_OUT_OF_TEMP -> "Doses delivered out of temp. range"
            RECALLED_BY_MANUFACTURER -> "Doses recalled by the manufacturer"
            DAMAGED_IN_TRANSIT -> "Doses damaged in transit"
        }
}
