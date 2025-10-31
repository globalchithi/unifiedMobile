package com.vaxcare.unifiedhub.feature.pinin.model

import com.vaxcare.unifiedhub.core.common.ext.addIfNotEmpty
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import com.vaxcare.unifiedhub.library.analytics.core.TrackableEvent

enum class PinningStatus {
    SUCCESS,
    FAIL
}

data class PinInStatus(
    val pinningStatus: PinningStatus,
    val pinUsed: String,
    val responseCode: Int? = null,
    val userFoundLocally: Boolean? = null,
    val username: String? = null,
) : TrackableEvent {
    override val name = "PinInStatus"

    override val props: AnalyticsProps
        get() = super.props.toMutableMap().apply {
            put("pinningStatus", pinningStatus.name)
            put("pinUsed", pinUsed)
            responseCode?.let { addIfNotEmpty("ResponseCode", it.toString()) }
            userFoundLocally?.let { addIfNotEmpty("userFoundLocally", it.toString()) }
            username?.let { addIfNotEmpty("username", it) }
        }
}
