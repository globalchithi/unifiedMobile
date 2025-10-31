package com.vaxcare.unifiedhub.library.scanner.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.component.button.ElevatedIconButton
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode
import com.vaxcare.unifiedhub.library.scanner.domain.ScanType
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
fun ScannerWithSearch(
    scannerActive: Boolean,
    onBarcodeScanned: (ParsedBarcode) -> Unit,
    onLotSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = modifier,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Scanner(
                scanType = ScanType.DOSE,
                scannerActive = scannerActive,
                onBarcode = onBarcodeScanned,
                modifier = Modifier
                    .size(width = 296.dp, height = 184.dp)
                    .clip(RoundedCornerShape(measurement.radius.sheetHard))
            )
        }

        ElevatedIconButton(
            onClick = onLotSearchClick,
            modifier = Modifier
                .testTag(TestTags.ScannerWithSearch.SEARCH_BUTTON)
                .size(56.dp)
                .graphicsLayer {
                    translationX = 28.dp.toPx()
                }
        ) {
            Icon(
                painter = painterResource(DesignSystemR.drawable.ic_search),
                contentDescription = "Search Lot Number"
            )
        }
    }
}
