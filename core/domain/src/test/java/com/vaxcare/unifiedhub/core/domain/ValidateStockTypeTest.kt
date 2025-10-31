package com.vaxcare.unifiedhub.core.domain

import com.vaxcare.unifiedhub.core.model.inventory.StockType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidateStockTypeTest {
    private lateinit var useCase: ValidateStockType

    @Before
    fun setup() {
        useCase = ValidateStockType()
    }

    @Test
    fun `GIVEN a StockType WHEN it exists IN availableStocks THEN returns StockType`() =
        runTest {
            val result = useCase(
                stockType = StockType.VFC,
                availableStocks = listOf(StockType.PRIVATE, StockType.VFC)
            )
            assertTrue("Result should be VFC", result == StockType.VFC)
        }

    @Test
    fun `GIVEN a StockType WHEN it does not exist IN availableStocks THEN returns PRIVATE`() =
        runTest {
            val result = useCase(
                stockType = StockType.THREE_SEVENTEEN,
                availableStocks = listOf(StockType.PRIVATE, StockType.VFC)
            )
            assertTrue("Result should be PRIVATE", result == StockType.PRIVATE)
        }
}
