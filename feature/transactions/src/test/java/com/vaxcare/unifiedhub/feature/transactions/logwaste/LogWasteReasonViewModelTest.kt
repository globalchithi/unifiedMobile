package com.vaxcare.unifiedhub.feature.transactions.logwaste

import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.LogWasteReasonDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.LogWasteReasonEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.LogWasteReasonEvent.ReasonConfirmed
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.LogWasteReasonIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.LogWasteReasonState
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.LogWasteReasonViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason.SPOILED_OR_OUT_OF_RANGE
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.session.LogWasteSession
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LogWasteReasonViewModelTest :
    BaseViewModelTest<LogWasteReasonState, LogWasteReasonEvent, LogWasteReasonIntent>() {
    private val analyticsRepository: AnalyticsRepository = mockk(relaxed = true)

    override lateinit var viewModel: LogWasteReasonViewModel

    private val logWasteSession: LogWasteSession = mockk()

    @Before
    fun setUp() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")

        viewModel = LogWasteReasonViewModel(
            analyticsRepository = analyticsRepository,
            dispatcherProvider = testDispatcherProvider,
            transactionSession = logWasteSession,
        )

        coEvery { analyticsRepository.track(any()) } just Runs
        coEvery { logWasteSession.stockType } returns StockType.PRIVATE
    }

    @Test
    fun `GIVEN no reason selected WHEN SelectReason is received with a valid reason THEN selected reason is updated`() =
        whenState {
            skipItems(1) // stockUpdate
            val selectedReason = SPOILED_OR_OUT_OF_RANGE
            viewModel.handleIntent(LogWasteReasonIntent.SelectReason(selectedReason))

            val updatedState = awaitItem()
            assertEquals(selectedReason, updatedState.selectedReason)
        }

    @Test
    fun `GIVEN EXPIRED reason is selected WHEN SelectReason is received with EXPIRED THEN ReturnExpiredProducts is set as active dialog`() =
        whenState {
            skipItems(1) // Stock Updated
            val selectedReason = LogWasteReason.EXPIRED

            viewModel.handleIntent(LogWasteReasonIntent.SelectReason(selectedReason))

            val updatedState = awaitItem()
            assertEquals(updatedState.activeDialog, LogWasteReasonDialog.ReturnExpiredProducts)
        }

    @Test
    fun `GIVEN DELIVER_OUT_OF_TEMP reason is selected WHEN SelectReason is received with DELIVER_OUT_OF_TEMP THEN ReturnProductsDeliveredOutOfTemp is set as active dialog`() =
        whenState {
            skipItems(1) // Stock Updated
            val selectedReason = LogWasteReason.DELIVER_OUT_OF_TEMP

            viewModel.handleIntent(LogWasteReasonIntent.SelectReason(selectedReason))

            val updatedState = awaitItem()
            assertEquals(updatedState.activeDialog, LogWasteReasonDialog.ReturnProductsDeliveredOutOfTemp)
        }

    @Test
    fun `GIVEN no reason selected WHEN ConfirmReason is received THEN no event is received`() =
        whenEvent(
            actions = {
                viewModel.handleIntent(LogWasteReasonIntent.ConfirmReason)
            },
            assertions = {
                expectNoEvents()
            }
        )

    @Test
    fun `GIVEN SPOILED_OR_OUT_OF_RANGE reason is selected WHEN ConfirmReason is received THEN ReasonConfirmed event is sent `() =
        whenEvent(
            actions = {
                with(viewModel) {
                    handleIntent(LogWasteReasonIntent.SelectReason(SPOILED_OR_OUT_OF_RANGE))
                    handleIntent(LogWasteReasonIntent.ConfirmReason)
                }
            },
            assertions = {
                assertEquals(
                    ReasonConfirmed(SPOILED_OR_OUT_OF_RANGE),
                    awaitItem()
                )
            }
        )

    @Test
    fun `WHEN GoBack is received THEN NavigateBack event is sent `() =
        whenEvent(
            actions = {
                with(viewModel) {
                    handleIntent(LogWasteReasonIntent.GoBack)
                }
            },
            assertions = {
                assertEquals(
                    LogWasteReasonEvent.NavigateBack,
                    awaitItem()
                )
            }
        )

    @Test
    fun `GIVEN ReturnExpiredProducts dialog is active WHEN CloseDialog is received THEN active dialog is set to null`() =
        whenState {
            skipItems(1) // Stock Updated

            val selectedReason = LogWasteReason.EXPIRED
            viewModel.handleIntent(LogWasteReasonIntent.SelectReason(selectedReason))
            skipItems(1)

            viewModel.handleIntent(LogWasteReasonIntent.CloseDialog)
            val updatedState = awaitItem()

            assertEquals(updatedState.activeDialog, null)
        }
}
