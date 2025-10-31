package com.vaxcare.unifiedhub.feature.transactions.returns.reason

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.feature.transactions.navigation.ReturnReasonRoute
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason.ReturnReasonEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason.ReturnReasonIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason.ReturnReasonState
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason.ReturnReasonUi
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason.ReturnsReasonViewModel
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ReturnsReasonViewModelTest :
    BaseViewModelTest<ReturnReasonState, ReturnReasonEvent, ReturnReasonIntent>() {
    private val analyticsRepository: AnalyticsRepository = mockk(relaxed = true)
    private val savedStateHandle = SavedStateHandle()

    override lateinit var viewModel: ReturnsReasonViewModel

    @Before
    fun setUp() {
        coEvery { analyticsRepository.track(any()) } just Runs

        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<ReturnReasonRoute>() } returns ReturnReasonRoute(StockType.PRIVATE)

        viewModel = ReturnsReasonViewModel(
            analyticsRepository = analyticsRepository,
            dispatcherProvider = testDispatcherProvider,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `GIVEN no reason selected WHEN SelectReason is received with a valid reason THEN selected reason is updated`() =
        whenState {
            skipItems(1) // stockUpdate
            val selectedReason = ReturnReasonUi.EXPIRED
            viewModel.handleIntent(ReturnReasonIntent.SelectReason(selectedReason))

            val updatedState = awaitItem()
            Assert.assertEquals(selectedReason, updatedState.selectedReason)
        }

    @Test
    fun `GIVEN no reason selected WHEN ConfirmReason is received THEN no event is received`() =
        whenEvent(
            actions = {
                viewModel.handleIntent(ReturnReasonIntent.ConfirmReason)
            },
            assertions = {
                expectNoEvents()
            }
        )

    @Test
    fun `GIVEN EXCESS_INVENTORY reason is selected WHEN ConfirmReason is received THEN ReasonConfirmed event is sent `() =
        whenEvent(
            actions = {
                with(viewModel) {
                    handleIntent(ReturnReasonIntent.SelectReason(ReturnReasonUi.EXCESS_INVENTORY))
                    handleIntent(ReturnReasonIntent.ConfirmReason)
                }
            },
            assertions = {
                Assert.assertEquals(
                    ReturnReasonEvent.ReasonConfirmed(ReturnReasonUi.EXCESS_INVENTORY),
                    awaitItem()
                )
            }
        )

    @Test
    fun `WHEN GoBack is received THEN NavigateBack event is sent `() =
        whenEvent(
            actions = {
                with(viewModel) {
                    handleIntent(ReturnReasonIntent.GoBack)
                }
            },
            assertions = {
                Assert.assertEquals(
                    ReturnReasonEvent.NavigateBack,
                    awaitItem()
                )
            }
        )
}
