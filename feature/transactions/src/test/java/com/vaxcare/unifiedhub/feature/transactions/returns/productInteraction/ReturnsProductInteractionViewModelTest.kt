package com.vaxcare.unifiedhub.feature.transactions.returns.productInteraction

import android.R.attr.data
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.domain.ConvertHtmlUseCase
import com.vaxcare.unifiedhub.core.domain.PostNewLotUseCase
import com.vaxcare.unifiedhub.core.domain.ValidateScannedProductUseCase
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult
import com.vaxcare.unifiedhub.core.model.inventory.LotInventory
import com.vaxcare.unifiedhub.core.model.inventory.ReturnReason
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.lot.LotNumberSource
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.core.ui.arch.MainDispatcherRule
import com.vaxcare.unifiedhub.core.ui.arch.TestDispatcherProvider
import com.vaxcare.unifiedhub.feature.transactions.navigation.ReturnProductInteractionRoute
import com.vaxcare.unifiedhub.feature.transactions.returns.ReturnsSharedTestData
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.ProductLotUiMapper
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.session.ReturnsSession
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
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
import com.vaxcare.unifiedhub.feature.transactions.returns.productInteraction.ReturnsProductInteractionTestData as TestData
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionEvent as Event
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionIntent as Intent
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionState as State

@RunWith(JUnit4::class)
class ReturnsProductInteractionViewModelTest : BaseViewModelTest<State, Event, Intent>() {
    @get:Rule
    val mainCoroutineRule = MainDispatcherRule()

    val testLotStateFlow = MutableStateFlow<Map<String, ReturnsSession.LotState>>(mapOf())
    private val session: ReturnsSession = mockk(relaxUnitFun = true) {
        every { lotState } returns MutableStateFlow(mapOf())
    }
    private val lotRepository: LotRepository = mockk()
    private val lotInventoryRepository: LotInventoryRepository = mockk()
    private val dispatcherProvider = TestDispatcherProvider()
    private val mapper: ProductLotUiMapper = mockk()
    private val validateScannedProduct: ValidateScannedProductUseCase = mockk()
    private val postNewLot: PostNewLotUseCase = mockk(relaxUnitFun = true)
    private val convertHtml: ConvertHtmlUseCase = mockk()
    private val savedStateHandle = SavedStateHandle()
    private val locationRepository = mockk<LocationRepository>()

    override lateinit var viewModel: ReturnsProductInteractionViewModel

    @Before
    fun setup() {
        coEvery { mapper.sessionToUi(any()) } returns emptyList()

        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<ReturnProductInteractionRoute>() } returns
            ReturnProductInteractionRoute(
                stockType = StockType.PRIVATE,
                reason = ReturnReason.EXCESS_INVENTORY
            )

        coEvery { locationRepository.getFeatureFlagsAsync() } returns emptyList()

        viewModel = ReturnsProductInteractionViewModel(
            session = session,
            lotInventoryRepository = lotInventoryRepository,
            lotRepository = lotRepository,
            dispatcherProvider = dispatcherProvider,
            mapper = mapper,
            validateScannedProduct = validateScannedProduct,
            postNewLot = postNewLot,
            convertHtml = convertHtml,
            savedStateHandle = savedStateHandle,
            locationRepository = locationRepository
        )
    }

    @Test
    fun `GIVEN a typical initial state WHEN start is called THEN the expected state is emitted`() {
        whenState {
            thenStateShouldBe(TestData.Init.uiState)
        }
    }

    @Test
    fun `GIVEN a typical state WHEN lotState emits new state THEN the UI state reflects the update`() {
        runTest {
            viewModel.uiState.test {
                givenState { TestData.Init.uiState }
                testLotStateFlow.tryEmit(
                    value = TestData.LotStateEmission.newLotState
                )

                assertEquals(
                    TestData.LotStateEmission.expectedUiState,
                    expectMostRecentItem()
                )
            }
        }
    }

    @Suppress("UnusedFlow")
    @Test
    fun `GIVEN the selected reason is EXPIRED WHEN start is called THEN expired products are populated alphabetically by antigen`() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<ReturnProductInteractionRoute>() } returns
            ReturnProductInteractionRoute(
                stockType = StockType.PRIVATE,
                reason = ReturnReason.EXPIRED
            )
        coEvery { mapper.sessionToUi(any()) } returns ReturnsSharedTestData.mockProductLotsUi

        // mock DB operations for expired lot population
        val maxExpirationDate = LocalDate.now().plusDays(7)
        val mockExpiredLots: List<Lot> = listOf(
            Lot(
                lotNumber = "VXC02",
                productId = 1,
                expiration = LocalDate.of(2025, 7, 6),
                salesProductId = 1
            ),
            Lot(
                lotNumber = "VXC04",
                productId = 1,
                expiration = LocalDate.of(2025, 7, 13),
                salesProductId = 1
            ),
            Lot(
                lotNumber = "VXC03",
                productId = 2,
                expiration = LocalDate.of(2025, 8, 6),
                salesProductId = 2
            ),
            Lot(
                lotNumber = "VXC01",
                productId = 0,
                expiration = LocalDate.of(2025, 6, 6),
                salesProductId = 0
            ),
        )
        val mockExpiredInventory = listOf(
            LotInventory(
                lotNumber = "VXC04",
                onHand = 10,
                inventorySourceId = 1,
                delta = 0,
                isDeleted = false,
                antigen = "Antigen B",
                productId = 1
            ),
            LotInventory(
                lotNumber = "VXC03",
                onHand = -5,
                inventorySourceId = 1,
                delta = 0,
                isDeleted = false,
                antigen = "Antigen C",
                productId = 2
            ),
            LotInventory(
                lotNumber = "VXC01",
                onHand = 5,
                inventorySourceId = 1,
                delta = 0,
                isDeleted = false,
                antigen = "Antigen A",
                productId = 0
            ),
            LotInventory(
                lotNumber = "VXC02",
                onHand = 15,
                inventorySourceId = 1,
                delta = 0,
                isDeleted = false,
                antigen = "Antigen B",
                productId = 1
            ),
        )
        every { lotRepository.getLotsExpiringBefore(maxExpirationDate) } returns flowOf(
            mockExpiredLots
        )
        every { lotInventoryRepository.getLotInventory(any(), any()) } returns flowOf(
            mockExpiredInventory
        )

        whenState {
            verify {
                lotRepository.getLotsExpiringBefore(maxExpirationDate)
                lotInventoryRepository.getLotInventory(
                    lotNumbers = listOf("VXC02", "VXC04", "VXC03", "VXC01"),
                    stockType = StockType.PRIVATE
                )

                session.populateLotState(
                    data = match { lotState ->
                        val expectedCounts = mapOf("VXC01" to 5, "VXC02" to 15, "VXC04" to 10)
                        lotState.mapValues { it.value.count } == expectedCounts
                    }
                )
            }

            thenStateShouldBe(TestData.InitExpired.expectedUiState)
        }
    }

    @Test
    fun `GIVEN a state containing no changes WHEN CloseScreen is received THEN NavigateBack is sent`() {
        every { session.containsSessionChanges() } returns false
        whenEvent(
            actions = {
                viewModel.handleIntent(Intent.CloseScreen)
            },
            assertions = {
                verify { session.hardReset() }
                assertEquals(Event.NavigateBack, awaitItem())
            }
        )
    }

    @Test
    fun `GIVEN a state containing changes WHEN CloseScreen is received THEN the DiscardChanges dialog should be shown`() {
        every { session.containsSessionChanges() } returns true

        runTest {
            viewModel.uiState.test {
                viewModel.handleIntent(Intent.CloseScreen)
                assertEquals(
                    TestData.Init.uiState.copy(
                        isScannerActive = false,
                        activeDialog = ReturnsProductInteractionDialog.DiscardChanges
                    ),
                    expectMostRecentItem()
                )
            }
        }
    }

    @Test
    fun `GIVEN the DiscardChanges dialog is showing WHEN DiscardChanges is received THEN the screen should close`() {
        whenEvent(
            actions = {
                givenState {
                    TestData.DiscardChanges.initialState
                }
                viewModel.handleIntent(Intent.ConfirmDiscardChanges)
            },
            assertions = {
                verify {
                    session.hardReset()
                }
                assertEquals(Event.NavigateBack, awaitItem())
            }
        )
    }

    @Test
    fun `GIVEN a dialog is showing WHEN DismissDialog is received THEN the active dialog should be hidden`() {
        runTest {
            givenState {
                TestData.DismissDialog.initialState
            }

            viewModel.uiState.test {
                viewModel.handleIntent(Intent.DismissDialog)
                assertEquals(TestData.DismissDialog.expectedUiState, expectMostRecentItem())
            }
        }
    }

    @Test
    fun `GIVEN any state WHEN SearchLot is received THEN Lot Search is opened`() {
        whenEvent(
            actions = {
                viewModel.handleIntent(Intent.SearchLot)
            },
            assertions = {
                assertEquals(TestData.SearchLot.expectedEvent, awaitItem())
            }
        )
    }

    @Test
    fun `GIVEN any state WHEN UpdateLotCount is received THEN the new state reflects the update`() {
        whenEvent(
            actions = {
                viewModel.handleIntent(TestData.UpdateLotCount.intentToHandle)
            },
            assertions = {
                verify { session.adjustCount("VXC01", 5) }
            }
        )
    }

    @Test
    fun `GIVEN a typical state WHEN OpenKeypad is received THEN the keypad should show`() {
        runTest {
            givenState {
                TestData.Init.uiState
            }

            viewModel.uiState.test {
                viewModel.handleIntent(TestData.OpenKeypad.intentToHandle)
                assertEquals(
                    TestData.OpenKeypad.expectedUiState,
                    expectMostRecentItem()
                )
            }
        }
    }

    @Test
    fun `GIVEN the keypad is showing WHEN SubmitKeypadInput is received THEN the new state reflects the update`() {
        runTest {
            givenState {
                TestData.SubmitKeypadInput.initialUiState
            }

            viewModel.uiState.test {
                viewModel.handleIntent(TestData.SubmitKeypadInput.intentToHandle)

                verify {
                    session.setCount(
                        lotNumber = "VXC01",
                        count = 99
                    )
                }
                assertEquals(
                    TestData.SubmitKeypadInput.expectedUiState,
                    expectMostRecentItem()
                )
            }
        }
    }

    @Test
    fun `GIVEN a typical state WHEN DeleteLot is received THEN the target lot is deleted`() {
        whenEvent(
            actions = {
                viewModel.handleIntent(Intent.DeleteLot("VXC01"))
            },
            assertions = {
                verify {
                    session.setDeleted("VXC01", true)
                }
            }
        )
    }

    @Test
    fun `GIVEN a lot is deleted WHEN UndoDelete is received for that lot THEN the deleted lot is restored`() {
        whenEvent(
            actions = {
                viewModel.handleIntent(Intent.UndoDelete("VXC01"))
            },
            assertions = {
                verify {
                    session.setDeleted("VXC01", false)
                }
            }
        )
    }

    @Test
    fun `GIVEN a barcode is scanned WHEN result is Expired OR Valid THEN the scanned lot is added`() {
        // valid

        coEvery {
            validateScannedProduct.invoke(any(), any(), any(), any())
        } returns TestData.ScanValidation.valid

        whenEvent(
            actions = {
                viewModel.handleIntent(TestData.ScanValidation.intentToHandle)
            },
            assertions = {
                verify {
                    session.setCount("VXC01", 1)
                }
            }
        )

        // expired

        coEvery {
            validateScannedProduct.invoke(any(), any(), any(), any())
        } returns TestData.ScanValidation.expired

        whenEvent(
            actions = {
                viewModel.handleIntent(TestData.ScanValidation.intentToHandle)
            },
            assertions = {
                verify {
                    session.setCount("VXC01", 1)
                }
            }
        )
    }

    @Test
    fun `GIVEN a barcode is scanned WHEN result is NewLot THEN the scanned lot is posted and added`() {
        coEvery {
            validateScannedProduct.invoke(
                any(),
                any(),
                any(),
                any()
            )
        } returns TestData.ScanValidation.newLot

        whenEvent(
            actions = {
                viewModel.handleIntent(TestData.ScanValidation.intentToHandle)
            },
            assertions = {
                coVerify {
                    with(TestData.ScanValidation.newLot) {
                        postNewLot(lotNumber, productId, expiration, LotNumberSource.VaxHubScan)
                        session.setCount(lotNumber, 1)
                    }
                }
            }
        )
    }

    @Test
    fun `GIVEN a barcode is scanned WHEN result is DuplicateLot THEN no lot is added or dialog displayed`() {
        coEvery { validateScannedProduct.invoke(any(), any(), any(), any()) } returns
            TestData.ScanValidation.duplicateLot

        runTest {
            givenState {
                TestData.Init.uiState
            }

            viewModel.uiState.test {
                viewModel.handleIntent(TestData.ScanValidation.intentToHandle)
                assertEquals(
                    TestData.ScanValidation.duplicateLotExpectedUiState,
                    expectMostRecentItem()
                )
            }
        }
    }

    @Test
    fun `GIVEN a barcode is scanned WHEN result is InvalidBarcode THEN the BadBarcodeScan error is shown`() {
        coEvery { validateScannedProduct.invoke(any(), any(), any(), any()) } returns
            ScanValidationResult.InvalidBarcode

        runTest {
            givenState {
                TestData.Init.uiState
            }

            viewModel.uiState.test {
                viewModel.handleIntent(TestData.ScanValidation.intentToHandle)
                assertEquals(
                    TestData.ScanValidation.invalidExpectedUiState,
                    expectMostRecentItem()
                )
            }
        }
    }
}
