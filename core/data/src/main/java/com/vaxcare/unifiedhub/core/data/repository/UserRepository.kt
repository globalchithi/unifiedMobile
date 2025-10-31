package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.mapper.UserMapper
import com.vaxcare.unifiedhub.core.database.dao.UserDao
import com.vaxcare.unifiedhub.core.database.model.UserEntity
import com.vaxcare.unifiedhub.core.datastore.datasource.LocationPreferenceDataSource
import com.vaxcare.unifiedhub.core.model.User
import com.vaxcare.unifiedhub.core.network.api.SetupApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

interface UserRepository {
    /**
     * Grabs users from api and forces deleteAndInsertAll to run, and overriding
     * the lastSync in locationPreferences
     */
    suspend fun forceSyncUsers(isCalledByJob: Boolean = false)

    /**
     * Boolean to check locationPreferences if it has been 1 minute since last sync
     *
     * @return Comparison of now and last sync + 1 minute
     */
    suspend fun needUsersSynced(): Boolean

    /**
     * Ping VHAPI in order to tell the server a session has started and we are online
     */
    suspend fun pingVaxCareServer()

    fun getAllUsers(): Flow<List<User>>

    suspend fun getUser(pin: String): User?
}

class UserRepositoryImpl @Inject constructor(
    private val locationPreferenceDataSource: LocationPreferenceDataSource,
    private val setupApi: SetupApi,
    private val userDao: UserDao,
    private val dispatcherProvider: DispatcherProvider,
    private val mapper: UserMapper
) : UserRepository {
    override suspend fun forceSyncUsers(isCalledByJob: Boolean) {
        withContext(dispatcherProvider.io) {
            getUsersForPartner(isCalledByJob)?.let { users ->
                deleteAndInsertAll(users)
                locationPreferenceDataSource.setLastUserSyncDate(LocalDateTime.now().toString())
            }
        }
    }

    override suspend fun needUsersSynced(): Boolean {
        val now = LocalDateTime.now()
        val lastSyncDate = locationPreferenceDataSource.lastUsersSyncDate.firstOrNull()
        return lastSyncDate.isNullOrBlank() ||
            now.isAfter(
                LocalDateTime
                    .parse(lastSyncDate)
                    .plusMinutes(1L)
            )
    }

    override suspend fun pingVaxCareServer() {
        withContext(dispatcherProvider.io) { setupApi.pingVaxCareServer() }
    }

    override fun getAllUsers(): Flow<List<User>> =
        userDao
            .getAll()
            .map { list -> list.map { mapper.entityToDomain(it) } }

    private suspend fun getUsersForPartner(isCalledByJob: Boolean) =
        locationPreferenceDataSource.partnerId.firstOrNull()?.let { pid ->
            setupApi
                .getUsersForPartner(
                    partnerId = pid.toString(),
                    isCalledByJob = isCalledByJob
                ).body()
                ?.map { mapper.networkToEntity(it) }
        }

    private suspend fun deleteAndInsertAll(users: List<UserEntity>) {
        withContext(Dispatchers.IO) {
            with(userDao) {
                deleteAll()
                insertAll(users)
            }
        }
    }

    override suspend fun getUser(pin: String): User? =
        userDao
            .getUserByPin(pin)
            ?.let { mapper.entityToDomain(it) }
}
