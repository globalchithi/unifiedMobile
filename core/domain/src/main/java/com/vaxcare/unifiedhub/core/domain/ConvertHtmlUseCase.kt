package com.vaxcare.unifiedhub.core.domain

import androidx.core.text.HtmlCompat
import javax.inject.Inject

class ConvertHtmlUseCase @Inject constructor() {
    operator fun invoke(html: String) = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
}
