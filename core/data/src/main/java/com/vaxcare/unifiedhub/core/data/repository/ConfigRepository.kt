package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.datastore.datasource.LicensePreferenceDataSource
import com.vaxcare.unifiedhub.core.network.api.SetupApi
import com.vaxcare.unifiedhub.core.network.util.IS_CALLED_BY_JOB
import retrofit2.http.Header
import retrofit2.http.Query
import javax.inject.Inject

interface ConfigRepository {
    suspend fun upsertSetupConfig(
        @Query("isOffline") isOffline: Boolean = true,
        @Header(value = IS_CALLED_BY_JOB) isCalledByJob: Boolean = false
    )
}

class ConfigRepositoryImpl @Inject constructor(
    private val setupApi: SetupApi,
    private val licensePrefs: LicensePreferenceDataSource
) : ConfigRepository {
    override suspend fun upsertSetupConfig(isOffline: Boolean, isCalledByJob: Boolean) {
        val setupConfig = setupApi.getSetupConfig(isOffline.toString(), isCalledByJob)
        setupConfig.codeCorpLicense?.let {
            licensePrefs.setCustomerId(it.customerId)
            licensePrefs.setScannerLicense(it.key)
        }
        setupConfig.dataDogLicense?.let {
            licensePrefs.setDatadogClientToken(it.clientToken)
            licensePrefs.setDatadogApplicationId(it.applicationId)
            licensePrefs.setDatadogRUMSampleRate(it.rumSampleRate)
            licensePrefs.setDatadogSessionReplaySampleRate(it.sessionReplaySampleRate)
            licensePrefs.setDatadogSite(it.site)
            licensePrefs.setDatadogEnabled(it.enabled)
        }
    }
}
