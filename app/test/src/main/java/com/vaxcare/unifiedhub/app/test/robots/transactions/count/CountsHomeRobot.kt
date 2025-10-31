package com.vaxcare.unifiedhub.app.test.robots.transactions.count

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performScrollToNode
import com.vaxcare.unifiedhub.app.test.arch.BaseRobot
import com.vaxcare.unifiedhub.app.test.rule.TestServerRule
import com.vaxcare.unifiedhub.core.ui.TestTags

class CountsHomeRobot(
    compose: ComposeTestRule,
    server: TestServerRule
) : BaseRobot<CountsHomeRobot>(compose, server) {
    private val scrollableContainer
        get() = onNodeAndWait(hasTestTag(TestTags.Counts.Home.PRODUCT_SHEET_CONTAINER))

    fun scrollToProduct(productId: Int) =
        apply {
            scrollableContainer.performScrollToNode(
                hasTestTag(TestTags.Counts.Home.productItem(productId))
            )
        }

    fun clickEditOnProduct(productId: Int) =
        apply {
            scrollToProduct(productId)
            waitAndClick(TestTags.Counts.Home.productItemEditButton(productId))
        }

    fun clickConfirmOnProduct(productId: Int) =
        apply {
            scrollToProduct(productId)
            waitAndClick(TestTags.Counts.Home.productItemConfirmButton(productId))
        }

    fun verifyProductIsConfirmed(productId: Int) =
        apply {
            scrollToProduct(productId)
            onNodeAndWait(hasTestTag(TestTags.Counts.Home.productItemConfirmedIcon(productId)))
                .assertIsDisplayed()
        }

    fun verifyProductQuantity(productId: Int, expectedQuantity: String) =
        apply {
            scrollToProduct(productId)
            waitAndAssertTextContains(
                testTag = TestTags.Counts.Home.productItemQuantity(productId),
                expectedText = expectedQuantity
            )
        }
}
