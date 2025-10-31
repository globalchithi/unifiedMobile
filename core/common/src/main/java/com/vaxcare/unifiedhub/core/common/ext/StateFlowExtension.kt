package com.vaxcare.unifiedhub.core.common.ext

import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

/**
 * Returns a [StateFlow] containing the results of applying the given [transform] function to each
 * value of the original [StateFlow]. This respects the dynamics of a [StateFlow], meaning that only
 * distinct values are emitted.
 *
 * This is an experimental solution from a kotlinx.coroutines contributor, proposed in this
 * ongoing [GitHub issue](https://github.com/Kotlin/kotlinx.coroutines/issues/2631#issuecomment-2812699291).
 *
 */
@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
fun <T, R> StateFlow<T>.mapSync(transform: (T) -> R): StateFlow<R> =
    object : StateFlow<R> {
        override val replayCache: List<R> get() = listOf(value)

        override suspend fun collect(collector: FlowCollector<R>): Nothing {
            var lastEmittedValue: Any? = nullSurrogate
            this@mapSync.collect { newValue ->
                val transformedValue = transform(newValue)
                if (transformedValue != lastEmittedValue) {
                    lastEmittedValue = transformedValue
                    collector.emit(transformedValue)
                }
            }
        }

        private var lastUpstreamValue = this@mapSync.value

        override var value: R = transform(lastUpstreamValue)
            private set
            get() {
                val currentUpstreamValue: T = this@mapSync.value
                if (currentUpstreamValue == lastUpstreamValue) return field
                val newValue = transform(currentUpstreamValue)
                field = newValue
                lastUpstreamValue = currentUpstreamValue
                return newValue
            }
    }

private val nullSurrogate = Any()
