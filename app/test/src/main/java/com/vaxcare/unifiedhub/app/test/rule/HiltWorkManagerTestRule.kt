package com.vaxcare.unifiedhub.app.test.rule

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.vaxcare.unifiedhub.di.HiltWorkerFactoryEntryPoint
import dagger.hilt.android.EntryPointAccessors
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * A JUnit4 Rule that initializes WorkManager for instrumented tests that use Hilt.
 * It correctly sets up the HiltWorkerFactory for dependency injection into Workers
 * and configures a synchronous executor for predictable test execution.
 *
 * This rule should be ordered after HiltAndroidRule to ensure Hilt is set up first.
 *
 * ### Usage:
 * ```kotlin
 * @get:Rule(order = 0)
 * val hiltRule = HiltAndroidRule(this)
 *
 * @get:Rule(order = 1)
 * val workManagerRule = HiltWorkManagerTestRule()
 * ```
 */
class HiltWorkManagerTestRule : TestWatcher() {
    override fun starting(description: Description) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Retrieve the HiltWorkerFactory from the Hilt component graph
        val hiltWorkerFactory = EntryPointAccessors
            .fromApplication(
                context,
                HiltWorkerFactoryEntryPoint::class.java
            ).getHiltWorkerFactory()

        // Create a test-specific configuration for WorkManager
        val config = Configuration
            .Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .setExecutor(SynchronousExecutor())
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

        // Initialize WorkManager for the test environment
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }
}
