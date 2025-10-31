package com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class PresentationUI(
    val id: Int,
    @StringRes val name: Int,
    @DrawableRes val iconRes: Int
)
