package com.vaxcare.unifiedhub.feature.admin.ui.info

import com.vaxcare.unifiedhub.core.ui.arch.ActiveDialog
import com.vaxcare.unifiedhub.core.ui.arch.DialogKey
import com.vaxcare.unifiedhub.core.ui.arch.UiEvent
import com.vaxcare.unifiedhub.core.ui.arch.UiIntent
import com.vaxcare.unifiedhub.core.ui.arch.UiState
import java.time.LocalDateTime

sealed class AdminInfoIntent : UiIntent {
    object CloseScreen : AdminInfoIntent()

    object CloseValidateScanner : AdminInfoIntent()

    object OpenSourceLibrary : AdminInfoIntent()

    object OpenSystemConnectivity : AdminInfoIntent()

    object ValidateScannerLicenseClicked : AdminInfoIntent()
}

data class AdminInfoState(
    val apkVersion: String = "",
    val serialNumber: String = "",
    val isLoading: Boolean = true,
    val lastSyncedDatabaseRecords: LocalDateTime? = null,
    override val activeDialog: DialogKey? = null
) : UiState,
    ActiveDialog

sealed class AdminInfoDialog : DialogKey {
    object ValidateScannerLicense : AdminInfoDialog()
}

sealed class AdminInfoEvent : UiEvent {
    object NavigateBack : AdminInfoEvent()

    object NavigateToOpenSourceLibrary : AdminInfoEvent()

    object NavigateToSystemConnectivity : AdminInfoEvent()
}
