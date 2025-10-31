package com.vaxcare.unifiedhub.core.ui.compose

import android.media.AudioManager
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.vaxcare.unifiedhub.core.ui.model.StockUi

val LocalAudioManager = staticCompositionLocalOf<AudioManager?> { null }
val LocalStock = compositionLocalOf { StockUi.PRIVATE }
