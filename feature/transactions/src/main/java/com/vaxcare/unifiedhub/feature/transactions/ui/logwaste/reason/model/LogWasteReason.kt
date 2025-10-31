package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model

enum class LogWasteReason(val reasonType: String, val metricText: String) {
    SPOILED_OR_OUT_OF_RANGE(
        reasonType = "Spoiled Dose",
        metricText = "Spoiled or out of range"
    ),
    BROKEN_OR_CONTAMINATED(
        reasonType = "Broken / Contaminated",
        metricText = "Broken or contaminated"
    ),
    PREPPED_AND_NOT_ADMINISTERED(
        reasonType = "Prepped & Not Administered",
        metricText = "Prepped and not administered"
    ),
    EXPIRED(
        reasonType = "Expired - RETURN ONLY",
        metricText = "Expired"
    ),
    DELIVER_OUT_OF_TEMP(
        reasonType = "Delivered Out Of Temp - RETURN ONLY",
        metricText = "Deliver out of temp"
    ),
    OTHER(
        reasonType = "Other",
        metricText = "Other"
    )
}
