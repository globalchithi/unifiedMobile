package com.vaxcare.unifiedhub.core.common.ext

import java.util.Locale
import kotlin.math.absoluteValue

/**
 * Converts this [Float] into a [String] that represents USD. The number is formatted with 2 decimal
 * places and with commas for thousands, millions, etc.
 */
fun Float.toUSD(): String =
    buildString {
        if (this@toUSD < 0) {
            append('-')
        }
        val fmt = String.format(
            locale = Locale.US,
            format = "%,.2f",
            this@toUSD.absoluteValue
        )
        append("$$fmt")
    }
