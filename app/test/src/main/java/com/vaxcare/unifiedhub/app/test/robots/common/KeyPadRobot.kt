package com.vaxcare.unifiedhub.app.test.robots.common

import androidx.compose.ui.test.junit4.ComposeTestRule
import com.vaxcare.unifiedhub.app.test.arch.BaseRobot
import com.vaxcare.unifiedhub.app.test.rule.TestServerRule
import com.vaxcare.unifiedhub.core.ui.TestTags

class KeyPadRobot<NextScreenRobot : BaseRobot<*>>(
    private val nextScreenRobot: NextScreenRobot,
    compose: ComposeTestRule,
    server: TestServerRule
) : BaseRobot<KeyPadRobot<NextScreenRobot>>(compose, server) {
    fun typeSequence(sequence: String) =
        apply {
            sequence.toCharArray().forEach { digit ->
                val tag = getTagForDigit(digit)
                waitAndClick(tag)
            }
        }

    fun clickConfirm(): NextScreenRobot {
        waitAndClick(TestTags.KeyPad.CONFIRM_BUTTON)
        return nextScreenRobot
    }

    fun clickBackspace() =
        apply {
            waitAndClick(TestTags.KeyPad.BACKSPACE_BUTTON)
        }

    fun clickClear() =
        apply {
            waitAndClick(TestTags.KeyPad.CLEAR_BUTTON)
        }

    private fun getTagForDigit(digit: Char): String = TestTags.KeyPad.digitButton(digit)
}
