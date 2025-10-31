package com.vaxcare.unifiedhub.core.model.lot

/**
 * Represents the different sources of newly introduced product lots.
 */
sealed class LotNumberSource(val id: Int) {
    data object Shipments : LotNumberSource(1)

    data object VaxHubScan : LotNumberSource(2)

    data object ManualEntry : LotNumberSource(3)
}
