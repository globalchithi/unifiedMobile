package com.vaxcare.unifiedhub.core.ui.component.keypad

import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

internal object KeypadTokens {
    const val KEYPAD_BTN_TAG_PREFIX = "btn_"
    const val MAX_INPUT_LENGTH_DEFAULT = 7

    private val keypadResources = mapOf(
        '1' to DesignSystemR.drawable.ic_digit_1,
        '2' to DesignSystemR.drawable.ic_digit_2,
        '3' to DesignSystemR.drawable.ic_digit_3,
        '4' to DesignSystemR.drawable.ic_digit_4,
        '5' to DesignSystemR.drawable.ic_digit_5,
        '6' to DesignSystemR.drawable.ic_digit_6,
        '7' to DesignSystemR.drawable.ic_digit_7,
        '8' to DesignSystemR.drawable.ic_digit_8,
        '9' to DesignSystemR.drawable.ic_digit_9,
        'd' to DesignSystemR.drawable.ic_arrow_back,
        '0' to DesignSystemR.drawable.ic_digit_0,
        'c' to DesignSystemR.drawable.ic_check,
    )
    val keypadRows = keypadResources
        .toList()
        .chunked(3)
}
