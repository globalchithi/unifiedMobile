package com.vaxcare.unifiedhub.core.model

enum class SignalStrengthLevel {
    GREAT,
    GOOD,
    FAIR,
    POOR,
    BAD,
    NO_INTERNET;

    companion object {
        fun fromDbmSignal(dbmSignal: Int) =
            when (dbmSignal) {
                in -45..Int.MAX_VALUE -> GREAT
                in -67..-46 -> GOOD
                in -70..-68 -> FAIR
                in -80..-71 -> POOR
                else -> BAD
            }
    }
}
