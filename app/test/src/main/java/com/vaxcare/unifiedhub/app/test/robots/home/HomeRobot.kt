package com.vaxcare.unifiedhub.app.test.robots.home

import androidx.compose.ui.test.junit4.ComposeTestRule
import com.vaxcare.unifiedhub.app.test.arch.BaseRobot
import com.vaxcare.unifiedhub.app.test.robots.common.ConfirmStockRobot
import com.vaxcare.unifiedhub.app.test.robots.common.KeyPadRobot
import com.vaxcare.unifiedhub.app.test.robots.transactions.count.CountsHomeRobot
import com.vaxcare.unifiedhub.app.test.rule.TestServerRule
import com.vaxcare.unifiedhub.core.ui.TestTags

class HomeRobot(
    compose: ComposeTestRule,
    server: TestServerRule
) : BaseRobot<HomeRobot>(compose, server) {
    fun verifySyncedClinicIs(clinicName: String) =
        apply {
            waitAndAssertTextContains(TestTags.Home.CLINIC_LABEL, clinicName)
        }

    fun openHamburgerMenu(): HamburgerMenuRobot {
        waitAndClick(TestTags.TopBar.CLOSE_BUTTON)

        return HamburgerMenuRobot(compose, server)
    }

    fun startCountsFlow(): KeyPadRobot<ConfirmStockRobot<CountsHomeRobot>> {
        waitAndClick(TestTags.Home.COUNT_BUTTON)
        return KeyPadRobot(
            nextScreenRobot = ConfirmStockRobot(
                compose = compose,
                server = server,
                nextScreenRobot = CountsHomeRobot(
                    compose = compose,
                    server = server
                )
            ),
            compose = compose,
            server = server
        )
    }
}
