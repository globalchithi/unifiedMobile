package com.vaxcare.unifiedhub.worker.args

import java.time.LocalDate

/**
 * ported from MH
 *
 * Arguments for InsertLotNumbersJob
 *
 * @property lotNumber name of new lot
 * @property epProductId associated product Id of new lot
 * @property expiration expiration date of new lot
 * @property source source of the lot insertion - this is either VaxHubScan (2) or Manual Entry(3)
 */
data class InsertLotNumbersJobArgs(
    val lotNumber: String? = null,
    val epProductId: Int? = null,
    val expiration: LocalDate? = null,
    val source: Int? = null
) {
    fun toMap(): Map<String, Any> =
        mapOf(
            "LotNumber" to (lotNumber ?: ""),
            "productId" to (epProductId?.toString() ?: ""),
            "expiration" to (expiration?.toString() ?: ""),
            "source" to (source?.toString() ?: "")
        )

    override fun toString(): String = "$lotNumber | $epProductId | $expiration"
}

data class LotInventoryArgs(
    val clearData: Boolean? = null,
    val updateMappingsAndCount: Boolean? = null
)
