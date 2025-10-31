package com.vaxcare.unifiedhub.feature.transactions.ui.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.ui.Icons
import com.vaxcare.unifiedhub.feature.transactions.R

@Composable
fun ProductLot(productLotUi: ProductLotUi, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Top
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(Icons.presentationIcon(productLotUi.presentation)),
                contentDescription = productLotUi.presentation.name
            )
        }
        Spacer(modifier = Modifier.width(VaxCareTheme.measurement.spacing.xSmall))
        Column {
            Text(
                text = buildAnnotatedString {
                    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                    append(productLotUi.antigen)
                    pop()
                    append(" (${productLotUi.product})")
                },
                style = VaxCareTheme.type.bodyTypeStyle.body4,
                textDecoration = if (productLotUi.isDeleted) {
                    TextDecoration.LineThrough
                } else {
                    TextDecoration.None
                }
            )
            Spacer(modifier = Modifier.width(VaxCareTheme.measurement.spacing.small))
            Text(
                text = stringResource(R.string.product_lot_lot_number, productLotUi.lotNumber),
                style = VaxCareTheme.type.bodyTypeStyle.body5,
                textDecoration = if (productLotUi.isDeleted) {
                    TextDecoration.LineThrough
                } else {
                    TextDecoration.None
                }
            )
        }
    }
}

data class ProductLotUi(
    val antigen: String,
    val product: String,
    val presentation: Presentation,
    val lotNumber: String,
    val isDeleted: Boolean
)

@Preview(showBackground = true)
@Composable
private fun PreviewProductLot() {
    VaxCareTheme {
        ProductLot(
            productLotUi = ProductLotUi(
                antigen = "Hep A",
                product = "Havrix",
                presentation = Presentation.PREFILLED_SYRINGE,
                lotNumber = "MNYNG93",
                isDeleted = false
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDeletedProductLot() {
    VaxCareTheme {
        ProductLot(
            productLotUi = ProductLotUi(
                antigen = "Hep A",
                product = "Havrix",
                presentation = Presentation.PREFILLED_SYRINGE,
                lotNumber = "MNYNG93",
                isDeleted = true
            )
        )
    }
}
