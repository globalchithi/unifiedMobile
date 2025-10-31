package com.vaxcare.unifiedhub.core.data.repository

import android.os.Build
import com.vaxcare.unifiedhub.core.data.BuildConfig
import com.vaxcare.unifiedhub.core.datastore.datasource.DevicePreferenceDataSource
import com.vaxcare.unifiedhub.core.datastore.datasource.LocationPreferenceDataSource
import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import com.vaxcare.unifiedhub.core.network.model.PostDTO
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

class PostDtoFactory @Inject constructor(
    private val locationPrefs: LocationPreferenceDataSource,
    private val userSessionPrefs: UserSessionPreferenceDataSource,
    private val devicePrefs: DevicePreferenceDataSource,
) {
    suspend fun <T> createPost(payload: T): PostDTO<T> =
        PostDTO(
            androidSdk = Build.VERSION.SDK_INT,
            androidVersion = Build.VERSION.RELEASE,
            assetTag = "-1",
            clinicId = locationPrefs.parentClinicId.first(),
            deviceSerialNumber = devicePrefs.serialNumber.first(),
            key = UUID.randomUUID().toString(),
            payload = payload,
            version = BuildConfig.VERSION_CODE,
            versionName = BuildConfig.VERSION_NAME,
            userName = userSessionPrefs.userName.first(),
            userId = userSessionPrefs.userId.first().toInt()
        )
}
