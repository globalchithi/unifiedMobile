package com.vaxcare.unifiedhub.core.common.ext

fun <K, V> MutableMap<K, V>.addIfNotEmpty(key: K, value: V) {
    if (value.toString().isNotEmpty()) {
        this[key] = value
    }
}
