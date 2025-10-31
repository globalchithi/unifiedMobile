package com.vaxcare.unifiedhub.feature.transactions.ui.common.confirmstock.metrics

import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent

class StockConfirmedMetric(stock: StockType) : TrackableEvent {
    override val name: String
        get() = "StockConfirmed"

    override val props: AnalyticsProps = mapOf("stock" to stock.prettyName)
}
