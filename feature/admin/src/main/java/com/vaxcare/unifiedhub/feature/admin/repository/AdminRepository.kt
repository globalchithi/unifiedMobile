package com.vaxcare.unifiedhub.feature.admin.repository

import com.vaxcare.unifiedhub.core.network.api.SetupApi
import kotlinx.coroutines.ensureActive
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

interface AdminRepository {
    suspend fun validatePassword(password: String): Boolean
}

class AdminRepositoryImpl @Inject constructor(
    private val setupApi: SetupApi
) : AdminRepository {
    override suspend fun validatePassword(password: String): Boolean =
        try {
            setupApi.validatePassword(password).body() == true
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e(e)
            false
        }
}
