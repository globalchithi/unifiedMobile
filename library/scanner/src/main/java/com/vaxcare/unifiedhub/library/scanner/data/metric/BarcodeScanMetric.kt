package com.vaxcare.unifiedhub.library.scanner.data.metric

import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent

data class BarcodeScanMetric(
    val rawBarcode: String,
    val symbology: String
) : TrackableEvent {
    override val name = "BarcodeScanned"
    override val props: AnalyticsProps
        get() = super.props.toMutableMap().apply {
            put("rawBarcode", rawBarcode)
            put("symbology", symbology)
        }
}
