package com.vaxcare.unifiedhub.core.data.extension

import android.icu.text.CompactDecimalFormat
import java.text.NumberFormat
import java.util.Locale

/**
 * Convert the integer to shorthand - meaning it will be converted using the format "X.XXX{M}" where
 * M is based on the current value.
 * Ex:  1500.toShortHand() will return "1.5K"
 *      1_250_000.shoShortHand() will return "1.25M"
 *
 * @return formatted shorthand value of the int. Anything below 1,000 is not different
 */
fun Int.toShorthand(): String =
    if (this in 1..999) {
        NumberFormat.getNumberInstance(Locale.US).format(this)
    } else {
        CompactDecimalFormat
            .getInstance(Locale.US, CompactDecimalFormat.CompactStyle.SHORT)
            .format(this)
    }
