package com.vaxcare.unifiedhub.feature.transactions.addpublic.summary

import app.cash.turbine.test
import com.vaxcare.unifiedhub.core.data.repository.AdjustmentRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.domain.SafeExpirationDateUseCase
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.core.ui.arch.MainDispatcherRule
import com.vaxcare.unifiedhub.core.ui.arch.TestDispatcherProvider
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUiMapper
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.session.AddPublicSession
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.summary.AddPublicSummaryEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.summary.AddPublicSummaryIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.summary.AddPublicSummaryState
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.summary.AddPublicSummaryViewModel
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate
import com.vaxcare.unifiedhub.feature.transactions.addpublic.AddPublicSharedTestData as sharedTestData
import com.vaxcare.unifiedhub.feature.transactions.addpublic.summary.AddPublicSummaryTestData as testData

@RunWith(JUnit4::class)
class AddPublicSummaryViewModelTest :
    BaseViewModelTest<AddPublicSummaryState, AddPublicSummaryEvent, AddPublicSummaryIntent>() {
    @get:Rule
    val mainCoroutineRule = MainDispatcherRule()

    override lateinit var viewModel: AddPublicSummaryViewModel

    private val session: AddPublicSession = mockk {
        every { productState } returns MutableStateFlow(emptyMap())
        every { submittableLotState } returns MutableStateFlow(emptyMap())
        every { stockType } returns StockType.VFC
        every { transactionKey } returns "1234"
        every { groupGuid } returns "5678"
    }
    private val analyticsRepository: AnalyticsRepository = mockk()
    private val adjustmentRepository: AdjustmentRepository = mockk()
    private val lotRepository: LotRepository = mockk()
    private val productUiMapper: ProductUiMapper = mockk {
        coEvery { sessionToUi(any(), any()) } returns emptyList()
    }
    private val dispatcherProvider = TestDispatcherProvider()
    private val safeExpirationDateUseCase: SafeExpirationDateUseCase = mockk()

    @Before
    fun setup() {
        viewModel = AddPublicSummaryViewModel(
            session = session,
            analyticsRepository = analyticsRepository,
            adjustmentRepository = adjustmentRepository,
            lotRepository = lotRepository,
            productUiMapper = productUiMapper,
            dispatcherProvider = dispatcherProvider,
            safeExpirationDate = safeExpirationDateUseCase
        )
    }

    @Test
    fun `GIVEN a complex session state WHEN start() THEN the expected state is emitted`() {
        with(session) {
            coEvery { submittableLotState } returns flowOf(mapOf())
            coEvery { productState } returns MutableStateFlow(mapOf())
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
    fun `GIVEN the submission will succeed WHEN receiving SubmitAddPublic THEN a loading state is shown and NavigateToAddPublicCompleted is sent`() {
        every { session.submittableLotState } returns flowOf(mapOf())
        every { safeExpirationDateUseCase.invoke(any()) } returns LocalDate.of(1970, 1, 1).atStartOfDay()
        coEvery { lotRepository.getLotsByNumber(any()) } returns emptyList()
        coEvery { analyticsRepository.track(any()) } just Runs
        coEvery {
            adjustmentRepository.buildAndSubmitAdjustment(
                key = any(),
                groupGuid = any(),
                stock = any(),
                type = any(),
                entries = any()
            )
        } returns true // 'true' simulates a successful submission

        runTest {
            viewModel.uiEvent.test {
                givenState { testData.SubmitSuccess.initialState }
                viewModel.handleIntent(AddPublicSummaryIntent.SubmitAddPublic)

                coVerify {
                    adjustmentRepository.buildAndSubmitAdjustment(
                        key = any(),
                        groupGuid = any(),
                        stock = any(),
                        type = any(),
                        entries = any()
                    )
                    analyticsRepository.track(testData.SubmitSuccess.expectedMetric)
                }
                assertEquals(
                    AddPublicSummaryEvent.NavigateToAddPublicCompleted,
                    awaitItem()
                )
            }
        }
    }

    @Test
    fun `GIVEN the submission will fail WHEN receiving SubmitAddPublic THEN eventually a loading and error state are shown`() {
        every { session.submittableLotState } returns flowOf(mapOf())
        every { safeExpirationDateUseCase.invoke(any()) } returns LocalDate.of(1970, 1, 1).atStartOfDay()
        coEvery { lotRepository.getLotsByNumber(any()) } returns emptyList()
        coEvery { analyticsRepository.track(any()) } just Runs
        coEvery {
            adjustmentRepository.buildAndSubmitAdjustment(
                key = any(),
                groupGuid = any(),
                stock = any(),
                type = any(),
                entries = any()
            )
        } returns false // 'false' simulates a failed submission

        runTest {
            viewModel.uiState.test {
                givenState { testData.SubmitFailure.initialState }
                viewModel.handleIntent(AddPublicSummaryIntent.SubmitAddPublic)

                coVerify {
                    adjustmentRepository.buildAndSubmitAdjustment(
                        key = any(),
                        groupGuid = any(),
                        stock = any(),
                        type = any(),
                        entries = any()
                    )
                    analyticsRepository.track(testData.SubmitFailure.expectedMetric)
                }
                assertEquals(
                    testData.SubmitFailure.expectedState,
                    expectMostRecentItem()
                )
            }
        }
    }

    @Test
    fun `WHEN GoBack is received THEN NavigateBack is sent`() {
        runTest {
            viewModel.uiEvent.test {
                viewModel.handleIntent(AddPublicSummaryIntent.GoBack)
                assertEquals(AddPublicSummaryEvent.NavigateBack, awaitItem())
            }
        }
    }

    @Test
    fun `WHEN DismissDialog is received THEN a null dialog state is emitted`() {
        runTest {
            viewModel.uiState.test {
                givenState { testData.DismissDialog.initialState }
                viewModel.handleIntent(AddPublicSummaryIntent.DismissDialog)

                assertEquals(
                    testData.DismissDialog.expectedState,
                    expectMostRecentItem()
                )
            }
        }
    }
}
