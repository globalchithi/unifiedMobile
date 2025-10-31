package com.vaxcare.unifiedhub.app.test.robots.home

import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.vaxcare.unifiedhub.app.test.arch.BaseRobot
import com.vaxcare.unifiedhub.app.test.robots.admin.login.AdminLoginRobot
import com.vaxcare.unifiedhub.app.test.rule.TestServerRule
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.feature.home.R

class HamburgerMenuRobot(
    compose: ComposeTestRule,
    server: TestServerRule
) : BaseRobot<HamburgerMenuRobot>(compose, server) {
    init {
        // Assets we are in Home
        onNodeAndWait(hasTestTag(TestTags.Home.CONTAINER))
    }

    fun clickAdminAccess(): AdminLoginRobot {
        val adminAccessText = context.getString((R.string.admin_access))

        val menuScrollableContainer =
            hasTestTag(TestTags.HamburgerMenu.SCROLL_CONTAINER) and hasScrollAction()

        scrollToAndClickText(
            text = adminAccessText,
            containerMatcher = menuScrollableContainer
        )

        return AdminLoginRobot(compose, server)
    }
}
