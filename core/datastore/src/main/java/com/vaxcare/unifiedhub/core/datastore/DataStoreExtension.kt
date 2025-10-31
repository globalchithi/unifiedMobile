package com.vaxcare.unifiedhub.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

operator fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>, default: T): Flow<T> =
    data.map { it[key] ?: default }

suspend fun <T> DataStore<Preferences>.setValue(key: Preferences.Key<T>, value: T) {
    edit { it[key] = value }
}

suspend fun DataStore<Preferences>.clearValue(key: Preferences.Key<*>) {
    edit { it.remove(key) }
}

suspend fun DataStore<Preferences>.clearAll() {
    edit { prefs ->
        prefs
            .asMap()
            .keys
            .forEach {
                prefs.remove(it)
            }
    }
}

/**
 * Wipe the datastore except for [excludedKeys].
 *
 * @param excludedKeys A [Set] of keys that should not be wiped.
 */
suspend fun DataStore<Preferences>.clearAllExcluding(excludedKeys: Set<Preferences.Key<*>>) {
    edit { prefs ->
        prefs
            .asMap()
            .keys
            .subtract(excludedKeys)
            .forEach {
                prefs.remove(it)
            }
    }
}

fun <T> DataStore<Preferences>.getPreferenceWithDefault(preferenceKey: Preferences.Key<T>, defaultValue: T): T {
    val dataStore = this
    val prefValue = runBlocking {
        dataStore.data.first()[preferenceKey]
            ?: defaultValue
    }
    return prefValue
}
