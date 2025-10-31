package com.vaxcare.unifiedhub.core.data.datasource

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.vaxcare.unifiedhub.core.common.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppUpdateRepository @Inject constructor(
    @ApplicationScope private val applicationScope: CoroutineScope,
) {
    var manager: AppUpdateManager? = null

    private val _appUpdateInfo = MutableStateFlow<AppUpdateInfo?>(null)
    val appUpdateInfo = _appUpdateInfo.asStateFlow().filterNotNull()

    fun initializeManager(context: Context) {
        manager = AppUpdateManagerFactory.create(context)
    }

    fun checkAppUpdateInfo() {
        manager?.appUpdateInfo?.addOnSuccessListener(::emitUpdate)
    }

    private fun emitUpdate(value: AppUpdateInfo) {
        applicationScope.launch {
            _appUpdateInfo.emit(value)
        }
    }
}
