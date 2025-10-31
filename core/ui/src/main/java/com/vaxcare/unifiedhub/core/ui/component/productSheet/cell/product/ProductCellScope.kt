package com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.TestTags

/**
 * A custom Compose scope for elements intended specifically for use in [ProductCell]. Reference
 * [androidx.compose.foundation.layout.RowScope] for an official example.
 *
 * [ProductCellScope] restricts visibility of its members to scopes with [ProductCellScope] as an implicit receiver.
 * ```
 * @Composable
 * fun ExampleComposable() {
 *      // this scope is *not* received by ProductCellScope
 *      LotExpirationText(/*...*/)  // <-- Unresolved reference
 * }
 *
 * ---
 *
 * val exampleContent: @Composable ProductCellScope.() -> Unit = {
 *      // this scope is received by ProductCellScope
 *      LotExpirationText(/*...*/)  // <-- Valid
 * }
 */
object ProductCellScope {
    val LocalTextDecoration = compositionLocalOf { TextDecoration.None }

    /**
     * A common VXC pattern for displaying a product's antigen and pretty name.
     *
     * @return Example: "DTap (Daptacel)"
     */
    fun productInfoText(antigen: String, prettyName: String) =
        buildAnnotatedString {
            pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
            append(antigen)
            pop()
            append(" ($prettyName)")
        }

    @Composable
    internal fun ProductTitleLine(
        @DrawableRes leadingIcon: Int,
        modifier: Modifier = Modifier,
        title: @Composable () -> Unit,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.testTag(TestTags.ProductSheet.ProductCell.TITLE_LINE)
        ) {
            Icon(
                painter = painterResource(leadingIcon),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = measurement.spacing.xSmall)
                    .size(24.dp)
            )
            title()
        }
    }

    /**
     * The row with highest emphasis for a [ProductCell].
     *
     * @param leadingIcon Typically provided by [Icons.presentationIcon].
     * @param title An [AnnotatedString] displayed to the right of [leadingIcon].
     */
    @Composable
    fun ProductTitleLine(
        @DrawableRes leadingIcon: Int,
        title: AnnotatedString,
        modifier: Modifier = Modifier
    ) {
        ProductTitleLine(
            leadingIcon = leadingIcon,
            modifier = modifier
        ) {
            Text(
                text = title,
                style = type.bodyTypeStyle.body4,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = LocalTextDecoration.current
            )
        }
    }

    @Composable
    fun ProductTitleLine(
        @DrawableRes leadingIcon: Int,
        title: String,
        modifier: Modifier = Modifier,
        fontWeight: FontWeight = FontWeight.SemiBold,
        fontStyle: FontStyle = FontStyle.Normal,
    ) {
        ProductTitleLine(
            leadingIcon = leadingIcon,
            modifier = modifier
        ) {
            Text(
                text = title,
                style = type.bodyTypeStyle.body4,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = LocalTextDecoration.current,
            )
        }
    }

    @Composable
    fun LotExpirationText(
        isExpired: Boolean,
        expiration: String,
        modifier: Modifier = Modifier,
    ) {
        Text(
            text = buildAnnotatedString {
                with(color.onContainer) {
                    if (isExpired) {
                        pushStyle(SpanStyle(color = error))
                        append("EXPIRED ")
                        pop()
                    } else {
                        append("EXPIRATION ")
                    }

                    append(expiration.ifEmpty { "--" })
                }
            },
            style = type.bodyTypeStyle.body5,
            textDecoration = LocalTextDecoration.current,
            modifier = modifier
        )
    }

    @Composable
    fun ProductCellText(
        text: String,
        modifier: Modifier = Modifier,
        style: TextStyle = type.bodyTypeStyle.body5
    ) {
        Text(
            text = text,
            style = style,
            textDecoration = LocalTextDecoration.current,
            modifier = modifier
        )
    }
}
