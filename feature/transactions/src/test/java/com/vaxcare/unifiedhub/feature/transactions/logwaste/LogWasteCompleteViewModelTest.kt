package com.vaxcare.unifiedhub.feature.transactions.logwaste

import app.cash.turbine.test
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.core.ui.arch.TestDispatcherProvider
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.complete.LogWasteCompleteEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.complete.LogWasteCompleteIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.complete.LogWasteCompleteState
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.complete.LogWasteCompleteViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.session.LogWasteSession
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import com.vaxcare.unifiedhub.feature.transactions.logwaste.LogWasteCompleteTestData as testData

@RunWith(JUnit4::class)
class LogWasteCompleteViewModelTest :
    BaseViewModelTest<LogWasteCompleteState, LogWasteCompleteEvent, LogWasteCompleteIntent>() {
    private val dispatcherProvider = TestDispatcherProvider()
    private val lotRepository: LotRepository = mockk()
    private val productRepository: ProductRepository = mockk()
    private val session: LogWasteSession = mockk()

    override lateinit var viewModel: LogWasteCompleteViewModel

    @Before
    fun setup() {
        viewModel = LogWasteCompleteViewModel(
            dispatcherProvider = dispatcherProvider,
            lotRepository = lotRepository,
            productRepository = productRepository,
            session = session
        )
    }

    @Test
    fun `GIVEN a session state in 'Private' stock WHEN start() THEN the expected state is emitted`() {
        every { session.lotState } returns MutableStateFlow(testData.sessionLotState)
        every { session.stockType } returns StockType.PRIVATE
        coEvery { lotRepository.getLotsByNumber(any()) } returns testData.mockLots
        every { productRepository.getProductsByLotNumber(any()) } returns testData.mockProducts

        runTest {
            viewModel.uiState.test {
                viewModel.start()

                assertEquals(
                    testData.InitialPrivate.expectedUiState,
                    expectMostRecentItem()
                )
            }
        }
    }

    @Test
    fun `GIVEN a session state in 'VFC' stock WHEN start() THEN the expected state is emitted`() {
        every { session.lotState } returns MutableStateFlow(testData.sessionLotState)
        every { session.stockType } returns StockType.VFC
        coEvery { lotRepository.getLotsByNumber(any()) } returns testData.mockLots
        every { productRepository.getProductsByLotNumber(any()) } returns testData.mockProducts

        runTest {
            viewModel.uiState.test {
                viewModel.start()

                assertEquals(
                    testData.InitialVfc.expectedUiState,
                    expectMostRecentItem()
                )
            }
        }
    }

    @Test
    fun `WHEN BackToHome is received THEN it navigates to Home`() {
        runTest {
            viewModel.uiEvent.test {
                viewModel.handleIntent(LogWasteCompleteIntent.BackToHome)
                assertEquals(LogWasteCompleteEvent.NavigateToHome, awaitItem())
            }
        }
    }

    @Test
    fun `WHEN LogOut is received THEN it navigates to Home`() {
        runTest {
            viewModel.uiEvent.test {
                viewModel.handleIntent(LogWasteCompleteIntent.LogOut)
                assertEquals(LogWasteCompleteEvent.NavigateToHome, awaitItem())
            }
        }
    }
}
