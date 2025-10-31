package com.vaxcare.unifiedhub.feature.transactions.addpublic.lotinteraction

import androidx.compose.ui.text.AnnotatedString
import app.cash.turbine.test
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.PackageRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.domain.ConvertHtmlUseCase
import com.vaxcare.unifiedhub.core.domain.PostNewLotUseCase
import com.vaxcare.unifiedhub.core.domain.ValidateScannedProductUseCase
import com.vaxcare.unifiedhub.core.domain.model.ScanValidationResult
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.model.lot.LotNumberSource
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.core.ui.arch.MainDispatcherRule
import com.vaxcare.unifiedhub.core.ui.arch.TestDispatcherProvider
import com.vaxcare.unifiedhub.core.ui.ext.toAnnotatedString
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.lotinteraction.AddPublicLotInteractionViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.session.AddPublicSession
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import com.vaxcare.unifiedhub.feature.transactions.addpublic.lotinteraction.AddPublicLotInteractionTestData as TestData
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.lotinteraction.AddPublicLotInteractionDialog as Dialog
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.lotinteraction.AddPublicLotInteractionEvent as Event
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.lotinteraction.AddPublicLotInteractionIntent as Intent
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.lotinteraction.AddPublicLotInteractionState as State

@RunWith(JUnit4::class)
class AddPublicLotInteractionViewModelTest : BaseViewModelTest<State, Event, Intent>() {
    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    val testLotStateFlow = MutableStateFlow(TestData.Init.lotState)
    val testSearchedLotFlow = MutableStateFlow<String?>(null)
    private val session: AddPublicSession = mockk(relaxUnitFun = true) {
        every { stockType } returns StockType.VFC
        every { productId } returns TestData.Init.product.id
        every { lotState } returns testLotStateFlow
        every { searchedLot } returns testSearchedLotFlow
    }
    private val lotRepository: LotRepository = mockk {
        coEvery { getLotsByNumber(any()) } returns TestData.Init.lots
    }
    private val packageRepository: PackageRepository = mockk {
        coEvery { getOneByProductId(any()) } returns TestData.Init.pkg
    }
    private val productRepository: ProductRepository = mockk {
        every { getProduct(any()) } returns flowOf(TestData.Init.product)
    }
    private val dispatcherProvider = TestDispatcherProvider()
    private val validateScannedProduct: ValidateScannedProductUseCase = mockk()
    private val postNewLot: PostNewLotUseCase = mockk(relaxUnitFun = true)
    private val convertHtml: ConvertHtmlUseCase = mockk()

    override lateinit var viewModel: AddPublicLotInteractionViewModel

    @Before
    fun setup() {
        viewModel = AddPublicLotInteractionViewModel(
            session = session,
            lotRepository = lotRepository,
            packageRepository = packageRepository,
            productRepository = productRepository,
            dispatcherProvider = dispatcherProvider,
            validateScannedProduct = validateScannedProduct,
            postNewLot = postNewLot,
            convertHtml = convertHtml
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

    @Test
    fun `GIVEN a state containing no changes WHEN CloseScreen is received THEN NavigateBack is sent`() {
        every { session.containsUncommittedChanges() } returns false
        whenEvent(
            actions = {
                viewModel.handleIntent(Intent.CloseScreen)
            },
            assertions = {
                verify { session.clearSearchedLot() }
                assertEquals(Event.NavigateBack, awaitItem())
            }
        )
    }

    @Test
    fun `GIVEN a state containing changes WHEN CloseScreen is received THEN the DiscardChanges dialog should be shown`() {
        every { session.containsUncommittedChanges() } returns true

        runTest {
            viewModel.uiState.test {
                viewModel.handleIntent(Intent.CloseScreen)
                assertEquals(
                    TestData.Init.uiState.copy(
                        isScannerActive = false,
                        activeDialog = Dialog.DiscardChanges
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
                viewModel.handleIntent(Intent.DiscardChanges)
            },
            assertions = {
                verify {
                    session.resetLotState()
                    session.clearSearchedLot()
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
    fun `GIVEN a typical state WHEN Confirm is received THEN the screen should close`() {
        whenEvent(
            actions = {
                viewModel.handleIntent(Intent.Confirm)
            },
            assertions = {
                verify {
                    session.commitChanges()
                    session.clearSearchedLot()
                }
                assertEquals(Event.NavigateBack, awaitItem())
            }
        )
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
                verify { session.createOrUpdateCount("ANTIGE", 5) }
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
                        lotNumber = "ANTIGE",
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
                viewModel.handleIntent(Intent.DeleteLot("ANTIGE"))
            },
            assertions = {
                verify {
                    session.setDeleted("ANTIGE", true)
                }
            }
        )
    }

    @Test
    fun `GIVEN a lot is deleted WHEN UndoDelete is received for that lot THEN the deleted lot is restored`() {
        whenEvent(
            actions = {
                viewModel.handleIntent(Intent.UndoDelete("ANTIGE"))
            },
            assertions = {
                verify {
                    session.setDeleted("ANTIGE", false)
                }
            }
        )
    }

    @Test
    fun `GIVEN a barcode is scanned WHEN result is Valid THEN the scanned lot is added`() {
        coEvery { validateScannedProduct.invoke(any(), any(), any(), any()) } returns TestData.ScanValidation.valid

        whenEvent(
            actions = {
                viewModel.handleIntent(TestData.ScanValidation.intentToHandle)
            },
            assertions = {
                verify {
                    session.setCount("ABCDEF", 1)
                }
            }
        )
    }

    @Test
    fun `GIVEN a barcode is scanned WHEN result is NewLot THEN the scanned lot is posted and added`() {
        coEvery { validateScannedProduct.invoke(any(), any(), any(), any()) } returns TestData.ScanValidation.newLot

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
    fun `GIVEN a barcode is scanned WHEN result is DuplicateLot THEN the lot is highlighted in list`() {
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
    @Ignore("SpannedString is very resistant to testing.")
    fun `GIVEN a barcode is scanned WHEN result is WrongProduct THEN the WrongProduct dialog is shown`() {
        coEvery { validateScannedProduct.invoke(any(), any(), any(), any()) } returns
            TestData.ScanValidation.wrongProduct
        every { convertHtml.invoke(any()) } returns mockk {
            every { toAnnotatedString() } returns AnnotatedString("no do that")
        }

        runTest {
            givenState {
                TestData.Init.uiState
            }

            viewModel.uiState.test {
                viewModel.handleIntent(TestData.ScanValidation.intentToHandle)
                assertEquals(
                    TestData.ScanValidation.wrongProductExpectedUiState,
                    expectMostRecentItem()
                )
            }
        }
    }

    @Test
    fun `GIVEN a barcode is scanned WHEN result is Expired THEN the ExpiredDose dialog is shown`() {
        coEvery { validateScannedProduct.invoke(any(), any(), any(), any()) } returns ScanValidationResult.Expired("")

        runTest {
            givenState {
                TestData.Init.uiState
            }

            viewModel.uiState.test {
                viewModel.handleIntent(TestData.ScanValidation.intentToHandle)
                assertEquals(
                    TestData.ScanValidation.expiredExpectedUiState,
                    expectMostRecentItem()
                )
            }
        }
    }

    @Test
    fun `GIVEN a barcode is scanned WHEN result is MismatchedProduct THEN the MismatchedProduct dialog is shown`() {
        coEvery { validateScannedProduct.invoke(any(), any(), any(), any()) } returns
            ScanValidationResult.MismatchedProduct

        runTest {
            givenState {
                TestData.Init.uiState
            }

            viewModel.uiState.test {
                viewModel.handleIntent(TestData.ScanValidation.intentToHandle)
                assertEquals(
                    TestData.ScanValidation.mismatchedExpectedUiState,
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
