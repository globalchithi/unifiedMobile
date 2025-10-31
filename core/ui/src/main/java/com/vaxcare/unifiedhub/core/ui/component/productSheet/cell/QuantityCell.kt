package com.vaxcare.unifiedhub.core.ui.component.productSheet.cell

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type

@SuppressLint("DefaultLocale")
@Composable
private fun QuantityCell(
    initialQuantity: Int,
    modifier: Modifier = Modifier,
    adjustedQuantity: Int? = null,
    unitPrice: String? = null,
    alignment: Alignment.Horizontal = Alignment.End,
    testTag: String? = null
) {
    Column(
        horizontalAlignment = alignment,
        modifier = modifier
    ) {
        Text(
            text = buildAnnotatedString {
                if (adjustedQuantity != null) {
                    pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                    append("$initialQuantity")
                    pop()
                    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                    append(" $adjustedQuantity")
                } else {
                    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                    append("$initialQuantity")
                }
            },
            style = type.bodyTypeStyle.body3,
            modifier = Modifier.then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
        )

        if (unitPrice != null) {
            Text(
                text = unitPrice,
                style = type.bodyTypeStyle.body5,
                modifier = Modifier.padding(top = measurement.spacing.xSmall)
            )
        }
    }
}

@Composable
fun QuantityCell(
    initialQuantity: Int,
    modifier: Modifier = Modifier,
    adjustedQuantity: Int? = null,
    testTag: String? = null
) {
    QuantityCell(
        initialQuantity = initialQuantity,
        modifier = modifier,
        adjustedQuantity = adjustedQuantity,
        unitPrice = null,
        testTag = testTag
    )
}

@Composable
fun QuantityCell(
    initialQuantity: Int,
    unitPrice: String,
    modifier: Modifier = Modifier,
    adjustedQuantity: Int? = null,
    testTag: String? = null
) {
    QuantityCell(
        initialQuantity = initialQuantity,
        modifier = modifier,
        adjustedQuantity = adjustedQuantity,
        unitPrice = unitPrice,
        alignment = Alignment.End,
        testTag = testTag
    )
}
