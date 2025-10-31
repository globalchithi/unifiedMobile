package com.vaxcare.unifiedhub.feature.admin.ui.info.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.component.VCContentDialog
import com.vaxcare.unifiedhub.core.ui.component.button.PrimaryButton
import com.vaxcare.unifiedhub.feature.admin.R
import com.vaxcare.unifiedhub.library.scanner.domain.ScanType
import com.vaxcare.unifiedhub.library.scanner.ui.Scanner
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemResource

@Composable
fun ValidateScannerDialog(modifier: Modifier = Modifier, onDismiss: () -> Unit) {
    VCContentDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(measurement.radius.cardMedium),
        modifier = modifier.size(640.dp, 480.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .width(640.dp)
                    .height(72.dp)
                    .padding(measurement.spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    textAlign = TextAlign.End,
                    style = type.bodyTypeStyle.body3Bold,
                    text = stringResource(R.string.admin_info_validate_scanner_license)
                )
            }
            Scanner(
                modifier = Modifier
                    .width(640.dp)
                    .height(332.dp),
                scanType = ScanType.DOSE,
                invalidLicenseContent = {
                    Box(
                        Modifier
                            .background(color.container.disabled)
                            .width(640.dp)
                            .height(332.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            style = type.bodyTypeStyle.body4,
                            text = stringResource(R.string.admin_info_scanner_license_not_valid)
                        )
                    }
                },
                onBarcode = {
                    // Do nothing/Use for testing
                }
            )
            Row(
                modifier = Modifier
                    .width(640.dp)
                    .height(76.dp)
                    .padding(measurement.spacing.small),
                horizontalArrangement = Arrangement.End,
            ) {
                PrimaryButton(
                    modifier = modifier.widthIn(min = 120.dp),
                    onClick = onDismiss,
                    text = stringResource(DesignSystemResource.string.ok)
                )
            }
        }
    }
}

@Preview(heightDp = 1182, widthDp = 738, showBackground = true)
@Composable
private fun PreviewValidateScannerDialog() {
    VaxCareTheme {
        ValidateScannerDialog { }
    }
}
