package com.vaxcare.unifiedhub.core.ui.component.productSheet.cell

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type

@Composable
@Deprecated("Deprecated in favor of new, slot-based ProductCell.")
private fun BaseProductCell(
    prettyName: String,
    antigenName: String,
    presentation: Painter,
    modifier: Modifier = Modifier,
    topText: String? = null,
    bottomText: String? = null,
) {
    Column(modifier) {
        if (topText != null) {
            Text(
                text = topText,
                style = type.bodyTypeStyle.body5Italic,
                modifier = Modifier.padding(start = 32.dp, bottom = 8.dp)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = presentation,
                contentDescription = null,
                modifier = Modifier.width(24.dp).height(24.dp)
            )

            Text(
                text = buildAnnotatedString {
                    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                    append(antigenName)
                    pop()
                    append(" ($prettyName)")
                },
                style = type.bodyTypeStyle.body4,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (bottomText != null) {
            Text(
                text = bottomText,
                style = type.bodyTypeStyle.body5,
                modifier = Modifier.padding(start = 32.dp)
            )
        }
    }
}

@Composable
@Deprecated("Deprecated in favor of new, slot-based ProductCell.")
fun ProductCell(
    prettyName: String,
    antigenName: String,
    presentation: Painter,
    topText: String?,
    bottomText: String?,
    modifier: Modifier = Modifier,
) {
    BaseProductCell(prettyName, antigenName, presentation, modifier, topText, bottomText)
}

@Composable
@Deprecated("Deprecated in favor of new, slot-based ProductCell.")
fun ProductCell(
    prettyName: String,
    antigenName: String,
    presentation: Painter,
    modifier: Modifier = Modifier,
) {
    BaseProductCell(prettyName, antigenName, presentation, modifier)
}
