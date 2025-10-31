package com.vaxcare.unifiedhub.core.data.datasource

import android.view.Surface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrientationProvider @Inject constructor() {
    private val _orientation = MutableStateFlow(-1)
    val orientation: StateFlow<Int> = _orientation

    fun updateWithRotation(rotation: Int) {
        val orientation = when (rotation) {
            // We mirror 90 & 270 here. This is because the ROTATION constant represents the SCREEN
            // rotation, but we want to track the DEVICE rotation.
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 270
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 90
            else -> -1
        }
        _orientation.update { orientation }
    }
}
