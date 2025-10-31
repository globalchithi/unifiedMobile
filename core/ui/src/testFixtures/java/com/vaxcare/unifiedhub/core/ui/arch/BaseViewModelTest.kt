@file:OptIn(ExperimentalCoroutinesApi::class)

package com.vaxcare.unifiedhub.core.ui.arch

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule

abstract class BaseViewModelTest<S : UiState, E : UiEvent, I : UiIntent> {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()
    protected val testDispatcherProvider = TestDispatcherProvider()

    protected abstract val viewModel: BaseViewModel<S, E, I>

    // GIVEN
    protected fun TestScope.givenState(builder: S.() -> S) {
        viewModel.setState(builder)
        advanceUntilIdle()
    }

    // WHEN / THEN
    protected fun whenState(block: suspend TurbineTestContext<S>.() -> Unit) =
        runTest {
            viewModel.uiState.test {
                viewModel.start()
                block()
                cancelAndIgnoreRemainingEvents()
            }
        }

    // WHEN / THEN
    protected fun whenEvent(
        actions: suspend TestScope.() -> Unit,
        assertions: suspend TurbineTestContext<E>.() -> Unit
    ) = runTest {
        viewModel.start()

        viewModel.uiEvent.test {
            actions()
            assertions()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // THEN
    protected fun thenStateShouldBe(expected: S) {
        assertEquals(expected, viewModel.currentState())
    }
}
