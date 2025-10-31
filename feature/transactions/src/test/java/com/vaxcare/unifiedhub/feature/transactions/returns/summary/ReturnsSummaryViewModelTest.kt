package com.vaxcare.unifiedhub.feature.transactions.returns.summary

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import com.vaxcare.unifiedhub.core.data.repository.AdjustmentRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ReturnRepository
import com.vaxcare.unifiedhub.core.domain.SafeExpirationDateUseCase
import com.vaxcare.unifiedhub.core.model.inventory.ReturnReason
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.core.ui.arch.MainDispatcherRule
import com.vaxcare.unifiedhub.core.ui.arch.TestDispatcherProvider
import com.vaxcare.unifiedhub.feature.transactions.navigation.ReturnsSummaryRoute
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.ProductUiMapper
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.session.ReturnsSession
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary.ReturnsSummaryEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary.ReturnsSummaryIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary.ReturnsSummaryState
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary.ReturnsSummaryViewModel
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate
import com.vaxcare.unifiedhub.feature.transactions.returns.ReturnsSharedTestData as sharedTestData
import com.vaxcare.unifiedhub.feature.transactions.returns.summary.ReturnsSummaryTestData as testData

@RunWith(JUnit4::class)
class ReturnsSummaryViewModelTest :
    BaseViewModelTest<ReturnsSummaryState, ReturnsSummaryEvent, ReturnsSummaryIntent>() {
    @get:Rule
    val mainCoroutineRule = MainDispatcherRule()

    override lateinit var viewModel: ReturnsSummaryViewModel

    private val session: ReturnsSession = mockk {
        every { submittableLotState } returns MutableStateFlow(emptyMap())
        every { transactionKey } returns "1234"
        every { groupGuid } returns "5678"
        every { pickup } returns sharedTestData.mockPickupAvailability
    }
    private val analyticsRepository: AnalyticsRepository = mockk()

    private val adjustmentRepository: AdjustmentRepository = mockk()
    private val returnRepository: ReturnRepository = mockk()
    private val lotRepository: LotRepository = mockk()
    private val productUiMapper: ProductUiMapper = mockk {
        coEvery { sessionToUi(any()) } returns emptyList()
    }
    private val dispatcherProvider = TestDispatcherProvider()
    private val safeExpirationDateUseCase: SafeExpirationDateUseCase = mockk()
    private val savedStateHandle = SavedStateHandle()

    @Before
    fun setup() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<ReturnsSummaryRoute>() } returns
            ReturnsSummaryRoute(
                stockType = StockType.PRIVATE,
                reason = ReturnReason.EXPIRED
            )

        viewModel = ReturnsSummaryViewModel(
            session = session,
            analyticsRepository = analyticsRepository,
            adjustmentRepository = adjustmentRepository,
            returnRepository = returnRepository,
            lotRepository = lotRepository,
            productUiMapper = productUiMapper,
            dispatcherProvider = dispatcherProvider,
            safeExpirationDate = safeExpirationDateUseCase,
            savedStateHandle = savedStateHandle
        )
    }

    @After
    fun cleanup() {
        unmockkStatic("androidx.navigation.SavedStateHandleKt")
    }

    @Test
    fun `GIVEN a complex session state WHEN start() THEN the expected state is emitted`() {
        with(session) {
            coEvery { submittableLotState } returns flowOf(mapOf())
        }
        coEvery {
            productUiMapper.sessionToUi(any())
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
    fun `GIVEN the submission will succeed, Private stock WHEN receiving SubmitReturn THEN a loading state is shown and NavigateToReturnCompleted is sent`() {
        every { session.submittableLotState } returns flowOf(mapOf())
        every { safeExpirationDateUseCase.invoke(any()) } returns LocalDate.of(1970, 1, 1).atStartOfDay()
        coEvery { lotRepository.getLotsByNumber(any()) } returns emptyList()
        coEvery { analyticsRepository.track(any()) } just Runs
        coEvery {
            returnRepository.buildAndSubmitReturn(any(), any(), any(), any(), any(), any())
        } returns true // 'true' simulates a successful submission

        runTest {
            viewModel.uiEvent.test {
                givenState { testData.SubmitSuccess.initialState }
                viewModel.handleIntent(ReturnsSummaryIntent.SubmitReturn)

                coVerify {
                    returnRepository.buildAndSubmitReturn(any(), any(), any(), any(), any(), any())
                    analyticsRepository.track(testData.SubmitSuccess.expectedMetric)
                }
                assertEquals(
                    ReturnsSummaryEvent.NavigateToReturnCompleted,
                    awaitItem()
                )
            }
        }
    }

    @Test
    fun `GIVEN the submission will fail, Private stock WHEN receiving SubmitReturn THEN eventually a loading and error state are shown`() {
        every { session.submittableLotState } returns flowOf(mapOf())
        every { safeExpirationDateUseCase.invoke(any()) } returns LocalDate.of(1970, 1, 1).atStartOfDay()
        coEvery { lotRepository.getLotsByNumber(any()) } returns emptyList()
        coEvery { analyticsRepository.track(any()) } just Runs
        coEvery {
            returnRepository.buildAndSubmitReturn(any(), any(), any(), any(), any(), any())
        } returns false // 'false' simulates a failed submission

        runTest {
            viewModel.uiState.test {
                givenState { testData.SubmitFailure.initialState }
                viewModel.handleIntent(ReturnsSummaryIntent.SubmitReturn)

                coVerify {
                    returnRepository.buildAndSubmitReturn(any(), any(), any(), any(), any(), any())
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
    fun `GIVEN the SubmissionFailed dialog is showing, Private stock WHEN retry is clicked THEN the submission is attempted again`() {
        every { session.submittableLotState } returns flowOf(mapOf())
        every { safeExpirationDateUseCase.invoke(any()) } returns LocalDate.of(1970, 1, 1).atStartOfDay()
        coEvery { lotRepository.getLotsByNumber(any()) } returns emptyList()
        coEvery { analyticsRepository.track(any()) } just Runs
        coEvery {
            returnRepository.submitCachedReturn()
        } returns true // 'true' simulates a successful submission

        runTest {
            viewModel.uiEvent.test {
                givenState { testData.RetrySubmit.initialState }
                viewModel.handleIntent(ReturnsSummaryIntent.RetrySubmission)

                coVerify {
                    returnRepository.submitCachedReturn()
                    analyticsRepository.track(testData.RetrySubmit.expectedMetric)
                }
                assertEquals(
                    ReturnsSummaryEvent.NavigateToReturnCompleted,
                    awaitItem()
                )
            }
        }
    }

    @Test
    fun `GIVEN the submission will succeed, VFC stock WHEN receiving SubmitReturn THEN a loading state is shown and NavigateToReturnCompleted is sent`() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<ReturnsSummaryRoute>() } returns
            ReturnsSummaryRoute(
                stockType = StockType.VFC,
                reason = ReturnReason.EXPIRED
            )

        every { session.submittableLotState } returns flowOf(mapOf())
        every { safeExpirationDateUseCase.invoke(any()) } returns LocalDate.of(1970, 1, 1).atStartOfDay()
        coEvery { lotRepository.getLotsByNumber(any()) } returns emptyList()
        coEvery { analyticsRepository.track(any()) } just Runs
        coEvery {
            adjustmentRepository.buildAndSubmitAdjustment(any(), any(), any(), any(), any(), any())
        } returns true // 'true' simulates a successful submission

        runTest {
            viewModel.uiEvent.test {
                givenState { testData.SubmitSuccess.initialState }
                viewModel.handleIntent(ReturnsSummaryIntent.SubmitReturn)

                coVerify {
                    adjustmentRepository.buildAndSubmitAdjustment(any(), any(), any(), any(), any(), any())
                    analyticsRepository.track(testData.SubmitSuccess.expectedMetric)
                }
                assertEquals(
                    ReturnsSummaryEvent.NavigateToReturnCompleted,
                    awaitItem()
                )
            }
        }
    }

    @Test
    fun `GIVEN the submission will fail, VFC stock WHEN receiving SubmitReturn THEN eventually a loading and error state are shown`() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<ReturnsSummaryRoute>() } returns
            ReturnsSummaryRoute(
                stockType = StockType.VFC,
                reason = ReturnReason.EXPIRED
            )

        every { session.submittableLotState } returns flowOf(mapOf())
        every { safeExpirationDateUseCase.invoke(any()) } returns LocalDate.of(1970, 1, 1).atStartOfDay()
        coEvery { lotRepository.getLotsByNumber(any()) } returns emptyList()
        coEvery { analyticsRepository.track(any()) } just Runs
        coEvery {
            adjustmentRepository.buildAndSubmitAdjustment(any(), any(), any(), any(), any(), any())
        } returns false // 'false' simulates a failed submission

        runTest {
            viewModel.uiState.test {
                givenState { testData.SubmitFailure.initialState }
                viewModel.handleIntent(ReturnsSummaryIntent.SubmitReturn)

                coVerify {
                    adjustmentRepository.buildAndSubmitAdjustment(any(), any(), any(), any(), any(), any())
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
    fun `GIVEN the SubmissionFailed dialog is showing, VFC stock WHEN retry is clicked THEN the submission is attempted again`() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<ReturnsSummaryRoute>() } returns
            ReturnsSummaryRoute(
                stockType = StockType.VFC,
                reason = ReturnReason.EXPIRED
            )

        every { session.submittableLotState } returns flowOf(mapOf())
        every { safeExpirationDateUseCase.invoke(any()) } returns LocalDate.of(1970, 1, 1).atStartOfDay()
        coEvery { lotRepository.getLotsByNumber(any()) } returns emptyList()
        coEvery { analyticsRepository.track(any()) } just Runs
        coEvery {
            adjustmentRepository.submitCachedAdjustment()
        } returns true // 'true' simulates a successful submission

        runTest {
            viewModel.uiEvent.test {
                givenState { testData.RetrySubmit.initialState }
                viewModel.handleIntent(ReturnsSummaryIntent.RetrySubmission)

                coVerify {
                    adjustmentRepository.submitCachedAdjustment()
                    analyticsRepository.track(testData.RetrySubmit.expectedMetric)
                }
                assertEquals(
                    ReturnsSummaryEvent.NavigateToReturnCompleted,
                    awaitItem()
                )
            }
        }
    }

    @Test
    fun `WHEN GoBack is received THEN NavigateBack is sent`() {
        runTest {
            viewModel.uiEvent.test {
                viewModel.handleIntent(ReturnsSummaryIntent.GoBack)
                assertEquals(ReturnsSummaryEvent.NavigateBack, awaitItem())
            }
        }
    }

    @Test
    fun `WHEN DismissDialog is received THEN a null dialog state is emitted`() {
        runTest {
            viewModel.uiState.test {
                givenState { testData.DismissDialog.initialState }
                viewModel.handleIntent(ReturnsSummaryIntent.DismissDialog)

                assertEquals(
                    testData.DismissDialog.expectedState,
                    expectMostRecentItem()
                )
            }
        }
    }
}
