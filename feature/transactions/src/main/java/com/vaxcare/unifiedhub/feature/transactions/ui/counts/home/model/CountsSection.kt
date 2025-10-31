package com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.model

import androidx.annotation.StringRes
import com.vaxcare.unifiedhub.core.designsystem.R

enum class CountsSection(
    @StringRes val title: Int
) {
    NON_SEASONAL(R.string.nonseasonal),
    SEASONAL(R.string.seasonal)
}
