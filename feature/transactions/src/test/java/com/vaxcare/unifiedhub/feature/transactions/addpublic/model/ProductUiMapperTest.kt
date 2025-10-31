package com.vaxcare.unifiedhub.feature.transactions.addpublic.model

import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model.ProductUiMapper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import com.vaxcare.unifiedhub.feature.transactions.addpublic.model.ProductUiMapperTestData as testData

@RunWith(JUnit4::class)
class ProductUiMapperTest {
    private val lotRepository: LotRepository = mockk()
    private val productRepository: ProductRepository = mockk()

    private lateinit var mapper: ProductUiMapper

    @Before
    fun setup() {
        mapper = ProductUiMapper(lotRepository, productRepository)
    }

    @Test
    fun `GIVEN a complex state WHEN sessionToUi() THEN deleted states are excluded`() {
        coEvery { lotRepository.getLotsByNumber(any()) } returns testData.mockLotsExcludingDeleted
        coEvery { productRepository.getProductsByLotNumber(any()) } returns testData.mockProducts

        runTest {
            val actual = mapper.sessionToUi(testData.sessionLotState, testData.sessionProductState)
            coVerify {
                lotRepository.getLotsByNumber(listOf("EFGH", "1234", "5678", "!@#$"))
                productRepository.getProductsByLotNumber(listOf("EFGH", "1234", "5678", "!@#$"))
            }
            assertEquals(testData.expected, actual)
        }
    }
}
