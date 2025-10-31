package com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.ui.Icons

@Composable
@Deprecated("Per developer consensus, 'ProductInfoCell' has been reimplemented with more conventional patterns.")
// TODO:
//  - move this component into `ProductCellScope`
//  - remove `SubProductUi` as a parameter
//  - finally, remove the `@Deprecated` annotation.
internal fun SubProductLine(
    subProduct: SubProductUi,
    modifier: Modifier = Modifier,
    textDecoration: TextDecoration = TextDecoration.None
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = Icons.presentationIcon(subProduct.presentation)),
            contentDescription = subProduct.presentation.name,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(VaxCareTheme.measurement.spacing.xSmall))
        Text(
            text = buildAnnotatedString {
                pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                append("${subProduct.quantity} ")
                pop()
                append("(${subProduct.productName})")
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = VaxCareTheme.type.bodyTypeStyle.body4,
            textDecoration = textDecoration
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSubProductLine() {
    VaxCareTheme {
        SubProductLine(
            modifier = Modifier.padding(16.dp),
            subProduct = SubProductUi(
                presentation = Presentation.MULTI_DOSE_VIAL,
                quantity = 10,
                productName = "Recombovax HB"
            )
        )
    }
}

@Preview(showBackground = true, name = "Long Product Name")
@Composable
private fun PreviewSubProductLineWithLongProductName() {
    VaxCareTheme {
        SubProductLine(
            modifier = Modifier.padding(16.dp),
            subProduct = SubProductUi(
                presentation = Presentation.MULTI_DOSE_VIAL,
                quantity = 10,
                productName = "Blue, Moderna - Covid Vaccine For Ages 6 yrs to <12 yrs, MDV"
            )
        )
    }
}
