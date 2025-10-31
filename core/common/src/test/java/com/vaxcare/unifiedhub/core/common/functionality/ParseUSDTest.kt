package com.vaxcare.unifiedhub.core.common.functionality

import com.vaxcare.unifiedhub.core.common.ext.toUSD
import org.junit.Assert.assertEquals
import org.junit.Test

class ParseUSDTest {
    @Test
    fun parseRoundFloat() {
        val (input, expected) = 19F to "$19.00"
        assertEquals(input.toUSD(), expected)
    }

    @Test
    fun parseNegativeRoundFloat() {
        val (input, expected) = -19F to "-$19.00"
        assertEquals(input.toUSD(), expected)
    }

    @Test
    fun parseDecimalFloat() {
        val (input, expected) = 19.99F to "$19.99"
        assertEquals(input.toUSD(), expected)
    }

    @Test
    fun parseNegativeDecimalFloat() {
        val (input, expected) = -19.99F to "-$19.99"
        assertEquals(input.toUSD(), expected)
    }

    @Test
    fun parseLargePositiveFloat() {
        val (input, expected) = 99999.99F to "$99,999.99"
        assertEquals(input.toUSD(), expected)
    }

    @Test
    fun parseLargeNegativeFloat() {
        val (input, expected) = -99999.99F to "-$99,999.99"
        assertEquals(input.toUSD(), expected)
    }

    @Test
    fun parseInteger() {
        val (input, expected) = 19 to "$19.00"
        assertEquals(input.toUSD(), expected)
    }

    @Test
    fun parseLargeInteger() {
        val (input, expected) = 99999 to "$99,999.00"
        assertEquals(input.toUSD(), expected)
    }

    @Test
    fun parseNegativeInteger() {
        val (input, expected) = -19 to "-$19.00"
        assertEquals(input.toUSD(), expected)
    }
}
