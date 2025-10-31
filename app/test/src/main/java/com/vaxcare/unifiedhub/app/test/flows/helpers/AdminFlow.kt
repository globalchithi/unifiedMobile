package com.vaxcare.unifiedhub.app.test.flows.helpers

import androidx.compose.ui.test.junit4.ComposeTestRule
import com.vaxcare.unifiedhub.app.test.robots.admin.details.AdminDetailsRobot
import com.vaxcare.unifiedhub.app.test.robots.admin.login.AdminLoginRobot
import com.vaxcare.unifiedhub.app.test.rule.TestServerRule

/**
 * A helper object to set up common scenarios for the Admin Login and Sync flow.
 * This centralizes the stubbing logic for reuse across multiple tests.
 */
object AdminFlow {
    /**
     * Stubs all necessary network calls for a complete, successful admin login and clinic sync.
     */
    fun setupSuccessfulAdminSync(
        server: TestServerRule,
        compose: ComposeTestRule,
        password: String,
        partnerId: String,
        clinicId: String
    ) {
        AdminLoginRobot(compose, server).stubSuccessfulLogin(password)

        AdminDetailsRobot(compose, server)
            .stubSuccessfulLocationSync(partnerId, clinicId)
            .stubHubSetup(partnerId)
            .stubInventoryJobs()
    }
}
