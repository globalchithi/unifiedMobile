package com.vaxcare.unifiedhub.core.ui.ext

import android.graphics.Typeface
import android.text.Layout
import android.text.Spanned
import android.text.style.AlignmentSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration

/**
 * Helper extension to convert Spanned to Annotated screen.
 * This is needed because our current version of Compose does not have a way to handle HTML text
 * well. There is a version out there that handles this but we currently cannot implement it in the
 * stationary hub.
 */
fun Spanned.toAnnotatedString(): AnnotatedString =
    buildAnnotatedString {
        val spanned = this@toAnnotatedString
        append(spanned.toString())
        getSpans(0, spanned.length, kotlin.Any::class.java).forEach { span ->
            val start = getSpanStart(span)
            val end = getSpanEnd(span)
            when (span) {
                is StyleSpan -> when (span.style) {
                    Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                    Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                    Typeface.BOLD_ITALIC -> addStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        ),
                        start,
                        end
                    )
                }

                is UnderlineSpan -> addStyle(
                    SpanStyle(textDecoration = TextDecoration.Underline),
                    start,
                    end
                )

                is ForegroundColorSpan -> addStyle(
                    SpanStyle(color = Color(span.foregroundColor)),
                    start,
                    end
                )

                is URLSpan -> addStyle(
                    SpanStyle(
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline
                    ),
                    start,
                    end
                )

                is AlignmentSpan -> when (span.alignment) {
                    Layout.Alignment.ALIGN_CENTER -> addStyle(
                        ParagraphStyle(textAlign = TextAlign.Center),
                        start,
                        end
                    )

                    Layout.Alignment.ALIGN_NORMAL -> addStyle(
                        ParagraphStyle(textAlign = TextAlign.Start),
                        start,
                        end
                    )

                    Layout.Alignment.ALIGN_OPPOSITE -> addStyle(
                        ParagraphStyle(textAlign = TextAlign.End),
                        start,
                        end
                    )

                    else -> Unit
                }
            }
        }
    }
