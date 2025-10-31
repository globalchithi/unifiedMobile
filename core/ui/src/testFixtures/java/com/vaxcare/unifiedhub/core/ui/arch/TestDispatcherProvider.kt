package com.vaxcare.unifiedhub.core.ui.arch

import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class TestDispatcherProvider @OptIn(ExperimentalCoroutinesApi::class) constructor(
    testCoroutineDispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()
) : DispatcherProvider {
    override val main: CoroutineDispatcher = testCoroutineDispatcher
    override val io: CoroutineDispatcher = testCoroutineDispatcher
    override val default: CoroutineDispatcher = testCoroutineDispatcher
}
