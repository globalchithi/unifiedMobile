package com.vaxcare.unifiedhub.core.data.functionality

import com.vaxcare.unifiedhub.core.data.adapter.TimeAdapter
import org.junit.Test

/**
 * Test for TimeAdapter flexibility
 */
class TimeAdapterTest {
    private val timeAdapter = TimeAdapter()
    private val timeStringsLocalDateTime = listOf(
        "0000-01-01T01:01:00",
        "2022-05-19T14:32:52.0435909Z",
        "2022-06-03T14:29:36.313",
        "2022-05-19T14:22:13.8462215",
        "2022-05-19T14:32:52.0435909Z",
        "2022-05-19T14:32:53.0591656Z",
        "2022-05-19T14:22:13.1430915",
        "2022-05-19T14:32:52.0435909Z",
        "2022-06-03T14:32:17.0853052",
        "2022-05-19T14:32:17.0853052",
        "2022-05-19T14:32:52.0435909Z",
        "2022-06-03T14:32:15.100859",
        "2022-05-19T14:32:15.1321077",
    )

    private val timeStringsLocalDate = listOf(
        "2025-03-31T00:00:00",
        "1950-12-25T00:00:00"
    )

    @Test
    fun timeAdapterTest() {
        val convertedDateTimeStrings =
            timeStringsLocalDateTime.map { string -> timeAdapter.stringToLocalDateTime(string) }
        assert(convertedDateTimeStrings.none { it == null })
        val convertedDateStrings =
            timeStringsLocalDate.map { string -> timeAdapter.stringToLocalDate(string) }
        assert(convertedDateStrings.none { it == null })
    }
}
