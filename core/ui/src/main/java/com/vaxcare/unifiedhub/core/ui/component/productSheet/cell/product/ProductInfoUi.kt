package com.vaxcare.unifiedhub.core.ui.component.productSheet.cell.product

import androidx.compose.ui.graphics.Color
import com.vaxcare.unifiedhub.core.model.product.Presentation

/**
 * Represents a state of a subtitle line
 * e.g., "LOT # AOZ67EF", "EXPIRATION 06/25/2025", "Expired Doses"
 */
@Deprecated("Per developer consensus, use of the 'intermediate UI data class' pattern is no longer supported.")
data class SubtitleLine(
    val text: String,
    val color: Color? = null, // For cases like "Expired Doses"
    val isBold: Boolean = false
)

@Deprecated("Per developer consensus, use of the 'intermediate UI data class' pattern is no longer supported.")
data class ProductInfoUi(
    val presentation: Presentation? = null,
    val topText: String? = null,
    val mainTextBold: String,
    val mainTextRegular: String? = null,
    val subtitleLines: List<SubtitleLine> = emptyList(),
    val isDeleted: Boolean = false,
)

/**
 * Represents a single line within a MultiProductCell,
 * e.g., "Icon 10 (Recombovax HB)"
 */
@Deprecated("Per developer consensus, use of the 'intermediate UI data class' pattern is no longer supported.")
data class SubProductUi(
    val presentation: Presentation,
    val quantity: Int,
    val productName: String
)

/**
 * Represents the entire state for the Multi-Product variant cell.
 */
@Deprecated("Per developer consensus, use of the 'intermediate UI data class' pattern is no longer supported.")
data class MultiProductUi(
    val antigen: String,
    val subProducts: List<SubProductUi>,
    val statusText: SubtitleLine? = null,
    val isDeleted: Boolean = false
)
