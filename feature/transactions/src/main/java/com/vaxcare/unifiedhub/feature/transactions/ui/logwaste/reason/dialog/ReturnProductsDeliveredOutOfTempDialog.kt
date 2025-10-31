package com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vaxcare.unifiedhub.core.ui.component.ButtonConfig
import com.vaxcare.unifiedhub.core.ui.component.VCBasicDialog
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
fun ReturnProductsDeliveredOutOfTempDialog(onCancel: () -> Unit, onReturnProducts: () -> Unit) {
    VCBasicDialog(
        onDismissRequest = onCancel,
        title = stringResource(R.string.log_waste_return_products_delivered_out_of_temp),
        text = stringResource(R.string.log_waste_return_products_delivered_out_of_temp_body),
        primaryButtonConfig = ButtonConfig(
            text = stringResource(R.string.log_waste_return_products),
            onClick = onReturnProducts
        ),
        secondaryButtonConfig = ButtonConfig(
            text = stringResource(DesignSystemR.string.cancel),
            onClick = onCancel
        ),
    )
}
