package com.vaxcare.unifiedhub.core.domain

import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class SafeExpirationDateUseCase @Inject constructor() {
    operator fun invoke(expiration: LocalDate?): LocalDateTime =
        (expiration ?: LocalDate.of(1970, 1, 1))
            .atStartOfDay()
}
