package com.vaxcare.unifiedhub.core.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.vaxcare.unifiedhub.core.ui.model.StockUi

@Composable
fun ProvideStock(type: StockUi, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        value = LocalStock provides type,
        content = content
    )
}
