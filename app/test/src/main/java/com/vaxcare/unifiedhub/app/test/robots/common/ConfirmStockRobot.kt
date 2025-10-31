package com.vaxcare.unifiedhub.app.test.robots.common

import androidx.compose.ui.test.junit4.ComposeTestRule
import com.vaxcare.unifiedhub.app.test.arch.BaseRobot
import com.vaxcare.unifiedhub.app.test.rule.TestServerRule
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.model.StockUi

class ConfirmStockRobot<NextScreenRobot : BaseRobot<*>>(
    private val nextScreenRobot: NextScreenRobot,
    compose: ComposeTestRule,
    server: TestServerRule
) : BaseRobot<ConfirmStockRobot<NextScreenRobot>>(compose, server) {
    fun selectStockAndConfirm(stock: StockUi): NextScreenRobot {
        waitAndClick(TestTags.ConfirmStock.stockButton(stock))
        waitAndClick(TestTags.ConfirmStock.NEXT_BUTTON)
        return nextScreenRobot
    }
}
