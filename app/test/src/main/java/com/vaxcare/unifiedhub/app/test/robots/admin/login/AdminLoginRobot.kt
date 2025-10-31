package com.vaxcare.unifiedhub.app.test.robots.admin.login

import androidx.compose.ui.test.junit4.ComposeTestRule
import com.vaxcare.unifiedhub.app.test.arch.BaseRobot
import com.vaxcare.unifiedhub.app.test.robots.admin.details.AdminDetailsRobot
import com.vaxcare.unifiedhub.app.test.rule.TestServerRule
import com.vaxcare.unifiedhub.core.ui.TestTags

class AdminLoginRobot(
    compose: ComposeTestRule,
    server: TestServerRule
) : BaseRobot<AdminLoginRobot>(compose, server) {
    companion object {
        const val DEFAULT_ADMIN_TEST_PASSWORD = "vxc3"
    }

    fun performAdminLogin(password: String = DEFAULT_ADMIN_TEST_PASSWORD): AdminDetailsRobot {
        typePassword(password)
            .clickLogin()

        return AdminDetailsRobot(compose, server)
    }

    fun typePassword(password: String = DEFAULT_ADMIN_TEST_PASSWORD) =
        apply {
            waitAndTypeText(TestTags.AdminLogin.PASSWORD_FIELD, password)
        }

    fun clickLogin() =
        apply {
            waitAndClick(TestTags.AdminLogin.LOGIN_BUTTON)
        }

    // ---------- STUBS ----------
    fun stubSuccessfulLogin(password: String) =
        apply {
            server.get("/api/setup/ValidatePassword?password=$password") {
                server.bodyJson("setup/validatePassword_200.json")
            }
        }
}
