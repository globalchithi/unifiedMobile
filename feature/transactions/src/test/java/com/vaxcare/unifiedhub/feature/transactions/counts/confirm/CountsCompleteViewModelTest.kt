package com.vaxcare.unifiedhub.feature.transactions.counts.confirm

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.domain.ClearUserSessionUseCase
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.core.ui.arch.MainDispatcherRule
import com.vaxcare.unifiedhub.core.ui.arch.TestDispatcherProvider
import com.vaxcare.unifiedhub.feature.transactions.navigation.CountsCompleteRoute
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.complete.CountsCompleteEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.complete.CountsCompleteIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.complete.CountsCompleteState
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.complete.CountsCompleteViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class CountsCompleteViewModelTest :
    BaseViewModelTest<CountsCompleteState, CountsCompleteEvent, CountsCompleteIntent>() {
    @get:Rule
    val mainCoroutineRule = MainDispatcherRule()

    override lateinit var viewModel: CountsCompleteViewModel

    private val clearUserSessionUseCase: ClearUserSessionUseCase = mockk()
    private val lotInventoryRepository: LotInventoryRepository = mockk()
    private val dispatcherProvider = TestDispatcherProvider()
    private val savedStateHandle = SavedStateHandle()

    @Before
    fun setup() {
        every { lotInventoryRepository.getLotInventoryTotalValue(StockType.VFC) } returns 10000F
        every { lotInventoryRepository.getLotInventoryTotalValue(StockType.PRIVATE) } returns 5000F

        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<CountsCompleteRoute>() } returns
            CountsCompleteTestData.NoVariance.navRoute

        viewModel = CountsCompleteViewModel(
            clearUserSessionUseCase,
            dispatcherProvider,
            lotInventoryRepository,
            savedStateHandle
        )
    }

    @After
    fun cleanup() {
        unmockkStatic("androidx.navigation.SavedStateHandleKt")
    }

    @Test
    fun `GIVEN args with no variance WHEN start is called THEN the expected state is emitted`() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<CountsCompleteRoute>() } returns
            CountsCompleteTestData.NoVariance.navRoute

        whenState {
            thenStateShouldBe(
                CountsCompleteTestData.NoVariance.expectedUiState
            )
        }
    }

    @Test
    fun `GIVEN args with positive total impact WHEN start is called THEN the expected state is emitted`() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<CountsCompleteRoute>() } returns
            CountsCompleteTestData.VarianceWithNegativeImpact.navRoute

        whenState {
            thenStateShouldBe(
                CountsCompleteTestData.VarianceWithNegativeImpact.expectedUiState
            )
        }
    }

    @Test
    fun `GIVEN args with negative total impact WHEN start is called THEN the expected state is emitted`() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<CountsCompleteRoute>() } returns
            CountsCompleteTestData.VarianceWithPositiveImpact.navRoute

        whenState {
            thenStateShouldBe(
                CountsCompleteTestData.VarianceWithPositiveImpact.expectedUiState
            )
        }
    }

    @Test
    fun `GIVEN args with variance but no impact WHEN start is called THEN the expected state is emitted`() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<CountsCompleteRoute>() } returns
            CountsCompleteTestData.VarianceWithNoImpact.navRoute

        whenState {
            thenStateShouldBe(
                CountsCompleteTestData.VarianceWithNoImpact.expectedUiState
            )
        }
    }

    @Test
    fun `WHEN handling LogOut intent`() {
        coEvery { clearUserSessionUseCase.invoke() } just runs

        whenEvent(
            actions = {
                viewModel.handleIntent(CountsCompleteIntent.LogOut)
                advanceUntilIdle()
            },
            assertions = {
                coVerify { clearUserSessionUseCase.invoke() }
                assertEquals(CountsCompleteEvent.NavigateToHome, awaitItem())
            }
        )
    }

    @Test
    fun `WHEN handling BackToHome intent`() {
        whenEvent(
            actions = { viewModel.handleIntent(CountsCompleteIntent.BackToHome) },
            assertions = {
                assertEquals(CountsCompleteEvent.NavigateToHome, awaitItem())
            }
        )
    }
}
