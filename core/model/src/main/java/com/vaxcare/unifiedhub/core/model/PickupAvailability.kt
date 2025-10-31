package com.vaxcare.unifiedhub.core.model

import java.time.LocalDate
import java.time.LocalTime

private val SampleData = (9..21).map { day ->
    PickupAvailability(
        date = LocalDate.of(2025, 6, day),
        startTime = LocalTime.of(8, 0),
        endTime = LocalTime.of(16, 0),
    )
}

data class PickupAvailability(
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
) {
    companion object {
        val Sample = SampleData
    }
}
