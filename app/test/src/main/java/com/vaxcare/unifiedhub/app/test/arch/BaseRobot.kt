package com.vaxcare.unifiedhub.app.test.arch

import android.content.Context
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.vaxcare.unifiedhub.app.test.rule.TestServerRule

/**
 * Base class for Fluent UI robots.
 *
 * ...
 * class LoginRobot(...) : BaseRobot<LoginRobot>(...) {
 *     fun typeUsername(name: String) = apply { /* ... */ }
 *     fun typePassword(pass: String) = apply { /* ... */ }
 *     fun tapLogin() = apply { /* ... */ }
 * }
 *
 * // Usage:
 * loginRobot.typeUsername("user").typePassword("pass").tapLogin()
 * ```
 */
abstract class BaseRobot<R : BaseRobot<R>>(
    protected val compose: ComposeTestRule,
    protected val server: TestServerRule
) {
    companion object {
        const val DEFAULT_WAIT_TIME: Long = 5_000
    }

    /**
     * Provides easy access to the application's target context for all robot classes.
     * Useful for retrieving string resources, etc.
     */
    protected val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Suppress("UNCHECKED_CAST")
    protected fun self(): R = this as R

    /**
     * Finds a node using the given [matcher], waits until it is displayed,
     * and then returns it for further actions. This avoids repeating waitUntil blocks.
     *
     * @param matcher The matcher to find the node (e.g., onNodeWithTag, onNodeWithText).
     * @param timeoutMillis The maximum time to wait for the node to be displayed.
     * @return A [SemanticsNodeInteraction] for the found node.
     */
    protected fun onNodeAndWait(
        matcher: SemanticsMatcher,
        timeoutMillis: Long = DEFAULT_WAIT_TIME
    ): SemanticsNodeInteraction {
        compose.waitUntil(timeoutMillis) {
            compose.onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty()
        }
        return compose.onNode(matcher).assertIsDisplayed()
    }

    /**
     * Finds a node by its [testTag], waits for it to be displayed, and then performs a click.
     */
    protected fun waitAndClick(testTag: String, timeoutMillis: Long = DEFAULT_WAIT_TIME) {
        onNodeAndWait(hasTestTag(testTag), timeoutMillis).performClick()
    }

    /**
     * Finds a node with the given [text], waits for it to be displayed, and then performs a click.
     */
    protected fun waitAndClickText(text: String, timeoutMillis: Long = DEFAULT_WAIT_TIME) {
        onNodeAndWait(hasText(text), timeoutMillis).performClick()
    }

    /**
     * Finds a scrollable container, scrolls until a node with the
     * specified [text] is visible, and then clicks on it.
     *
     * @param text The text of the node to scroll to and click.
     * @param containerMatcher A matcher to uniquely identify the scrollable container.
     *                         Defaults to finding the first scrollable container.
     * @param ignoreCase Whether the text comparison should be case-insensitive.
     */
    protected fun scrollToAndClickText(
        text: String,
        containerMatcher: SemanticsMatcher = hasScrollAction(),
        ignoreCase: Boolean = false
    ) {
        compose
            .onNode(containerMatcher)
            .performScrollToNode(hasText(text, ignoreCase = ignoreCase))

        compose
            .onNode(hasText(text, ignoreCase = ignoreCase))
            .performClick()
    }

    /**
     * Finds a node by its [testTag], waits for it to be displayed, and then types the given [text].
     */
    protected fun waitAndTypeText(
        testTag: String,
        text: String,
        timeoutMillis: Long = DEFAULT_WAIT_TIME
    ) {
        onNodeAndWait(hasTestTag(testTag), timeoutMillis).performTextInput(text)
    }

    /**
     * Waits for a loading indicator (identified by [testTag]) to first appear
     * and then disappear. This is useful for testing UI that shows a loading animation
     * during a background operation.
     *
     * @param testTag The test tag of the loading indicator composable.
     * @param appearTimeoutMillis The maximum time to wait for the indicator to appear.
     * @param disappearTimeoutMillis The maximum time to wait for the indicator to disappear.
     */
    protected fun waitForLoadingIndicator(
        testTag: String,
        appearTimeoutMillis: Long = DEFAULT_WAIT_TIME,
        disappearTimeoutMillis: Long = 10_000
    ) {
        // This confirms that the loading state has started.
        compose.waitUntil(appearTimeoutMillis) {
            compose
                .onAllNodesWithTag(testTag)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // This confirms that the loading state has finished.
        compose.waitUntil(disappearTimeoutMillis) {
            compose
                .onAllNodesWithTag(testTag)
                .fetchSemanticsNodes()
                .isEmpty()
        }
    }

    /**
     * Finds a node by its [testTag], waits for it to be displayed, and then asserts
     * that its text contains the given [expectedText].
     *
     * @param testTag The test tag of the composable to check.
     * @param expectedText The text to check for (can be a partial match).
     * @param ignoreCase Whether the text comparison should be case-insensitive.
     * @param timeoutMillis The maximum time to wait for the node to appear.
     */
    protected fun waitAndAssertTextContains(
        testTag: String,
        expectedText: String,
        ignoreCase: Boolean = false,
        timeoutMillis: Long = DEFAULT_WAIT_TIME
    ) {
        onNodeAndWait(hasTestTag(testTag), timeoutMillis)
            .assertTextContains(expectedText, ignoreCase = ignoreCase)
    }
}
