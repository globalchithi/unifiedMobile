package com.vaxcare.unifiedhub.feature.transactions.ui.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.ui.component.ErrorMessage
import com.vaxcare.unifiedhub.feature.transactions.R
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode
import com.vaxcare.unifiedhub.library.scanner.ui.ScannerWithSearch

@Composable
fun PortraitScannerSection(
    headerText: String,
    scannerActive: Boolean,
    onBarcodeScanned: (barcode: ParsedBarcode) -> Unit,
    onLotSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
    invalidScan: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(VaxCareTheme.measurement.spacing.xLarge))
        Text(
            modifier = Modifier.width(274.dp),
            text = headerText,
            style = VaxCareTheme.type.displayTypeStyle.display3,
            color = VaxCareTheme.color.onContainer.disabled,
        )
        Spacer(Modifier.width(VaxCareTheme.measurement.spacing.xLarge))
        Column {
            ScannerWithSearch(
                scannerActive = scannerActive,
                onBarcodeScanned = onBarcodeScanned,
                onLotSearchClick = onLotSearchClick,
            )
            ErrorMessage(
                isError = invalidScan,
                text = R.string.scanner_section_unknown_product,
                textStyle = VaxCareTheme.type.bodyTypeStyle.body5,
                modifier = Modifier.padding(top = VaxCareTheme.measurement.spacing.xSmall)
            )
        }
    }
}

@Composable
fun LandscapeScannerSection(
    headerText: String,
    scannerActive: Boolean,
    onBarcodeScanned: (barcode: ParsedBarcode) -> Unit,
    onLotSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
    invalidScan: Boolean = false
) {
    Column(modifier = modifier) {
        ScannerWithSearch(
            scannerActive = scannerActive,
            onBarcodeScanned = onBarcodeScanned,
            onLotSearchClick = onLotSearchClick,
        )
        Spacer(Modifier.height(VaxCareTheme.measurement.spacing.small))
        ErrorMessage(
            isError = invalidScan,
            text = R.string.scanner_section_unknown_product,
            textStyle = VaxCareTheme.type.bodyTypeStyle.body5,
            modifier = Modifier.padding(bottom = VaxCareTheme.measurement.spacing.medium)
        )
        Text(
            modifier = Modifier.wrapContentSize(),
            text = headerText,
            style = VaxCareTheme.type.displayTypeStyle.display3,
            color = VaxCareTheme.color.onContainer.disabled,
            textAlign = TextAlign.Start
        )
    }
}

@Preview(showBackground = true, widthDp = 738)
@Composable
private fun PreviewPortraitScannerSection() {
    VaxCareTheme {
        PortraitScannerSection(
            headerText = stringResource(R.string.scan_each_product),
            scannerActive = true,
            onBarcodeScanned = {},
            onLotSearchClick = {},
            invalidScan = true,
        )
    }
}

@Preview(showBackground = true, heightDp = 640, widthDp = 332)
@Composable
private fun PreviewLandscapeScannerSection() {
    VaxCareTheme {
        LandscapeScannerSection(
            headerText = stringResource(R.string.scan_each_product),
            scannerActive = true,
            onBarcodeScanned = {},
            onLotSearchClick = {},
            invalidScan = true,
        )
    }
}
