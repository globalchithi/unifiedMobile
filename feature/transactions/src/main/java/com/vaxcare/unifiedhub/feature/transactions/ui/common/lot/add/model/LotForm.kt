package com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model

import com.vaxcare.unifiedhub.core.common.ext.isToday
import java.time.LocalDate

data class LotForm(
    val lotNumber: String? = null,
    val antigen: String? = null,
    val product: ProductUI? = null,
    val presentation: PresentationUI? = null,
    val expirationDate: LocalDate? = null,
    val isPreSelected: Boolean = false
) {
    val isValidExpiration: Boolean
        get() = expirationDate?.isToday() == true ||
            expirationDate?.isAfter(LocalDate.now()) == true

    val isComplete
        get() =
            antigen != null &&
                product != null &&
                presentation != null &&
                expirationDate != null &&
                isValidExpiration
}
