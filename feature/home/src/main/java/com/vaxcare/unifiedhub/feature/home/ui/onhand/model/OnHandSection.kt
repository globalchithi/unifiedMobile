package com.vaxcare.unifiedhub.feature.home.ui.onhand.model

import androidx.annotation.StringRes
import com.vaxcare.unifiedhub.core.designsystem.R

enum class OnHandSection(
    @StringRes val title: Int
) {
    NON_SEASONAL(R.string.nonseasonal),
    SEASONAL(R.string.seasonal)
}
