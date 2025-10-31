package com.vaxcare.unifiedhub.core.model.inventory

interface AdjustmentReason {
    /**
     * @return the string representation of the Reason that is expected by the backend.
     */
    fun getReasonString(): String
}
