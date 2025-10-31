package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.session

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.feature.transactions.navigation.AddPublicSectionRoute
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AddPublicSessionTest {
    val lotNumber = "testLot"

    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var session: AddPublicSession

    @Before
    fun setup() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        savedStateHandle = mockk(relaxed = true) {
            every { toRoute<AddPublicSectionRoute>() } returns AddPublicSectionRoute(
                stockType = StockType.VFC,
                shouldConfirmStock = true
            )
        }
        session = AddPublicSession(savedStateHandle)
    }

    @Test
    fun `GIVEN a lot number not in lotState WHEN setCount() with count = 10 THEN lot state is created with count = 10`() {
        testSetCount(count = 10, expected = 10)
    }

    @Test
    fun `GIVEN a lot number not in lotState WHEN setCount() with count = 0 THEN lot state is created with count = 1`() {
        testSetCount(count = 0, expected = 1)
    }

    @Test
    fun `GIVEN a lot number already in lotState WHEN setCount() with count = 10 THEN lot state count = 10`() {
        testSetCount(newCount = 10, expected = 10, initialCount = 1)
    }

    @Test
    fun `GIVEN a lot number already in lotState WHEN setCount() with count = 0 THEN lot state count = 1`() {
        testSetCount(newCount = 0, expected = 1, initialCount = 10)
    }

    @Test
    fun `GIVEN a lot number not in lotState WHEN createOrUpdateCount() with change = 10 THEN lot state is created with count = 10`() {
        testCreateOrUpdate(change = 10, expected = 10)
    }

    @Test
    fun `GIVEN a lot number not in lotState WHEN createOrUpdateCount() with change = -5 THEN lot state is created with count = 1`() {
        testCreateOrUpdate(change = -5, expected = 1)
    }

    @Test
    fun `GIVEN a lot number already in lotState WHEN createOrUpdateCount() with change = 5 THEN lot state count is incremented by 5`() {
        testCreateOrUpdate(change = 5, expected = 10, initialCount = 5)
    }

    @Test
    fun `GIVEN a lot number already in lotState WHEN createOrUpdateCount() with change = -5 THEN lot state count is decremented by 5`() {
        testCreateOrUpdate(change = -5, expected = 5, initialCount = 10)
    }

    @Test
    fun `GIVEN a lot number already in lotState WHEN createOrUpdateCount() with change = -5 THEN lot state count is decremented by 5 but not set to less than 1`() {
        testCreateOrUpdate(change = -5, expected = 1, initialCount = 5)
    }

    @Test
    fun `GIVEN a lot number not in lotState WHEN createOrIncrementCount() THEN lot state is created with count = 1`() {
        runTest {
            // Create lot state -- count should be 1
            session.createOrIncrementCount(lotNumber)

            val lotState = session.lotState.value[lotNumber]
            assertNotNull(lotState)
            assertEquals(1, lotState?.count)
        }
    }

    @Test
    fun `GIVEN a lot number already in lotState WHEN createOrIncrementCount() THEN count is incremented by 1`() {
        runTest {
            // Create lot state -- count should be 1
            session.createOrIncrementCount(lotNumber)
            // Increment count by 1 -- count should be 2
            session.createOrIncrementCount(lotNumber)

            val lotState = session.lotState.value[lotNumber]
            assertNotNull(lotState)
            assertEquals(2, lotState?.count)
        }
    }

    private fun testCreateOrUpdate(change: Int, expected: Int) {
        runTest {
            session.createOrUpdateCount(lotNumber, change)

            val lotState = session.lotState.value[lotNumber]
            assertNotNull(lotState)
            assertEquals(expected, lotState?.count)
        }
    }

    private fun testCreateOrUpdate(
        change: Int,
        expected: Int,
        initialCount: Int
    ) {
        runTest {
            session.createOrUpdateCount(lotNumber, initialCount)
            session.createOrUpdateCount(lotNumber, change)

            val lotState = session.lotState.value[lotNumber]
            assertNotNull(lotState)
            assertEquals(expected, lotState?.count)
        }
    }

    private fun testSetCount(count: Int, expected: Int) {
        runTest {
            session.setCount(lotNumber, count)

            val lotState = session.lotState.value[lotNumber]
            assertNotNull(lotState)
            assertEquals(expected, lotState?.count)
        }
    }

    private fun testSetCount(
        newCount: Int,
        expected: Int,
        initialCount: Int
    ) {
        runTest {
            session.setCount(lotNumber, initialCount)
            // Create lot state -- count should be 10
            session.setCount(lotNumber, newCount)

            val lotState = session.lotState.value[lotNumber]
            assertNotNull(lotState)
            assertEquals(expected, lotState?.count)
        }
    }
}
