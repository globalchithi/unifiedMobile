package com.vaxcare.unifiedhub.feature.transactions.counts.submit

import com.vaxcare.unifiedhub.core.data.repository.CountRepository
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.core.ui.arch.MainDispatcherRule
import com.vaxcare.unifiedhub.core.ui.arch.TestDispatcherProvider
import com.vaxcare.unifiedhub.feature.transactions.counts.submit.CountsSubmitTestData.defaultUiStatePrivateStock
import com.vaxcare.unifiedhub.feature.transactions.counts.submit.CountsSubmitTestData.defaultUiStatePublicStock
import com.vaxcare.unifiedhub.feature.transactions.counts.submit.CountsSubmitTestData.inventory
import com.vaxcare.unifiedhub.feature.transactions.counts.submit.CountsSubmitTestData.lotNumbers
import com.vaxcare.unifiedhub.feature.transactions.counts.submit.CountsSubmitTestData.lots
import com.vaxcare.unifiedhub.feature.transactions.counts.submit.CountsSubmitTestData.products
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.session.CountSession
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitState
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitViewModel
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CountsSubmitViewModelTest :
    BaseViewModelTest<CountsSubmitState, CountsSubmitEvent, CountsSubmitIntent>() {
    @get:Rule
    val mainCoroutineRule = MainDispatcherRule()

    override lateinit var viewModel: CountsSubmitViewModel

    private val countSession: CountSession = mockk()
    private val countRepository: CountRepository = mockk()
    private val lotRepository: LotRepository = mockk()
    private val lotInventoryRepository: LotInventoryRepository = mockk()
    private val productRepository: ProductRepository = mockk()
    private val dispatcherProvider = TestDispatcherProvider()
    private val analyticsRepository: AnalyticsRepository = mockk(relaxed = true)

    @Before
    fun setup() {
        with(countSession) {
            every { addedLots } returns MutableStateFlow(setOf(222 to lotNumbers[4])).asStateFlow()
            every { confirmedIds } returns MutableStateFlow(listOf(111, 222)).asStateFlow()
            every { lotState } returns
                MutableStateFlow(CountsSubmitTestData.lotState).asStateFlow()
            every { stockType } returns StockType.VFC
        }
        every { lotInventoryRepository.getLotInventory(StockType.VFC) } returns flowOf(inventory)
        coEvery { analyticsRepository.track(any()) } just Runs
        coEvery { lotRepository.getLotsByNumber(lotNumbers) } returns lots
        coEvery { productRepository.getProductsByLotNumber(lotNumbers) } returns products

        viewModel = CountsSubmitViewModel(
            countSession,
            countRepository,
            lotRepository,
            lotInventoryRepository,
            productRepository,
            dispatcherProvider,
            analyticsRepository
        )
    }

    @Test
    fun `start results in the expected state emission for public stock`() {
        whenState {
            thenStateShouldBe(defaultUiStatePublicStock)
        }
    }

    @Test
    fun `start results in the expected state emission for private stock`() {
        every { countSession.stockType } returns StockType.PRIVATE
        every { lotInventoryRepository.getLotInventory(StockType.PRIVATE) } returns flowOf(inventory)
        whenState {
            thenStateShouldBe(defaultUiStatePrivateStock)
        }
    }
}
