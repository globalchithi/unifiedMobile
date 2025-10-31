package com.vaxcare.unifiedhub.library.analytics.enrichers

import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
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
class SessionAnalyticsEnricherTest {
    lateinit var sessionAnalyticsEnricher: SessionAnalyticsEnricher
    private val userSessionPreferenceDataSource: UserSessionPreferenceDataSource = mockk(relaxUnitFun = true)

    private val userId = 1L
    private val sessionId = "session-1"
    private val expectedProps: AnalyticsProps = mapOf(
        "userId" to userId.toString(),
        "sessionId" to sessionId
    )

    @Before
    fun setUp() {
        val ctx = this
        with(userSessionPreferenceDataSource) {
            every { userId } returns flowOf(ctx.userId)
            every { sessionId } returns flowOf(ctx.sessionId)
        }

        sessionAnalyticsEnricher = SessionAnalyticsEnricher(userSessionPreferenceDataSource)
    }

    @Test
    fun `Props should match expected values`() =
        runTest {
            assertEquals(expectedProps, sessionAnalyticsEnricher.defaultProps())
        }
}
