package com.vaxcare.unifiedhub.feature.transactions.addpublic.complete

import app.cash.turbine.test
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.core.ui.arch.TestDispatcherProvider
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.complete.AddPublicCompleteEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.complete.AddPublicCompleteIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.complete.AddPublicCompleteState
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.complete.AddPublicCompleteViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUiMapper
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.session.AddPublicSession
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import com.vaxcare.unifiedhub.feature.transactions.addpublic.AddPublicSharedTestData as sharedTestData
import com.vaxcare.unifiedhub.feature.transactions.addpublic.complete.AddPublicCompleteTestData as testData

@RunWith(JUnit4::class)
class AddPublicCompleteViewModelTest :
    BaseViewModelTest<AddPublicCompleteState, AddPublicCompleteEvent, AddPublicCompleteIntent>() {
    private val session: AddPublicSession = mockk()
    private val dispatcherProvider = TestDispatcherProvider()
    private val productUiMapper: ProductUiMapper = mockk()

    override lateinit var viewModel: AddPublicCompleteViewModel

    @Before
    fun setup() {
        viewModel = AddPublicCompleteViewModel(
            session = session,
            productUiMapper = productUiMapper,
            dispatcherProvider = dispatcherProvider,
        )
    }

    @Test
    fun `GIVEN a complex session state WHEN start() THEN the expected state is emitted`() {
        with(session) {
            coEvery { submittableLotState } returns flowOf(mapOf())
            coEvery { productState } returns MutableStateFlow(mapOf())
            every { stockType } returns StockType.VFC
        }
        coEvery {
            productUiMapper.sessionToUi(any(), any())
        } returns sharedTestData.mockProductsUi

        runTest {
            viewModel.uiState.test {
                viewModel.start()

                assertEquals(
                    testData.Initial.expectedUiState,
                    expectMostRecentItem()
                )
            }
        }
    }

    @Test
    fun `WHEN BackToHome is received THEN it navigates to Home`() {
        runTest {
            viewModel.uiEvent.test {
                viewModel.handleIntent(AddPublicCompleteIntent.BackToHome)
                assertEquals(AddPublicCompleteEvent.NavigateToHome, awaitItem())
            }
        }
    }

    @Test
    fun `WHEN LogOut is received THEN it navigates to Home`() {
        runTest {
            viewModel.uiEvent.test {
                viewModel.handleIntent(AddPublicCompleteIntent.LogOut)
                assertEquals(AddPublicCompleteEvent.NavigateToHome, awaitItem())
            }
        }
    }
}
