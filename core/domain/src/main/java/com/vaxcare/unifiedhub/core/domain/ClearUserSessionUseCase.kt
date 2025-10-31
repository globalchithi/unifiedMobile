package com.vaxcare.unifiedhub.core.domain

import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import timber.log.Timber
import javax.inject.Inject

class ClearUserSessionUseCase @Inject constructor(
    private val userSessionPreferenceDataSource: UserSessionPreferenceDataSource,
) {
    suspend operator fun invoke() {
        userSessionPreferenceDataSource.clearUserSession()
        Timber.i("User session was cleared.")
    }
}
