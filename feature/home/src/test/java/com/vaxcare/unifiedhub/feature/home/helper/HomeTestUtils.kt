package com.vaxcare.unifiedhub.feature.home.helper

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.model.AppUpdateType
import com.vaxcare.unifiedhub.core.data.datasource.AppUpdateRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf

object HomeTestUtils {
    internal fun AppUpdateRepository.mockIsUpdateAvailable(value: Boolean) {
        every { appUpdateInfo } returns flowOf(
            mockk<AppUpdateInfo> {
                every { isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) } returns value
            }
        )
    }
}
