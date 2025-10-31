package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.data.mapper.ReturnMapper
import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import com.vaxcare.unifiedhub.core.model.PickupAvailability
import com.vaxcare.unifiedhub.core.model.inventory.Return
import com.vaxcare.unifiedhub.core.model.inventory.ReturnReason
import com.vaxcare.unifiedhub.core.model.inventory.ReturnedLot
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.network.api.InventoryApi
import com.vaxcare.unifiedhub.core.network.api.ReturnApi
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
import java.time.LocalDate
import java.time.LocalTime

private val mockReturnedLots = listOf(
    ReturnedLot(
        lotNumber = "ABCD",
        receiptKey = "EFGH",
        productId = 0,
        count = 4,
        expirationDate = LocalDate.of(2026, 1, 1)
    )
)
private val mockPickup = PickupAvailability(
    date = LocalDate.of(2025, 11, 11),
    startTime = LocalTime.of(8, 0),
    endTime = LocalTime.of(16, 0),
)
private val expectedReturn = Return(
    groupGuid = "5678",
    stockId = 1,
    reasonId = 12,
    pickup = mockPickup,
    noOfLabels = 9,
    returnedLots = mockReturnedLots,
    userId = 99,
    userName = "Wayne"
)

@RunWith(JUnit4::class)
class ReturnRepositoryTest {
    private val returnApi: ReturnApi = mockk()
    private val inventoryApi: InventoryApi = mockk()
    private val returnMapper: ReturnMapper = mockk()
    private val userSessionPrefs: UserSessionPreferenceDataSource = mockk()

    lateinit var repository: ReturnRepositoryImpl

    @Before
    fun setup() {
        repository = ReturnRepositoryImpl(
            returnApi = returnApi,
            inventoryApi = inventoryApi,
            returnMapper = returnMapper,
            userSessionPrefs = userSessionPrefs
        )
    }

    @Test
    fun `GIVEN valid arguments and that the call will succeed WHEN buildAndSubmitReturn() THEN it returns 'true'`() {
        every { userSessionPrefs.userId } returns flowOf(99)
        every { userSessionPrefs.userName } returns flowOf("Wayne")
        every { returnMapper.domainToNetwork(any()) } returns mockk()
        coJustRun { inventoryApi.postReturn(any()) }

        runTest {
            val result = repository.buildAndSubmitReturn(
                groupGuid = "5678",
                stock = StockType.PRIVATE,
                reason = ReturnReason.EXCESS_INVENTORY,
                noOfLabels = 9,
                pickup = mockPickup,
                returnedLots = mockReturnedLots,
            )

            verify {
                returnMapper.domainToNetwork(expectedReturn)
            }

            assertEquals(true, result)
        }
    }

    @Test
    fun `GIVEN the first call will fail and the second will succeed WHEN invoking submitCachedReturn() after buildAndSubmitReturn() THEN it returns 'true'`() {
        every { userSessionPrefs.userId } returns flowOf(99)
        every { userSessionPrefs.userName } returns flowOf("Wayne")
        every { returnMapper.domainToNetwork(any()) } returns mockk()
        coEvery { inventoryApi.postReturn(any()) } throws Exception()

        runTest {
            // make the first call, which caches the adjustment
            val firstResult = repository.buildAndSubmitReturn(
                groupGuid = "5678",
                stock = StockType.PRIVATE,
                reason = ReturnReason.EXCESS_INVENTORY,
                noOfLabels = 9,
                pickup = mockPickup,
                returnedLots = mockReturnedLots,
            )

            // re-mock the call so it doesn't throw
            coJustRun { inventoryApi.postReturn(any()) }

            val secondResult = repository.submitCachedReturn()

            verify(atLeast = 2) {
                returnMapper.domainToNetwork(expectedReturn)
            }

            assertEquals(false, firstResult)
            assertEquals(true, secondResult)
        }
    }
}
