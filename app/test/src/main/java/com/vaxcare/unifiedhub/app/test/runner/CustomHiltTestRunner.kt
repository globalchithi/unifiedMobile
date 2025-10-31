package com.vaxcare.unifiedhub.app.test.runner

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication
import timber.log.Timber

/**
 * A custom TestRunner personalized to configure Hilt in instrumented tests.
 * This runner is in charge of replacing the Application to HiltTestApplication.
 * If moved from package DO NOT forget to update the `testInstrumentationRunner` path inside gradle
 * otherwise tests won't run.
 */
class CustomHiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application =
        super.newApplication(cl, HiltTestApplication::class.java.name, context).also {
            Timber.Forest.plant(Timber.DebugTree())
        }
}
