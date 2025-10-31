package com.vaxcare.unifiedhub.feature.home.ui.home.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface Notification {
    val position: Int

    data class OverdueCount(val daysOverdue: Int) : Notification {
        override val position: Int = 1
    }

    data class ExpiredDoses(val count: Int) : Notification {
        override val position: Int = 2
    }

    data object AppUpdate : Notification {
        override val position: Int = 3
    }
}
