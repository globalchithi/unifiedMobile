package com.vaxcare.unifiedhub.core.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vaxcare.unifiedhub.core.ui.R
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
fun NoInternetDialog(
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    onGoToNetworkSettings: (() -> Unit)?,
    modifier: Modifier = Modifier,
    allowRetry: Boolean = true,
) {
    val primaryButton = if (allowRetry) {
        ButtonConfig(
            text = stringResource(DesignSystemR.string.try_again),
            onClick = onRetry,
        )
    } else {
        ButtonConfig(
            text = stringResource(R.string.network_settings),
            onClick = {
                onGoToNetworkSettings?.invoke()
            },
        )
    }
    VCBasicDialog(
        onDismissRequest = onDismiss,
        primaryButtonConfig = primaryButton,
        title = stringResource(R.string.no_internet_dialog_title),
        text = stringResource(R.string.no_internet_dialog_description),
        modifier = modifier,
        secondaryButtonConfig = ButtonConfig(
            text = stringResource(DesignSystemR.string.cancel),
            onClick = onDismiss
        )
    )
}
