package com.vaxcare.unifiedhub.app.test.flows.adminlogin

import com.vaxcare.unifiedhub.app.test.arch.SystemRobot
import com.vaxcare.unifiedhub.app.test.flows.helpers.AdminFlow
import com.vaxcare.unifiedhub.app.test.robots.home.HomeRobot
import com.vaxcare.unifiedhub.app.test.rule.VaxCareIntegrationTestRule
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AdminLoginHappyPathTests {
    @get:Rule
    val testRule = VaxCareIntegrationTestRule(this)

    private val server get() = testRule.server
    private val compose get() = testRule.compose

    private val adminPassword = "vxc3"
    private val partnerId = "100001"
    private val clinicId = "10808"
    private val clinicName = "Church"

    @Before
    fun setup() {
        AdminFlow.setupSuccessfulAdminSync(server, compose, adminPassword, partnerId, clinicId)
    }

    @Test
    fun `GIVEN right admin credentials WHEN admin logs in THEN hub synced is successfully to a clinic`() {
        HomeRobot(compose, server)
            .openHamburgerMenu()
            .clickAdminAccess()
            .performAdminLogin(adminPassword)
            .enterPartnerAndClinicIds(partnerId, clinicId)
            .verifyPartnerIds(partnerId)
            .verifyClinicIds(clinicId)
            .clickCloseAndReturnHome()
            .verifySyncedClinicIs(clinicName)
            .startCountsFlow()
            .typeSequence("2097")
            .clickConfirm()
            .selectStockAndConfirm(StockUi.PRIVATE)

        SystemRobot().receiveFcmMessage(eventType = "SYNC_LOCATION")
    }
}
