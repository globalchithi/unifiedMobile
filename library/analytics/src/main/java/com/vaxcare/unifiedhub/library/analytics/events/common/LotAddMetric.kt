package com.vaxcare.unifiedhub.library.analytics.events.common

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent

data class LotAddMetric(
    var screenSource: String = "",
    var scannerType: String = "Camera",
    var symbologyScanned: String = "",
    var productSource: String = "",
    var productId: Int = 0,
    var productName: String = "",
    var ndc: String = "",
    var lotNumber: String = "",
    var expirationDate: String = "",
    var rawBarcodeData: String = "",
) : TrackableEvent {
    override val name: String = "LotAdded"
    override val props: AnalyticsProps
        get() = super.props.toMutableMap().apply {
            put("screenSource", screenSource)
            put("scannerType", scannerType)
            put("symbologyScanned", symbologyScanned)
            put("productSource", productSource)
            put("productId", productId.toString())
            put("productName", productName)
            put("ndc", ndc)
            put("lotNumber", lotNumber)
            put("expirationDate", expirationDate)
            put("rawBarcodeData", rawBarcodeData)
        }
}
