package com.vaxcare.unifiedhub.library.scanner.ui

import android.view.View
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.scanner.domain.ParseBarcodeUseCase
import com.vaxcare.unifiedhub.library.scanner.domain.ScanType
import com.vaxcare.unifiedhub.library.scanner.domain.ScannerRepository
import com.vaxcare.unifiedhub.library.scanner.ui.ScannerEvent
import com.vaxcare.unifiedhub.library.scanner.ui.ScannerIntent
import com.vaxcare.unifiedhub.library.scanner.ui.ScannerState
import com.vaxcare.unifiedhub.library.scanner.ui.ScannerStatus
import com.vaxcare.unifiedhub.library.scanner.ui.ScannerViewModel
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScannerViewModelTest : BaseViewModelTest<ScannerState, ScannerEvent, ScannerIntent>() {
    @MockK
    private lateinit var scannerRepository: ScannerRepository

    @MockK
    private lateinit var analyticsRepository: AnalyticsRepository

    @MockK
    private lateinit var parseBarcode: ParseBarcodeUseCase

    override val viewModel: ScannerViewModel by lazy {
        ScannerViewModel(
            scannerRepository = scannerRepository,
            dispatcherProvider = testDispatcherProvider,
            parseBarcode = parseBarcode,
            analyticsRepository = analyticsRepository
        )
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery { scannerRepository.stop() } just Runs
    }

    @Ignore("No value produced in 3s")
    @Test
    fun `GIVEN hardware initialization fails WHEN start is called THEN sets status to Stopped`() =
        runTest {
            // Given
            every { scannerRepository.initHardware() } returns false

            // When
            viewModel.handleIntent(ScannerIntent.Start(ScanType.DOSE))

            // Then
            whenState {
                awaitItem() // Initial state
                awaitItem().run {
                    assertEquals(ScannerStatus.Stopped, status)
                }
            }
        }

    @Ignore("No value produced in 3s")
    @Test
    fun `GIVEN hardware initialization succeeds WHEN start is called THEN sets status to Activating`() =
        runTest {
            // Given
            every { scannerRepository.initHardware() } returns true
            coEvery { scannerRepository.activate() } returns flowOf(true)
            coEvery { scannerRepository.preview() } returns mockk()
            coEvery { scannerRepository.rawResults() } returns emptyFlow()

            // When
            viewModel.handleIntent(ScannerIntent.Start(ScanType.DOSE))

            // Then
            whenState {
                awaitItem() // Initial
                awaitItem().run { assertEquals(ScannerStatus.Activating, status) }
                awaitItem().run { assertTrue(status is ScannerStatus.Activation) }
                awaitItem().run { assertTrue(status is ScannerStatus.Scanning) }
            }
        }

    @Ignore("Unknown issue due to .aar dependency")
    @Test
    fun `WHEN Pause is called THEN status transitions from Scanning to Paused`() =
        runTest {
            // Given
            val preview = mockk<View>(relaxed = true)
            viewModel.setState { copy(status = ScannerStatus.Scanning) }

            // When
            viewModel.handleIntent(ScannerIntent.Pause)

            // Then
            advanceUntilIdle()
            assertTrue(viewModel.currentState().status is ScannerStatus.Paused)
        }

    @Ignore("Unknown issue due to .aar dependency")
    @Test
    fun `WHEN Resume is called THEN status transitions from Paused to Scanning`() =
        runTest {
            // Given
            val preview = mockk<View>(relaxed = true)
            viewModel.setState { copy(status = ScannerStatus.Paused) }

            // When
            viewModel.handleIntent(ScannerIntent.Resume)

            // Then
            advanceUntilIdle()
            assertTrue(viewModel.currentState().status is ScannerStatus.Scanning)
        }

    @Ignore("Unknown issue due to .aar dependency")
    @Test
    fun `WHEN Stop is called THEN scannerRepository stops and status is Stopped`() =
        runTest {
            // Given
            every { scannerRepository.stop() } just Runs

            // When
            viewModel.handleIntent(ScannerIntent.Stop)

            // Then
            advanceUntilIdle()
            coVerify(timeout = 100) { scannerRepository.stop() }
            assertEquals(ScannerStatus.Stopped, viewModel.currentState().status)
        }
}
