package com.vaxcare.unifiedhub.core.ui.component.modalsidesheet

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlin.math.abs
import kotlin.math.sign

@Composable
fun rememberModalSideSheetState(
    initialValue: ModalSideSheetValue = ModalSideSheetValue.Hidden,
    confirm: (ModalSideSheetValue) -> Boolean = { true },
    spec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
) = rememberSaveable(
    saver = ModalSideSheetState.saver(confirm, spec)
) { ModalSideSheetState(initialValue, confirm, spec) }

enum class ModalSideSheetValue { Hidden, Expanded }

@Stable
class ModalSideSheetState internal constructor(
    initialValue: ModalSideSheetValue,
    private val confirm: (ModalSideSheetValue) -> Boolean,
    private val spec: AnimationSpec<Float>
) {
    private val offset =
        Animatable(if (initialValue == ModalSideSheetValue.Hidden) Float.NaN else 0f)
    internal var hiddenPx: Float by mutableFloatStateOf(Float.NaN)
        private set

    suspend fun updateAnchors(hidden: Float) {
        hiddenPx = hidden
        offset.updateBounds(minOf(hidden, 0f), maxOf(hidden, 0f))
        if (currentValue == ModalSideSheetValue.Hidden) {
            offset.snapTo(hidden)
        }
    }

    val currentValue: ModalSideSheetValue
        get() = if (offset.value == 0f) ModalSideSheetValue.Expanded else ModalSideSheetValue.Hidden
    val isVisible get() = currentValue == ModalSideSheetValue.Expanded

    suspend fun show() = animateTo(ModalSideSheetValue.Expanded)

    suspend fun hide() = animateTo(ModalSideSheetValue.Hidden)

    fun offsetX(): State<Float> = derivedStateOf { offset.value }

    suspend fun snapBy(delta: Float) {
        offset.snapTo(
            (offset.value + delta)
                .coerceIn(minOf(hiddenPx, 0f), maxOf(hiddenPx, 0f))
        )
    }

    suspend fun settle(lastVelocity: Float) {
        val target = when {
            abs(lastVelocity) > 800f ->
                if (lastVelocity.sign == hiddenPx.sign) {
                    ModalSideSheetValue.Hidden
                } else {
                    ModalSideSheetValue.Expanded
                }

            abs(offset.value) > abs(hiddenPx) * 0.5f ->
                ModalSideSheetValue.Hidden

            else -> ModalSideSheetValue.Expanded
        }
        animateTo(target)
    }

    private suspend fun animateTo(target: ModalSideSheetValue) {
        val targetPx = if (target == ModalSideSheetValue.Hidden) hiddenPx else 0f
        if (abs(offset.value - targetPx) < 0.5f || !confirm(target)) return
        offset.animateTo(targetPx, spec)
    }

    companion object {
        fun saver(confirm: (ModalSideSheetValue) -> Boolean, spec: AnimationSpec<Float>) =
            androidx.compose.runtime.saveable.Saver<ModalSideSheetState, ModalSideSheetValue>(
                save = { it.currentValue },
                restore = { ModalSideSheetState(it, confirm, spec) }
            )
    }
}
