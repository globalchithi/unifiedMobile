package com.vaxcare.unifiedhub.permissions

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.datastore.datasource.DevicePreferenceDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PermissionsViewModel @Inject constructor(
    private val devicePreferenceDataSource: DevicePreferenceDataSource
) : ViewModel() {
    companion object {
        private const val MOBILE_BRIDGE_APP_PACKAGE = "com.vaxcare.mobilebridge"
        private const val URL_FMT = "content://%s/datapoints"
        private const val DEVICE_INFO_ID = "_id"
        private const val DEVICE_INFO_SERIAL = "serial"
        private const val DEVICE_INFO_IMEI = "imei"
        private const val DEVICE_INFO_ICCID = "iccid"
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }

    private val deviceInfoContentUriString =
        URL_FMT.format("$MOBILE_BRIDGE_APP_PACKAGE.${Build.MODEL.lowercase()}")

    sealed class PermissionsState {
        data object Loading : PermissionsState()

        data class NeedsPermission(
            val permission: String
        ) : PermissionsState()

        data object PermissionsGranted : PermissionsState()

        data object DeviceInfoUnavailable : PermissionsState()

        data object DeviceInfoCaptured : PermissionsState()
    }

    private val _uiState = MutableStateFlow<PermissionsState>(PermissionsState.Loading)
    val uiState: StateFlow<PermissionsState> = _uiState.asStateFlow()

    fun checkPermissions(baseContext: Context) {
        _uiState.tryEmit(PermissionsState.Loading)
        val currentPermissions = REQUIRED_PERMISSIONS.map { permission ->
            PermissionStatus(
                permission = permission,
                status = ContextCompat.checkSelfPermission(baseContext, permission)
            )
        }

        val state =
            currentPermissions.firstOrNull { it.status == PackageManager.PERMISSION_DENIED }?.let {
                PermissionsState.NeedsPermission(it.permission)
            } ?: PermissionsState.PermissionsGranted

        _uiState.tryEmit(state)
    }

    fun getDeviceInfoFromContentResolver(contentResolver: ContentResolver) =
        viewModelScope.launch {
            _uiState.tryEmit(PermissionsState.Loading)
            devicePreferenceDataSource.serialNumber.firstOrNull()?.let { existingSerial ->
                Timber.d("existing serial: $existingSerial")
                if (existingSerial.isBlank()) {
                    obtainDeviceDetailsFromContentResolver(contentResolver)
                } else {
                    _uiState.tryEmit(PermissionsState.DeviceInfoCaptured)
                }
            } ?: run { obtainDeviceDetailsFromContentResolver(contentResolver) }
        }

    private suspend fun obtainDeviceDetailsFromContentResolver(contentResolver: ContentResolver) {
        val deviceInfo = getDeviceInfoFromBridge(contentResolver)
        if (deviceInfo.serial.isBlank()) {
            _uiState.tryEmit(PermissionsState.DeviceInfoUnavailable)
        } else {
            devicePreferenceDataSource.setSerialNumber(deviceInfo.serial)
            devicePreferenceDataSource.setImei(deviceInfo.imei)
            devicePreferenceDataSource.setIccid(deviceInfo.iccid)
            Timber.d("getDeviceInfoFromContentResolver:${deviceInfo.serial} | ${deviceInfo.imei} | ${deviceInfo.iccid}")
            _uiState.tryEmit(PermissionsState.DeviceInfoCaptured)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getDeviceInfoFromBridge(contentResolver: ContentResolver): DeviceInfo =
        with(contentResolver) {
            var cursor: Cursor? = null
            var serial = ""
            var imei = ""
            var iccid = ""
            try {
                cursor =
                    query(Uri.parse(deviceInfoContentUriString), null, null, null, DEVICE_INFO_ID)

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        serial = cursor.getString(
                            cursor.getColumnIndex(DEVICE_INFO_SERIAL).coerceAtLeast(0)
                        )
                        imei = cursor.getString(
                            cursor.getColumnIndex(DEVICE_INFO_IMEI).coerceAtLeast(0)
                        )
                        iccid = cursor.getString(
                            cursor.getColumnIndex(DEVICE_INFO_ICCID).coerceAtLeast(0)
                        )
                    } while (cursor.moveToNext())
                }
            } catch (e: Exception) {
                Timber.e(e, "Bridge app failed to return data in cursor")
            } finally {
                cursor?.close()
            }
            DeviceInfo(serial, imei, iccid)
        }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    data class DeviceInfo(
        val serial: String,
        val imei: String,
        val iccid: String
    )

    private data class PermissionStatus(
        val permission: String,
        var status: Int
    )
}
