package com.vaxcare.unifiedhub.feature.transactions.returns.window

import com.vaxcare.unifiedhub.core.data.repository.ReturnRepository
import com.vaxcare.unifiedhub.core.model.PickupAvailability
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.session.ReturnsSession
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.window.ReturnsWindowDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.window.ReturnsWindowEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.window.ReturnsWindowIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.window.ReturnsWindowState
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.window.ReturnsWindowViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate
import java.time.LocalTime

@RunWith(JUnit4::class)
class ReturnsWindowViewModelTest :
    BaseViewModelTest<ReturnsWindowState, ReturnsWindowEvent, ReturnsWindowIntent>() {
    private val session: ReturnsSession = mockk(relaxed = true)
    private val repository: ReturnRepository = mockk(relaxed = true)
    override lateinit var viewModel: ReturnsWindowViewModel

    @Before
    fun setUp() {
        viewModel = ReturnsWindowViewModel(
            session = session,
            repository = repository,
            dispatchers = testDispatcherProvider
        )
    }

    private fun pickups(range: IntRange) =
        range.map {
            PickupAvailability(
                date = LocalDate.of(2025, 9, it),
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(11, 0)
            )
        }

    @Test
    fun `GIVEN repository has pickup dates WHEN start is called THEN first page is loaded and loading is false`() =
        whenState {
            coEvery { repository.getPickupAvailability() } returns pickups(1..5)

            val loaded = awaitItem()
            assertFalse(loaded.loading)
        }

    @Test
    fun `GIVEN repository returns empty availability WHEN start is called THEN NavigateToSummary event is sent`() {
        whenEvent(
            actions = {
                // GIVEN
                coEvery { repository.getPickupAvailability() } returns emptyList()
                viewModel.start()
            },
            assertions = {
                // THEN
                assertEquals(
                    ReturnsWindowEvent.NavigateToSummary(null),
                    awaitItem()
                )
            }
        )
    }

    @Test
    fun `GIVEN repository throws an error WHEN start is called THEN Retry dialog is shown`() {
        // GIVEN
        coEvery { repository.getPickupAvailability() } throws RuntimeException("boom")

        // THEN
        whenState {
            val stateResult = awaitItem()
            assertFalse(stateResult.loading)
            assertEquals(ReturnsWindowDialog.Retry, stateResult.activeDialog)
        }
    }

    @Test
    fun `GIVEN multiple pages of pickups WHEN GetNextAvailablePickUps is handled repeatedly THEN pagination wraps around`() {
        // GIVEN
        coEvery { repository.getPickupAvailability() } returns pickups(1..7)

        whenState {
            var stateResult = awaitItem()
            assertEquals(0, stateResult.pageIndex)

            // WHEN
            viewModel.handleIntent(ReturnsWindowIntent.GetNextAvailablePickUps)
            // THEN
            stateResult = awaitItem()
            assertEquals(1, stateResult.pageIndex)

            // WHEN
            viewModel.handleIntent(ReturnsWindowIntent.GetNextAvailablePickUps)
            // THEN
            stateResult = awaitItem()
            assertEquals(2, stateResult.pageIndex)

            // WHEN
            viewModel.handleIntent(ReturnsWindowIntent.GetNextAvailablePickUps)
            // THEN
            stateResult = awaitItem()
            assertEquals(0, stateResult.pageIndex)
        }
    }

    @Test
    fun `GIVEN available pickups are loaded WHEN a pickup is selected THEN isSelected is true and canConfirm is enabled`() {
        coEvery { repository.getPickupAvailability() } returns pickups(1..3)

        whenState {
            // GIVEN

            var stateResult = awaitItem()
            assertFalse(stateResult.canConfirm)

            // WHEN
            viewModel.handleIntent(ReturnsWindowIntent.SelectPickUp(1))

            // THEN
            stateResult = awaitItem()
            assertTrue(stateResult.selectedIndex == 1)
            assertTrue(stateResult.canConfirm)
        }
    }

    @Test
    fun `GIVEN shipping labels are at minimum WHEN DecrementLabelQuantity is handled THEN quantity is clamped to minimum`() =
        whenState {
            // GIVEN
            coEvery { repository.getPickupAvailability() } returns pickups(1..1)
            awaitItem()

            // WHEN
            viewModel.handleIntent(ReturnsWindowIntent.DecrementLabelQuantity)

            // THEN
            val stateResult = viewModel.currentState()
            assertEquals(
                ReturnsWindowState.MIN_SHIPPING_LABEL_QUANTITY,
                stateResult.shippingLabels
            )
            assertFalse(stateResult.canDecrementShippingLabels)
        }

    @Test
    fun `GIVEN shipping labels are at maximum WHEN IncrementLabelQuantity THEN quantity is clamped to maximum`() {
        // GIVEN
        coEvery { repository.getPickupAvailability() } returns pickups(1..1)

        whenState {
            // WHEN
            repeat(ReturnsWindowState.MAX_SHIPPING_LABEL_QUANTITY) {
                viewModel.handleIntent(ReturnsWindowIntent.IncrementLabelQuantity)
                awaitItem()
            }

            // THEN
            val stateResult = viewModel.currentState()
            assertEquals(
                ReturnsWindowState.MAX_SHIPPING_LABEL_QUANTITY,
                stateResult.shippingLabels
            )
            assertFalse(stateResult.canIncrementShippingLabels)
        }
    }
}
