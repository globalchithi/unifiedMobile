package com.vaxcare.unifiedhub.core.domain

import com.vaxcare.unifiedhub.core.data.repository.AppRepository
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.data.repository.PreferenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SetupVaxHub @Inject constructor(
    private val appRepository: AppRepository,
    private val locationRepository: LocationRepository,
    private val preferenceRepository: PreferenceRepository,
) {
    operator fun invoke(
        pid: String,
        cid: String,
        tabletId: String,
        scope: CoroutineScope
    ) = scope.launch {
        appRepository.wipeDatabase()
        preferenceRepository.clearPreferences()
        locationRepository.setLocationPreferences(pid, cid, tabletId)
        syncLocation().join()
    }

    private fun CoroutineScope.syncLocation() =
        launch {
            locationRepository.sync()
        }
}
