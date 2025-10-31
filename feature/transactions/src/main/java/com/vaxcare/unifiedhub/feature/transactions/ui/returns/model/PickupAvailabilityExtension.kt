package com.vaxcare.unifiedhub.feature.transactions.ui.returns.model

import com.vaxcare.unifiedhub.core.model.PickupAvailability
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, MM/dd", Locale.ENGLISH)
private val TIME_FORMATTER = DateTimeFormatter.ofPattern("ha", Locale.ENGLISH)

internal fun PickupAvailability.getDisplayText(): String {
    val dateFmt = date.format(DATE_FORMATTER)
    val startTimeFmt = startTime.format(TIME_FORMATTER)
    val endTimeFmt = endTime.format(TIME_FORMATTER)

    return "$dateFmt $startTimeFmt - $endTimeFmt"
}
