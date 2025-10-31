package com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.ui.Icons
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product.ProductCellScope.LocalTextDecoration

/**
 * A slot-based component API for product data displayed in a list or sheet.
 *
 * Important references for implementation:
 * - [ProductCellScope]
 * - [ProductCellScope.ProductCellText]
 * - [ProductCellScope.LotExpirationText]
 *
 * For a full usage sample, reference the previews in this file.
 *
 * @param titleRow A slot for the [ProductCell] title row.
 * @param topContent An optional slot for content above the title row (e.g., "Update Count").
 * @param bottomContent An optional slot for content below the title row (e.g., Lot Info, Expiration, etc.).
 * @param strikeText Whether or not to apply [TextDecoration.LineThrough] to contained text.
 * @param removeDefaultPadding *MOST* usages of [ProductCell] need padding to offset text in the optional slots.
 *   This horizontally aligns the [topContent] and [bottomContent] with the text in [titleRow]. Set [removeDefaultPadding]
 *   to 'true' to disable this padding.
 */
@Composable
fun ProductCell(
    titleRow: @Composable ProductCellScope.() -> Unit,
    modifier: Modifier = Modifier,
    topContent: @Composable (ProductCellScope.() -> Unit)? = null,
    bottomContent: @Composable (ProductCellScope.() -> Unit)? = null,
    strikeText: Boolean = false,
    removeDefaultPadding: Boolean = false,
) {
    val localDecoration = if (strikeText) TextDecoration.LineThrough else TextDecoration.None
    val iconOffsetPadding = if (removeDefaultPadding) 0.dp else 32.dp

    CompositionLocalProvider(LocalTextDecoration provides localDecoration) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier.testTag(TestTags.ProductSheet.ProductCell.CONTAINER)
        ) {
            if (topContent != null) {
                Box(
                    content = { ProductCellScope.topContent() },
                    modifier = Modifier
                        .testTag(TestTags.ProductSheet.ProductCell.TOP_CONTENT)
                        .padding(
                            start = iconOffsetPadding,
                            bottom = measurement.spacing.xSmall
                        )
                )
            }
            ProductCellScope.titleRow()
            if (bottomContent != null) {
                Box(
                    content = { ProductCellScope.bottomContent() },
                    modifier = Modifier
                        .testTag(TestTags.ProductSheet.ProductCell.BOTTOM_CONTENT)
                        .padding(
                            start = iconOffsetPadding,
                            top = measurement.spacing.xSmall
                        )
                )
            }
        }
    }
}

// region Deprecated version ======================================================
// ================================================================================

/**
 * A reusable base layout component that organizes product information.
 * It uses the "Slot API" pattern to allow for complete customization of the text content,
 * while it only manages the layout (icon on the left, text column on the right).
 *
 * @param presentation An optional presentation type to display the product icon.
 * @param modifier The Modifier to be applied to the component.
 * @param topContent An optional slot to display content above the main line (e.g., "Update Count").
 * @param mainContent The main content slot for the product/antigen name.
 * @param subtitleContent An optional slot for secondary content (e.g., Lot, Expiration, etc.).
 */
@Deprecated("Per developer consensus, 'ProductInfoCell' has been reimplemented with more conventional patterns.")
@Composable
fun ProductInfoCell(
    modifier: Modifier = Modifier,
    presentation: Presentation? = null,
    topContent: @Composable (() -> Unit)? = null,
    mainContent: @Composable () -> Unit,
    subtitleContent: @Composable (() -> Unit)? = null
) {
    Column(modifier) {
        if (topContent != null) {
            Box(
                modifier = Modifier.padding(bottom = measurement.spacing.xSmall),
                content = { topContent() }
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (presentation != null) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Icons.presentationIcon(presentation)),
                    contentDescription = presentation.name
                )
                Spacer(modifier = Modifier.width(measurement.spacing.xSmall))
            }
            mainContent()
        }
        if (subtitleContent != null) {
            Box(
                modifier = Modifier.padding(top = measurement.spacing.xSmall),
                content = { subtitleContent() }
            )
        }
    }
}

@Deprecated("Per developer consensus, 'ProductInfoCell' has been reimplemented with more conventional patterns.")
@Composable
fun ProductInfo(info: ProductInfoUi, modifier: Modifier = Modifier) {
    val textDecoration = if (info.isDeleted) TextDecoration.LineThrough else TextDecoration.None

    ProductInfoCell(
        presentation = info.presentation,
        modifier = modifier,
        topContent = info.topText?.let {
            {
                Text(
                    text = it,
                    style = type.bodyTypeStyle.body5Italic,
                    color = color.onContainer.onContainerPrimary,
                    textDecoration = textDecoration,
                    modifier = Modifier.padding(start = measurement.spacing.large)
                )
            }
        },
        mainContent = {
            Text(
                text = buildAnnotatedString {
                    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                    append(info.mainTextBold)
                    pop()
                    info.mainTextRegular?.let { append(" $it") }
                },
                style = type.bodyTypeStyle.body4,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = textDecoration
            )
        },
        subtitleContent = if (info.subtitleLines.isNotEmpty()) {
            {
                Column(Modifier.padding(start = measurement.spacing.large)) {
                    info.subtitleLines.forEach { line ->
                        Text(
                            text = line.text,
                            style = if (line.isBold) type.bodyTypeStyle.body5Bold else type.bodyTypeStyle.body5,
                            color = line.color ?: VaxCareTheme.color.onContainer.onContainerPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = textDecoration
                        )
                    }
                }
            }
        } else {
            null
        }
    )
}

// endregion

@Preview(showBackground = true)
@Composable
private fun `Product - Lot Info - Expiration`() {
    VaxCareTheme {
        ProductCell(
            strikeText = true,
            titleRow = {
                ProductTitleLine(
                    leadingIcon = Icons.presentationIcon(Presentation.PREFILLED_SYRINGE),
                    title = productInfoText("DTap", "Daptacel"),
                )
            },
            bottomContent = {
                Column {
                    ProductCellText(
                        text = "LOT # VXC1234",
                        modifier = Modifier.padding(bottom = measurement.spacing.xSmall)
                    )

                    LotExpirationText(
                        isExpired = true,
                        expiration = "06/06/2025"
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun `Product - Lot Info - Status`() {
    VaxCareTheme {
        ProductCell(
            titleRow = {
                ProductTitleLine(
                    leadingIcon = Icons.presentationIcon(Presentation.PREFILLED_SYRINGE),
                    title = productInfoText("DTap", "Daptacel"),
                )
            },
            topContent = {
                Text(
                    text = "Update Count",
                    style = type.bodyTypeStyle.body5Italic,
                    textDecoration = LocalTextDecoration.current
                )
            },
            bottomContent = {
                ProductCellText(
                    text = "LOT # VXC1234",
                    modifier = Modifier.padding(bottom = measurement.spacing.xSmall)
                )
            },
            modifier = Modifier.background(color.container.warningContainer)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun `Lot Info - Expiration`() {
    VaxCareTheme {
        ProductCell(
            titleRow = {
                ProductTitleLine(
                    leadingIcon = Icons.presentationIcon(Presentation.PREFILLED_SYRINGE),
                    title = buildAnnotatedString {
                        pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                        append("VXC1234")
                    }
                )
            },
            bottomContent = {
                LotExpirationText(
                    isExpired = true,
                    expiration = "06/06/2025"
                )
            }
        )
    }
}
