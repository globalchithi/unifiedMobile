package com.vaxcare.unifiedhub.core.data.repository

import android.os.Build
import android.util.Base64
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.BuildConfig
import com.vaxcare.unifiedhub.core.data.datasource.DeviceNetworkProvider
import com.vaxcare.unifiedhub.core.datastore.datasource.DevicePreferenceDataSource
import com.vaxcare.unifiedhub.core.datastore.datasource.LocationPreferenceDataSource
import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import com.vaxcare.unifiedhub.core.model.ConnectionType
import com.vaxcare.unifiedhub.core.network.interceptor.IdentifierProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val NO_USER_LOGGED_IN = "NO_USER_LOGGED_IN"

/**
 * Helper class that extracts the needed information from different repositories to provide the
 * token needed for the X-VaxHub-Identifier header, mobileData header, userSessionId and the
 * correlationId
 *
 * @param devicePreferenceRepository for serial, imei and iccid
 * @param locationPreferenceRepository for partnerId / clinicId
 * @param userSessionPreferenceRepository for userId, userName and userSessionId
 * @param deviceNetworkProvider exposes a flow containing data about the device's network
 * @param dispatcherProvider for scope dispatcher
 */
@Singleton
class VaxHubIdentifierProvider @Inject constructor(
    devicePreferenceRepository: DevicePreferenceDataSource,
    locationPreferenceRepository: LocationPreferenceDataSource,
    userSessionPreferenceRepository: UserSessionPreferenceDataSource,
    deviceNetworkProvider: DeviceNetworkProvider,
    dispatcherProvider: DispatcherProvider
) : IdentifierProvider {
    private val job: Job = SupervisorJob()
    private val scope = CoroutineScope(dispatcherProvider.io + job)
    private var headerInfo = HeaderInfo.Builder().build()

    init {
        scope.launch {
            locationPreferenceRepository.partnerId
                .combine(locationPreferenceRepository.parentClinicId) { pid, cid ->
                    HeaderInfo
                        .Builder()
                        .partnerId(pid)
                        .clinicId(cid)
                }.combine(devicePreferenceRepository.serialNumber) { build, serial ->
                    build.serial(serial)
                }.combine(devicePreferenceRepository.imei) { build, imei -> build.imei(imei) }
                .combine(devicePreferenceRepository.iccid) { build, iccid -> build.iccid(iccid) }
                .combine(userSessionPreferenceRepository.userId) { build, uid -> build.userId(uid) }
                .combine(userSessionPreferenceRepository.userName) { build, name ->
                    build.username(
                        name
                    )
                }.combine(userSessionPreferenceRepository.sessionId) { build, sid ->
                    build.userSession(
                        sid
                    )
                }.combine(deviceNetworkProvider.networkInfo) { build, nd ->
                    build.cellular(
                        nd.connectionTypes.all { it == ConnectionType.CELLULAR }
                    )
                }.collectLatest { builder -> headerInfo = builder.build() }
        }
    }

    override fun header(): String {
        val result = StringBuilder().apply {
            append("{\"androidSdk\":${Build.VERSION.SDK_INT},")
            append("\"androidVersion\":\"${Build.VERSION.RELEASE}\",")
            append("\"assetTag\":-1,")
            append("\"clinicId\":${headerInfo.clinicId},")
            append("\"deviceSerialNumber\":\"${headerInfo.deviceSerialNumber}\",")
            append("\"partnerId\":${headerInfo.partnerId},")
            append("\"userId\":${headerInfo.userId},")
            append("\"userName\": \"${headerInfo.userName}\",")
            append("\"version\":${BuildConfig.VERSION_CODE},")
            append("\"versionName\":\"${BuildConfig.VERSION_NAME}\",")
            append("\"modelType\":\"UnifiedHub\"")
            if (headerInfo.imei.isNotEmpty() && headerInfo.iccid.isNotEmpty()) {
                append(",\"imei\":\"${headerInfo.imei}\",")
                append("\"iccid\":\"${headerInfo.iccid}\"")
            }
            append("}")
        }
        return Base64.encodeToString(result.toString().toByteArray(), Base64.NO_WRAP)
    }

    /**
     * This was an ask from Bill Dean (bdean@vaxcare.com) long ago.
     * This is what they use in AI to link requests and traces together.
     */
    override fun correlationId(): String {
        val correlationId = UUID.randomUUID().toString().replace("-", "")
        val secondCorrelationId = UUID
            .randomUUID()
            .toString()
            .replace("-", "")
            .substring(0, 16)
        return "00-$correlationId-$secondCorrelationId-01"
    }

    override fun isCellular(): Boolean = headerInfo.isCellular

    override fun sessionId(): String = headerInfo.userSessionId

    /**
     * Class for helping create the VaxHubIdentifier
     */
    private data class HeaderInfo(
        val partnerId: Long,
        val clinicId: Long,
        val deviceSerialNumber: String,
        val userId: Long,
        val userName: String,
        val userSessionId: String,
        val imei: String,
        val iccid: String,
        val isCellular: Boolean
    ) {
        class Builder(
            private var partnerId: Long = 0,
            private var clinicId: Long = 0,
            private var deviceSerialNumber: String = "",
            private var userId: Long = 0,
            private var userName: String = "",
            private var userSessionId: String = NO_USER_LOGGED_IN,
            private var imei: String = "",
            private var iccid: String = "",
            private var isCellular: Boolean = false
        ) {
            fun partnerId(pid: Long) = apply { partnerId = pid }

            fun clinicId(cid: Long) = apply { clinicId = cid }

            fun userId(uid: Long) = apply { userId = uid }

            fun serial(serial: String) = apply { deviceSerialNumber = serial }

            fun username(name: String) = apply { userName = name }

            fun userSession(sid: String) = apply { userSessionId = sid }

            fun imei(ime: String) = apply { imei = ime }

            fun iccid(icci: String) = apply { iccid = icci }

            fun cellular(cell: Boolean) = apply { isCellular = cell }

            fun build() =
                HeaderInfo(
                    partnerId = partnerId,
                    clinicId = clinicId,
                    deviceSerialNumber = deviceSerialNumber,
                    userId = userId,
                    userName = userName,
                    userSessionId = userSessionId,
                    imei = imei,
                    iccid = iccid,
                    isCellular = isCellular
                )
        }
    }
}
