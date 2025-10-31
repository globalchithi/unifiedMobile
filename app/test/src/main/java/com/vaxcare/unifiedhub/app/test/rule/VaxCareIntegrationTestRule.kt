package com.vaxcare.unifiedhub.app.test.rule

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.vaxcare.unifiedhub.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * A composite JUnit4 Rule that combines all the common rules needed for a
 * VaxCare feature/integration test. This reduces boilerplate in test classes.
 *
 * It correctly chains:
 * 1. HiltAndroidRule (for DI)
 * 2. DataStoreTestRule (for cleaning DataStore)
 * 3. HiltWorkManagerTestRule (for setting up WorkManager with Hilt)
 * 5. TestServerRule (for mocking network responses)
 * 6. AndroidComposeTestRule (for UI testing)
 *
 * ### Usage:
 * ```kotlin
 * @get:Rule
 * val testRule = VaxCareIntegrationTestRule(this)
 *
 * // Access individual rules via the main rule:
 * val server = testRule.server
 * val compose = testRule.compose
 * ```
 */
class VaxCareIntegrationTestRule(
    // The test class instance is needed for HiltAndroidRule
    testInstance: Any
) : TestRule {
    val hiltRule = HiltAndroidRule(testInstance)
    val dataStoreRule = DataStoreTestRule()
    val workManagerRule = HiltWorkManagerTestRule()
    val server = TestServerRule()
    val compose = createAndroidComposeRule<MainActivity>()

    private val chain: RuleChain = RuleChain
        .outerRule(hiltRule)
        .around(dataStoreRule)
        .around(workManagerRule)
        .around(server)
        .around(compose)

    override fun apply(base: Statement, description: Description): Statement = chain.apply(base, description)
}
