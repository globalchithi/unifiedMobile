package com.vaxcare.unifiedhub.feature.transactions.logwaste

import com.vaxcare.unifiedhub.core.common.ext.toUSD
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import com.vaxcare.unifiedhub.core.domain.SafeExpirationDateUseCase
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.core.ui.Icons
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.model.LotState
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.session.LogWasteSession
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.LogWasteSummaryEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.LogWasteSummaryIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.LogWasteSummaryState
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.LogWasteSummaryViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.model.ProductUi
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.UUID

@RunWith(JUnit4::class)
class LogWasteSummaryViewModelTest :
    BaseViewModelTest<LogWasteSummaryState, LogWasteSummaryEvent, LogWasteSummaryIntent>() {
    private val analyticsRepository: AnalyticsRepository = mockk(relaxed = true)
    private val productRepository: ProductRepository = mockk(relaxed = true)
    private val lotRepository: LotRepository = mockk(relaxed = true)
    private val lotInventoryRepository: LotInventoryRepository = mockk(relaxed = true)
    private val userSession: UserSessionPreferenceDataSource = mockk(relaxed = true)
    private val safeExpirationDateUseCase = SafeExpirationDateUseCase()

    override lateinit var viewModel: LogWasteSummaryViewModel

    private val session: LogWasteSession = mockk()
    private val lotStateFlow = MutableStateFlow(
        mapOf(
            "10DOSE" to LotState(10),
            "5DOSE" to LotState(5),
            "1DOSE" to LotState(1),
        )
    )
    private val wasteReasonFlow = MutableStateFlow<LogWasteReason?>(LogWasteReason.PREPPED_AND_NOT_ADMINISTERED)
    private val userIdFlow = MutableStateFlow<Long>(123)
    private val userNameFlow = MutableStateFlow("test!")

    private val lots = listOf(
        Lot(
            lotNumber = "10DOSE",
            productId = 1,
            expiration = null,
            salesProductId = 1
        ),
        Lot(
            lotNumber = "5DOSE",
            productId = 1,
            expiration = null,
            salesProductId = 1
        ),
        Lot(
            lotNumber = "1DOSE",
            productId = 2,
            expiration = null,
            salesProductId = 2
        ),
    )

    private val products = listOf(
        Product(
            id = 1,
            antigen = "RSV",
            displayName = "rsv",
            presentation = Presentation.PREFILLED_SYRINGE,
            categoryId = 1,
            prettyName = "rsvpretty",
            lossFee = 12.34f,
            inventoryGroup = "RSV"
        ),
        Product(
            id = 2,
            antigen = "RSV",
            displayName = "rsv",
            presentation = Presentation.PREFILLED_SYRINGE,
            categoryId = 1,
            prettyName = "rsvpretty",
            lossFee = 12.34f,
            inventoryGroup = "RSV"
        )
    )

    private val expectedState = LogWasteSummaryState(
        reason = LogWasteReason.PREPPED_AND_NOT_ADMINISTERED,
        stock = StockUi.PRIVATE,
        products = listOf(
            ProductUi(
                id = 1,
                antigen = "RSV",
                prettyName = "(rsvpretty)",
                quantity = 15,
                unitPrice = "${12.34f.toUSD()} ea.",
                value = (12.34f * 15).toUSD(),
                lotsPreview = "10DOSE & 1 more",
                valueFloat = 12.34f * 15,
                presentationIcon = Icons.presentationIcon(Presentation.PREFILLED_SYRINGE)
            ),
            ProductUi(
                id = 2,
                antigen = "RSV",
                prettyName = "(rsvpretty)",
                quantity = 1,
                unitPrice = "${12.34f.toUSD()} ea.",
                value = 12.34f.toUSD(),
                lotsPreview = "1DOSE ",
                valueFloat = 12.34f,
                presentationIcon = Icons.presentationIcon(Presentation.PREFILLED_SYRINGE)
            )
        ),
        total = "-$197.44"
    )

    @Before
    fun setUp() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")

        every { session.lotState } returns lotStateFlow
        every { session.wasteReason } returns wasteReasonFlow
        every { session.transactionKey } returns UUID.randomUUID().toString()
        every { session.groupGuid } returns UUID.randomUUID().toString()
        every { userSession.userId } returns userIdFlow
        every { userSession.userName } returns userNameFlow

        viewModel = LogWasteSummaryViewModel(
            analyticsRepository = analyticsRepository,
            productRepository = productRepository,
            dispatcherProvider = testDispatcherProvider,
            lotRepository = lotRepository,
            lotInventoryRepository = lotInventoryRepository,
            userSession = userSession,
            session = session,
            safeExpirationDate = safeExpirationDateUseCase
        )

        coEvery { analyticsRepository.track(any()) } just Runs
        coEvery { session.stockType } returns StockType.PRIVATE
        coEvery { lotRepository.getLotsByNumber(any()) } returns lots
        coEvery { productRepository.getProductsByIds(any()) } returns products
    }

    @Test
    fun `Product quantities and values are positive values while total is negative`() =
        whenState {
            thenStateShouldBe(expectedState)
        }

    @Test
    fun `Test Success`() =
        whenEvent(
            actions = {
                coEvery { lotInventoryRepository.postInventoryAdjustments(any()) } just Runs
                viewModel.handleIntent(LogWasteSummaryIntent.SubmitLogWaste)
            },
            assertions = {
                assert(LogWasteSummaryEvent.NavigateToLogWasteCompleted == awaitItem())
            }
        )

    @Test
    fun `Test Failure`() =
        whenState {
            coEvery { lotInventoryRepository.postInventoryAdjustments(any()) } throws Exception("test")
            skipItems(1) // stockUpdate
            viewModel.handleIntent(LogWasteSummaryIntent.SubmitLogWaste)

            val state = awaitItem()
            assert(state.isLoading)
            val state2 = awaitItem()
            assert(state2.activeDialog != null)
        }
}
