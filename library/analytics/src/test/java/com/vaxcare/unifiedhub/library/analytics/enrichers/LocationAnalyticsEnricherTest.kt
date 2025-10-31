package com.vaxcare.unifiedhub.library.analytics.enrichers

import com.vaxcare.unifiedhub.core.datastore.datasource.LocationPreferenceDataSource
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsProps
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LocationAnalyticsEnricherTest {
    lateinit var locationAnalyticsEnricher: LocationAnalyticsEnricher
    private val locationPreferenceDataSource: LocationPreferenceDataSource = mockk(relaxUnitFun = true)

    private val partnerId = 1L
    private val clinicId = 1L
    private val expectedProps: AnalyticsProps = mapOf(
        "partnerId" to partnerId.toString(),
        "clinicId" to clinicId.toString()
    )

    @Before
    fun setUp() {
        val ctx = this
        with(locationPreferenceDataSource) {
            every { partnerId } returns flowOf(ctx.partnerId)
            every { clinicId } returns flowOf(ctx.clinicId)
            every { parentClinicId } returns flowOf(ctx.clinicId)
        }

        locationAnalyticsEnricher = LocationAnalyticsEnricher(locationPreferenceDataSource)
    }

    @Test
    fun `Props should match expected values`() =
        runTest {
            assertEquals(expectedProps, locationAnalyticsEnricher.defaultProps())
        }
}
