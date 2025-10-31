package com.vaxcare.unifiedhub.app.test.rule

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File

class DataStoreTestRule : TestWatcher() {
    private val testContext: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    override fun finished(description: Description) {
        super.finished(description)
        runBlocking(Dispatchers.IO) {
            clearAllTestDataStores()
        }
    }

    private fun clearAllTestDataStores() {
        val datastoreDir = File(testContext.filesDir, "datastore")
        if (datastoreDir.exists()) {
            datastoreDir
                .listFiles { _, name -> name.startsWith("TEST_") }
                ?.forEach { it.delete() }
        }
    }
}
