package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.data.mapper.AdjustmentMapper
import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import com.vaxcare.unifiedhub.core.model.AdjustmentType
import com.vaxcare.unifiedhub.core.model.inventory.Adjustment
import com.vaxcare.unifiedhub.core.model.inventory.AdjustmentEntry
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.network.api.InventoryApi
import com.vaxcare.unifiedhub.core.network.model.AdjustmentEntryRequestDTO
import com.vaxcare.unifiedhub.core.network.model.PostDTO
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AdjustmentRepositoryTest {
    private val inventoryApi: InventoryApi = mockk()
    private val adjustmentMapper: AdjustmentMapper = mockk()
    private val postDtoFactory: PostDtoFactory = mockk()
    private val userSessionPrefs: UserSessionPreferenceDataSource = mockk()

    lateinit var adjustmentRepository: AdjustmentRepository

    @Before
    fun setup() {
        adjustmentRepository = AdjustmentRepository(
            inventoryApi = inventoryApi,
            adjustmentMapper = adjustmentMapper,
            postDtoFactory = postDtoFactory,
            userSessionPrefs = userSessionPrefs
        )
    }

    @Test
    fun `GIVEN valid arguments and that the call will succeed WHEN buildAndSubmitAdjustment() THEN it returns 'true'`() {
        every { userSessionPrefs.userId } returns flowOf(99)
        every { userSessionPrefs.userName } returns flowOf("Wayne")
        every { adjustmentMapper.domainToNetwork(any()) } returns emptyList()
        coEvery { postDtoFactory.createPost(any<List<AdjustmentEntryRequestDTO>>()) } returns
            mockk<PostDTO<List<AdjustmentEntryRequestDTO>>>()
        coJustRun { inventoryApi.postAdjustments(any()) }

        val entries = listOf(
            AdjustmentEntry(
                lotNumber = "ABCD",
                salesProductId = 0,
                delta = 4,
                doseValue = 99F,
                expiration = "01/01/2026",
                receiptKey = "EFGH"
            )
        )
        val expectedAdjustment = Adjustment(
            key = "1234",
            groupGuid = "5678",
            type = AdjustmentType.ADD_PUBLIC,
            stockId = "1",
            adjustments = entries,
            userId = 99,
            userName = "Wayne"
        )

        runTest {
            val result = adjustmentRepository.buildAndSubmitAdjustment(
                key = "1234",
                groupGuid = "5678",
                stock = StockType.PRIVATE,
                type = AdjustmentType.ADD_PUBLIC,
                entries = entries
            )

            verify {
                adjustmentMapper.domainToNetwork(expectedAdjustment)
            }

            assertEquals(true, result)
        }
    }

    @Test
    fun `GIVEN the first call will fail and the second will succeed WHEN invoking submitCachedAdjustment() after buildAndSubmitAdjustment() THEN it returns 'true'`() {
        every { userSessionPrefs.userId } returns flowOf(99)
        every { userSessionPrefs.userName } returns flowOf("Wayne")
        every { adjustmentMapper.domainToNetwork(any()) } returns emptyList()
        coEvery { postDtoFactory.createPost(any<List<AdjustmentEntryRequestDTO>>()) } returns
            mockk<PostDTO<List<AdjustmentEntryRequestDTO>>>()
        coEvery { inventoryApi.postAdjustments(any()) } throws Exception()

        val entries = listOf(
            AdjustmentEntry(
                lotNumber = "ABCD",
                salesProductId = 0,
                delta = 4,
                doseValue = 99F,
                expiration = "01/01/2026",
                receiptKey = "EFGH"
            )
        )
        val expectedAdjustment = Adjustment(
            key = "1234",
            groupGuid = "5678",
            type = AdjustmentType.ADD_PUBLIC,
            stockId = "1",
            adjustments = entries,
            userId = 99,
            userName = "Wayne"
        )

        runTest {
            // make the first call, which caches the adjustment
            val firstResult = adjustmentRepository.buildAndSubmitAdjustment(
                key = "1234",
                groupGuid = "5678",
                stock = StockType.PRIVATE,
                type = AdjustmentType.ADD_PUBLIC,
                entries = entries
            )

            // re-mock the call so it doesn't throw
            coJustRun { inventoryApi.postAdjustments(any()) }

            val secondResult = adjustmentRepository.submitCachedAdjustment()

            verify(atLeast = 2) {
                adjustmentMapper.domainToNetwork(expectedAdjustment)
            }

            assertEquals(false, firstResult)
            assertEquals(true, secondResult)
        }
    }
}
