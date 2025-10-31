package com.vaxcare.unifiedhub.feature.transactions.ui.common.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.R
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.component.button.ElevatedIconButton
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode
import com.vaxcare.unifiedhub.library.scanner.domain.ScanType
import com.vaxcare.unifiedhub.library.scanner.ui.Scanner

@Composable
fun ScannerWithSearch(
    scannerActive: Boolean,
    onBarcodeScanned: (ParsedBarcode) -> Unit,
    onLotSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.widthIn(min = 328.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Scanner(
                modifier = Modifier
                    .size(width = 296.dp, height = 184.dp)
                    .clip(RoundedCornerShape(measurement.radius.sheetHard)),
                scanType = ScanType.DOSE,
                scannerActive = scannerActive,
                onBarcode = onBarcodeScanned
            )
        }

        ElevatedIconButton(
            onClick = onLotSearchClick,
            iconDrawRes = R.drawable.ic_search,
            modifier = Modifier
                .size(56.dp)
                .graphicsLayer {
                    translationX = -8.dp.toPx()
                }.align(Alignment.CenterEnd)
                .testTag(TestTags.ScannerWithSearch.SEARCH_BUTTON),
            contentDescription = "Search Lot Number"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewScannerWithSearch() {
    VaxCareTheme {
        ScannerWithSearch(scannerActive = true, onBarcodeScanned = {}, onLotSearchClick = {})
    }
}
