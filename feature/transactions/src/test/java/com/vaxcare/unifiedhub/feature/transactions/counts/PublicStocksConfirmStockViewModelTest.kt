@file:OptIn(ExperimentalCoroutinesApi::class)
package com.vaxcare.unifiedhub.feature.transactions.counts

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.model.inventory.StockType.PRIVATE
import com.vaxcare.unifiedhub.core.model.inventory.StockType.STATE
import com.vaxcare.unifiedhub.core.model.inventory.StockType.THREE_SEVENTEEN
import com.vaxcare.unifiedhub.core.model.inventory.StockType.VFC
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.transactions.ui.common.confirmstock.ConfirmStockEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.common.confirmstock.ConfirmStockIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.common.confirmstock.ConfirmStockState
import com.vaxcare.unifiedhub.feature.transactions.ui.common.confirmstock.ConfirmStockViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.common.confirmstock.navigation.ConfirmStockRoute
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

private const val TITLE_RES = 101
private const val SUBTITLE_RES = 202
private val PRESELECT_STOCK = VFC
private const val PUBLIC_STOCKS_ONLY = true

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class PublicStocksConfirmStockViewModelTest :
    BaseViewModelTest<ConfirmStockState, ConfirmStockEvent, ConfirmStockIntent>() {
    private val locationRepo: LocationRepository = mockk(relaxUnitFun = true)
    private val analyticsRepo: AnalyticsRepository = mockk(relaxUnitFun = true)
    private val savedStateHandle = SavedStateHandle()

    override lateinit var viewModel: ConfirmStockViewModel

    @Before
    fun setUp() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<ConfirmStockRoute>() } returns
            ConfirmStockRoute(
                title = TITLE_RES,
                subtitle = SUBTITLE_RES,
                preselectedStock = PRESELECT_STOCK,
                publicStocksOnly = PUBLIC_STOCKS_ONLY
            )

        every { locationRepo.getStockTypes() } returns
            flowOf(listOf(PRIVATE, VFC, STATE, THREE_SEVENTEEN))

        coEvery { analyticsRepo.track(any()) } just Runs

        viewModel = ConfirmStockViewModel(
            locationRepository = locationRepo,
            analyticsRepository = analyticsRepo,
            dispatchers = testDispatcherProvider,
            savedStateHandle = savedStateHandle
        )
    }

    @Test
    fun `WHEN publicStocksOnly IS true THEN PRIVATE is not displayed`() =
        whenState {
            val updated = awaitItem()
            val expected = listOf(StockUi.VFC, StockUi.STATE, StockUi.THREE_SEVENTEEN)
            val actual = updated.stocks

            assertEquals(expected.size, actual.size)
            assertTrue(expected.containsAll(actual))
            assertTrue(actual.containsAll(expected))
        }
}
