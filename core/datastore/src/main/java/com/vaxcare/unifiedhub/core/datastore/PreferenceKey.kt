package com.vaxcare.unifiedhub.core.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKey {
    val CLINIC_ID = longPreferencesKey("CLINIC_ID_KEY")
    val ICCID = stringPreferencesKey("ICCID_KEY")
    val IMEI = stringPreferencesKey("IMEI_KEY")
    val PARENT_CLINIC_ID = longPreferencesKey("PARENT_CLINIC_ID_KEY")
    val PARTNER_ID = longPreferencesKey("PARTNER_ID_KEY")
    val SERIAL_NUMBER = stringPreferencesKey("SERIAL_NUMBER_KEY")
    val SESSION_ID = stringPreferencesKey("USER_SESSION_ID_KEY")
    val TABLET_ID = stringPreferencesKey("TABLET_ID_KEY")
    val USER_ID = longPreferencesKey("USER_ID_KEY")
    val USER_NAME = stringPreferencesKey("USER_NAME_KEY")
    val IS_LOCATION_SYNCED = booleanPreferencesKey("IS_LOCATION_SYNCED")
    val LAST_USER_SYNC_DATE = stringPreferencesKey("LAST_USER_SYNC_DATE")
    val SCANNER_LICENSE = stringPreferencesKey("SCANNER_LICENSE")
    val SCANNER_CUSTOMER_ID = stringPreferencesKey("SCANNER_CUSTOMER_ID")
    val LAST_SELECTED_STOCK = intPreferencesKey("LAST_SELECTED_STOCK")
    val DATADOG_CLIENT_TOKEN = stringPreferencesKey("DATADOG_CLIENT_TOKEN")
    val DATADOG_APPLICATION_ID = stringPreferencesKey("DATADOG_APPLICATION_ID")
    val DATADOG_RUM_SAMPLE_RATE = floatPreferencesKey("DATADOG_RUM_SAMPLE_RATE")
    val DATADOG_SESSION_REPLAY_SAMPLE_RATE = floatPreferencesKey("DATADOG_SESSION_REPLAY_SAMPLE_RATE")
    val DATADOG_SITE = stringPreferencesKey("DATADOG_SITE")
    val DATADOG_ENABLED = booleanPreferencesKey("DATADOG_ENABLED")
}
