package com.vaxcare.unifiedhub.feature.transactions.returns.complete

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import com.vaxcare.unifiedhub.core.common.ext.toStandardDate
import com.vaxcare.unifiedhub.core.model.inventory.ReturnReason
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.core.ui.arch.MainDispatcherRule
import com.vaxcare.unifiedhub.core.ui.arch.TestDispatcherProvider
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.navigation.ReturnsCompleteRoute
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.complete.ReturnsCompleteEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.complete.ReturnsCompleteIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.complete.ReturnsCompleteState
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.complete.ReturnsCompleteViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.ProductUiMapper
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.session.ReturnsSession
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate
import com.vaxcare.unifiedhub.feature.transactions.returns.ReturnsSharedTestData as sharedTestData

@RunWith(JUnit4::class)
class ReturnsCompleteViewModelTest :
    BaseViewModelTest<ReturnsCompleteState, ReturnsCompleteEvent, ReturnsCompleteIntent>() {
    @get:Rule
    val mainCoroutineRule = MainDispatcherRule()

    private val session: ReturnsSession = mockk {
        every { submittableLotState } returns MutableStateFlow(emptyMap())
        every { pickup } returns sharedTestData.mockPickupAvailability
    }
    private val mapper: ProductUiMapper = mockk()
    private val dispatcherProvider = TestDispatcherProvider()
    private val savedStateHandle: SavedStateHandle = mockk()

    override lateinit var viewModel: ReturnsCompleteViewModel

    @Before
    fun setup() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<ReturnsCompleteRoute>() } returns
            ReturnsCompleteRoute(
                stockType = StockType.PRIVATE,
                reason = ReturnReason.EXPIRED
            )

        viewModel = ReturnsCompleteViewModel(
            session = session,
            productUiMapper = mapper,
            dispatcherProvider = dispatcherProvider,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `GIVEN a session containing state WHEN start() THEN the expected UI state is emitted`() {
        coEvery { mapper.sessionToUi(any()) } returns sharedTestData.mockProductsUi

        val expectedUiState = ReturnsCompleteState(
            stockType = StockUi.PRIVATE,
            reason = ReturnReason.EXPIRED,
            products = sharedTestData.mockProductsUi,
            date = LocalDate.now().toStandardDate(),
            shipmentPickup = "Tue, 11/11 8AM - 4PM",
            totalProducts = "78"
        )

        whenState {
            assertEquals(expectedUiState, awaitItem())
        }
    }

    @Test
    fun `WHEN BackToHome is received THEN it navigates to Home`() {
        runTest {
            viewModel.uiEvent.test {
                viewModel.handleIntent(ReturnsCompleteIntent.BackToHome)
                assertEquals(ReturnsCompleteEvent.NavigateToHome, awaitItem())
            }
        }
    }

    @Test
    fun `WHEN LogOut is received THEN it navigates to Home`() {
        runTest {
            viewModel.uiEvent.test {
                viewModel.handleIntent(ReturnsCompleteIntent.LogOut)
                assertEquals(ReturnsCompleteEvent.NavigateToHome, awaitItem())
            }
        }
    }
}
