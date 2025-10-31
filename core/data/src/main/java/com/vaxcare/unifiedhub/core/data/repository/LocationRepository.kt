package com.vaxcare.unifiedhub.core.data.repository

import com.vaxcare.unifiedhub.core.data.mapper.FeatureFlagMapper
import com.vaxcare.unifiedhub.core.data.mapper.LocationMapper
import com.vaxcare.unifiedhub.core.database.dao.LocationDao
import com.vaxcare.unifiedhub.core.database.model.FeatureFlagEntity
import com.vaxcare.unifiedhub.core.datastore.datasource.LocationPreferenceDataSource
import com.vaxcare.unifiedhub.core.model.Location
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.network.api.SetupApi
import com.vaxcare.unifiedhub.core.network.model.FeatureFlagDTO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

interface LocationRepository {
    /**
     * The partner ID and clinic ID that represent this device's location.
     */
    val pidCid: Flow<Pair<Long, Long>>

    /**
     * Whether or not the device is currently synced to a location.
     */
    val isSynced: Flow<Boolean>

    suspend fun setLocationPreferences(
        partnerID: String,
        clinicID: String,
        tabletId: String
    )

    fun getLocation(): Flow<Location?>

    fun getStockTypes(): Flow<List<StockType>>

    suspend fun getCheckData(partnerID: String, clinicID: String): Pair<Boolean, String>

    /**
     * Sync the Room table with the remote data source
     *
     */
    suspend fun sync()

    suspend fun getFeatureFlagsAsync(): List<FeatureFlagDTO>

    // TODO: implement the remaining members
}

class LocationRepositoryImpl @Inject constructor(
    private val setupApi: SetupApi,
    private val locationPreferenceDataSource: LocationPreferenceDataSource,
    private val locationDao: LocationDao,
    private val locationMapper: LocationMapper,
    private val featureFlagMapper: FeatureFlagMapper
) : LocationRepository {
    override val pidCid
        get() = with(locationPreferenceDataSource) {
            partnerId.combine(parentClinicId) { pid, cid ->
                pid to cid
            }
        }

    override val isSynced: Flow<Boolean>
        get() = locationPreferenceDataSource.isLocationSynced

    override suspend fun setLocationPreferences(
        partnerID: String,
        clinicID: String,
        tabletId: String
    ) = with(locationPreferenceDataSource) {
        setPartnerId(partnerID.toLong())
        setParentClinicId(clinicID.toLong())
        setTabletId(tabletId)
        setIsLocationSynced(true)
    }

    override fun getLocation(): Flow<Location?> = locationDao.get().map(locationMapper::entityToDomain)

    override fun getStockTypes(): Flow<List<StockType>> =
        getLocation().map { it?.stockTypes ?: listOf(StockType.PRIVATE) }

    override suspend fun getCheckData(partnerID: String, clinicID: String): Pair<Boolean, String> {
        val response = setupApi.getPidCidCheck(partnerID, clinicID)
        val checkResult = response.body() ?: return Pair(false, "")
        return Pair(checkResult.result, checkResult.tabletId)
    }

    override suspend fun sync() {
        coroutineScope {
            locationPreferenceDataSource.parentClinicId.collectLatest {
                // guarantee that the datastore contains a non-0 value before making the request
                if (it > 0) {
                    setupApi.getLocationData(it.toString()).body()?.let { body ->
                        val locationEntity = locationMapper.networkToEntity(body)
                        val featureFlags =
                            featureFlagMapper.networkToEntity(body.activeFeatureFlags)
                        locationDao.insert(locationEntity)
                        locationDao.replaceAllFeatureFlags(featureFlags.filterFeatureFlags())
                    } ?: {
                        Timber.e("Could not sync location due to a null response body.")
                    }

                    // cancel the coroutineScope, thus canceling this collector
                    coroutineContext.cancel()
                }
            }
        }
    }

    override suspend fun getFeatureFlagsAsync(): List<FeatureFlagDTO> {
        val entities = locationDao.getFeatureFlags()
        return featureFlagMapper.entityToDomain(entities)
    }

    /**
     * Ignores adding LARCsOnly FF is LARCsEnabled FF is not present
     */
    private fun List<FeatureFlagEntity>.filterFeatureFlags(): List<FeatureFlagEntity> {
        val hasLarcsOnly = any { it.featureFlagName == "LARCsOnly" }
        val hasLarcsEnabled = any { it.featureFlagName == "LARCsEnabled" }
        return if (hasLarcsOnly && !hasLarcsEnabled) {
            this.filter { it.featureFlagName != "LARCsOnly" }
        } else {
            this
        }
    }
}
