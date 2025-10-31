package com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.search

import androidx.annotation.DrawableRes
import androidx.compose.ui.text.AnnotatedString

data class SelectedLot(
    val lotNumber: AnnotatedString,
    @DrawableRes val presentationIcon: Int,
    val productName: AnnotatedString
)
