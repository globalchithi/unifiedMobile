/**************************************************************************************************
 * Copyright VaxCare (c) 2025.                                                                    *
 **************************************************************************************************/

package com.vaxcare.unifiedhub.library.analytics.managers

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.VisibleForTesting
import com.datadog.android.Datadog
import com.datadog.android.DatadogSite
import com.datadog.android.compose.enableComposeActionTracking
import com.datadog.android.core.configuration.BackPressureMitigation
import com.datadog.android.core.configuration.BackPressureStrategy
import com.datadog.android.core.configuration.BatchSize
import com.datadog.android.core.configuration.Configuration
import com.datadog.android.core.configuration.UploadFrequency
import com.datadog.android.log.Logger
import com.datadog.android.log.Logs
import com.datadog.android.log.LogsConfiguration
import com.datadog.android.ndk.NdkCrashReports
import com.datadog.android.privacy.TrackingConsent
import com.datadog.android.rum.GlobalRumMonitor
import com.datadog.android.rum.Rum
import com.datadog.android.rum.RumConfiguration
import com.datadog.android.rum.RumMonitor
import com.datadog.android.rum.configuration.VitalsUpdateFrequency
import com.datadog.android.rum.metric.interactiontonextview.TimeBasedInteractionIdentifier
import com.datadog.android.rum.metric.networksettled.TimeBasedInitialResourceIdentifier
import com.datadog.android.rum.tracking.MixedViewTrackingStrategy
import com.datadog.android.sessionreplay.ImagePrivacy
import com.datadog.android.sessionreplay.SessionReplay
import com.datadog.android.sessionreplay.SessionReplayConfiguration
import com.datadog.android.sessionreplay.TextAndInputPrivacy
import com.datadog.android.sessionreplay.TouchPrivacy
import com.datadog.android.sessionreplay.material.MaterialExtensionSupport
import com.datadog.android.timber.DatadogTree
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.datastore.datasource.DevicePreferenceDataSource
import com.vaxcare.unifiedhub.core.datastore.datasource.LicensePreferenceDataSource
import com.vaxcare.unifiedhub.core.datastore.datasource.LocationPreferenceDataSource
import com.vaxcare.unifiedhub.core.datastore.datasource.UserSessionPreferenceDataSource
import com.vaxcare.unifiedhub.library.analytics.ApplicationName
import com.vaxcare.unifiedhub.library.analytics.BuildConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatadogManager @Inject constructor(
    private val licensePreferenceDataSource: LicensePreferenceDataSource,
    private val devicePreferenceDataSource: DevicePreferenceDataSource,
    private val locationRepository: LocationRepository,
    private val locationPreferenceDataSource: LocationPreferenceDataSource,
    private val userSessionPreferenceDataSource: UserSessionPreferenceDataSource,
    private val dispatcherProvider: DispatcherProvider,
    private val context: Application,
) {
    private var currentDatadogTree: DatadogTree? = null

    @Suppress("KotlinConstantConditions")
    private val environmentName =
        if (BuildConfig.BUILD_TYPE == "staging") {
            "stg"
        } else if (BuildConfig.BUILD_TYPE == "debug") {
            "dev"
        } else {
            BuildConfig.BUILD_TYPE
        }

    @OptIn(FlowPreview::class)
    suspend fun configureAndEnable() {
        val initial: Pair<Pair<DatadogSettings, Attributes>?, Pair<DatadogSettings, Attributes>>? = null
        buildSettingsFlow()
            .combine(buildAttributesFlow()) { settings, attributes ->
                settings to attributes
            }.debounce(100) // wait briefly for multiple settings/attribute updates
            .runningFold(initial) { history, newValues ->
                history?.run {
                    val (_, last) = this
                    last to newValues
                } ?: (null to newValues)
            }.filterNotNull()
            .map { (prevValues, newValues) ->
                val (newSettings, newAttributes) = newValues
                var settings = newSettings
                prevValues?.first?.run {
                    settings =
                        if (newSettings == this) {
                            newSettings.copy(haveDatadogSettingsChanged = false)
                        } else {
                            newSettings
                        }
                } ?: {
                    settings = newSettings.copy(haveDatadogSettingsChanged = true)
                }
                settings to newAttributes
            }.flowOn(dispatcherProvider.io)
            // Process settings updates!
            .collectLatest { (settings, attributes) ->
                if (!Datadog.isInitialized()) {
                    startDatadog(settings, attributes)
                } else if (settings.haveDatadogSettingsChanged) {
                    restartDatadog(settings, attributes)
                } else {
                    GlobalRumMonitor.get().configureAttributes(attributes)
                    Datadog.setUserInfo(attributes.userId, "", attributes.userName)
                }
            }
    }

    private fun stopDatadog() {
        Timber.d("Stopping Datadog...")
        currentDatadogTree?.run { Timber.uproot(currentDatadogTree!!) }
        Datadog.stopInstance()
    }

    private fun startDatadog(settings: DatadogSettings, attributes: Attributes) {
        if (!settings.enabled) {
            Timber.d("Datadog is not enabled.")
            return
        }

        // configure the Datadog SDK
        Timber.d("Starting Datadog...")
        Datadog.configureAndInit(settings, attributes)

        // configure individual Datadog services
        Rum.configureAndEnable(settings)
        SessionReplay.configureAndEnable(settings)
        NdkCrashReports.enable()

        // apply attributes
        applyAttributes(attributes)
    }

    private fun applyAttributes(attributes: Attributes) {
        Logs.configureAndEnable(attributes)
        GlobalRumMonitor.get().configureAttributes(attributes)
    }

    private fun restartDatadog(settings: DatadogSettings, attributes: Attributes) {
        stopDatadog()
        startDatadog(settings, attributes)
    }

    /**
     * Returns a flow which combines all Datadog settings into a single object.
     *
     * NOTE: If multiple changes come in at once, this combine will execute
     *       multiple times. Main collector in configureAndEnable uses
     *       debounce to delay processing while multiple changes are applied.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun buildSettingsFlow(): Flow<DatadogSettings> =
        combine(
            licensePreferenceDataSource.datadogClientToken.distinctUntilChanged(),
            licensePreferenceDataSource.datadogApplicationId.distinctUntilChanged(),
            licensePreferenceDataSource.datadogRumSampleRate.distinctUntilChanged(),
            licensePreferenceDataSource.datadogSessionReplaySampleRate.distinctUntilChanged(),
            licensePreferenceDataSource.datadogSite.map(::safeSite).distinctUntilChanged(),
            licensePreferenceDataSource.datadogEnabled.distinctUntilChanged(),
        ) { flowArray ->
            val token = flowArray[0] as String
            val appId = flowArray[1] as String
            val rum = flowArray[2] as Float
            val replay = flowArray[3] as Float
            val site = flowArray[4] as DatadogSite
            val enabled = flowArray[5] as Boolean
            DatadogSettings(
                clientToken = token,
                applicationId = appId,
                rumSampleRate = rum,
                sessionReplaySampleRate = replay,
                site = site,
                enabled = enabled,
                haveDatadogSettingsChanged = true
            )
        }

    private fun safeSite(raw: String): DatadogSite =
        runCatching { DatadogSite.valueOf(raw) }.getOrElse {
            Timber.e("Invalid Datadog site: $raw — using US3")
            DatadogSite.US3
        }

    /**
     * Returns a flow which combines all attributes into a single object.
     *
     * NOTE: If multiple changes come in at once, this combine will execute
     *       multiple times. Main collector in configureAndEnable uses
     *       debounce to delay processing while multiple changes are applied.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    @Suppress("UNCHECKED_CAST")
    fun buildAttributesFlow(): Flow<Attributes> =
        combine(
            devicePreferenceDataSource.serialNumber.distinctUntilChanged(),
            devicePreferenceDataSource.imei.distinctUntilChanged(),
            devicePreferenceDataSource.iccid.distinctUntilChanged(),
            locationPreferenceDataSource.partnerId.map(Long::toString).distinctUntilChanged(),
            locationPreferenceDataSource.parentClinicId.map(Long::toString).distinctUntilChanged(),
            featureFlagsFlow(),
            userSessionPreferenceDataSource.userId.map(Long::toString).distinctUntilChanged(),
            userSessionPreferenceDataSource.userName.distinctUntilChanged()
        ) { flowArray ->
            val serialNumber = flowArray[0] as String
            val imei = flowArray[1] as String
            val iccid = flowArray[2] as String
            val partnerId = flowArray[3] as String
            val clinicId = flowArray[4] as String
            val featureFlags = flowArray[5] as Map<String, Boolean>
            val userId = flowArray[6] as String
            val userName = flowArray[7] as String
            Attributes(serialNumber, imei, iccid, featureFlags, userId, userName, partnerId, clinicId)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun featureFlagsFlow(): Flow<Map<String, Boolean>> =
        locationPreferenceDataSource.isLocationSynced.flatMapLatest { isLocationSynced ->
            if (isLocationSynced) {
                flowOf(locationRepository.getFeatureFlagsAsync().associate { it.featureFlagName to true })
            } else {
                flowOf(emptyMap())
            }
        }

    private fun Rum.configureAndEnable(settings: DatadogSettings) {
        with(settings) {
            RumConfiguration
                .Builder(applicationId)
                .trackUserInteractions()
                .trackLongTasks()
                .setSessionSampleRate(rumSampleRate)
                .setVitalsUpdateFrequency(VitalsUpdateFrequency.AVERAGE)
                .setInitialResourceIdentifier(TimeBasedInitialResourceIdentifier())
                .setLastInteractionIdentifier(TimeBasedInteractionIdentifier())
                .useViewTrackingStrategy(MixedViewTrackingStrategy(trackExtras = true))
                .trackBackgroundEvents(true)
                .enableComposeActionTracking()
                .build()
                .also(::enable)
        }
    }

    private fun RumMonitor.configureAttributes(attributes: Attributes) {
        with(attributes) {
            // explicitly casting values to strings so they can be filtered in datadog
            // numbers are treated as performance metrics and can only be graphed with
            addAttribute("androidSdk", "${Build.VERSION.SDK_INT}")
            addAttribute("androidVersion", Build.VERSION.RELEASE)
            addAttribute("assetTag", "-1")
            addAttribute("clinicId", cid)
            addAttribute("serialNumber", serialNumber)
            addAttribute("partnerId", pid)
            addAttribute("version", BuildConfig.VERSION_CODE.toString())
            addAttribute("versionName", BuildConfig.VERSION_NAME.toString())
            addAttribute("modelType", ApplicationName.MODEL_TYPE)

            val imei = imei
            val iccid = iccid
            if (imei.isNotEmpty() && iccid.isNotEmpty()) {
                addAttribute("imei", imei)
                addAttribute("iccid", iccid)
            }

            addFeatureFlagEvaluations(attributes.featureFlags)
        }
    }

    private fun SessionReplay.configureAndEnable(settings: DatadogSettings) {
        with(settings) {
            SessionReplayConfiguration
                .Builder(sessionReplaySampleRate)
                .addExtensionSupport(MaterialExtensionSupport())
                .setTextAndInputPrivacy(TextAndInputPrivacy.MASK_SENSITIVE_INPUTS)
                .setImagePrivacy(ImagePrivacy.MASK_NONE)
                .setTouchPrivacy(TouchPrivacy.SHOW)
                .build()
                .also(::enable)
        }
    }

    private fun Logs.configureAndEnable(attributes: Attributes) {
        LogsConfiguration
            .Builder()
            .build()
            .also(::enable)
        Logger
            .Builder()
            .setNetworkInfoEnabled(false)
            .setService("UHLogs")
            .setLogcatLogsEnabled(false)
            .setRemoteSampleRate(100f)
            .setRemoteLogThreshold(Log.INFO)
            .setBundleWithTraceEnabled(false)
            .setBundleWithRumEnabled(true)
            .build()
            .also {
                it.addTag("serial number", attributes.serialNumber)
                currentDatadogTree = DatadogTree(it)
                Timber.plant(currentDatadogTree!!)
            }
    }

    private fun Datadog.configureAndInit(settings: DatadogSettings, attributes: Attributes) {
        with(settings) {
            val configuration = Configuration
                .Builder(
                    clientToken,
                    environmentName,
                    ApplicationName.VARIANT_NAME
                ).useSite(site)
                .setBatchSize(BatchSize.SMALL)
                .setUploadFrequency(UploadFrequency.FREQUENT)
                .setBackpressureStrategy(
                    BackPressureStrategy(
                        32,
                        { },
                        { },
                        BackPressureMitigation.DROP_OLDEST
                    )
                ).build()

            initialize(
                context = context,
                configuration = configuration,
                trackingConsent = TrackingConsent.GRANTED
            )
            setVerbosity(Log.INFO)
            setUserInfo(attributes.userId.toString(), "", attributes.userName)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    data class DatadogSettings(
        val clientToken: String,
        val applicationId: String,
        val rumSampleRate: Float,
        val sessionReplaySampleRate: Float,
        val site: DatadogSite,
        val enabled: Boolean,
        val haveDatadogSettingsChanged: Boolean = false,
    )

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    data class Attributes(
        val serialNumber: String,
        val imei: String,
        val iccid: String,
        val featureFlags: Map<String, Boolean>,
        val userId: String,
        val userName: String,
        val pid: String,
        val cid: String,
    )
}
