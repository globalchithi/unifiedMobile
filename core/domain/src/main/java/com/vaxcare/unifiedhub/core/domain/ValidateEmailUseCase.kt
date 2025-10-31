package com.vaxcare.unifiedhub.core.domain

import java.util.regex.Pattern
import javax.inject.Inject

/**
 * From official RFC 5322 standard email regex
 */
private val EMAIL_ADDRESS =
    Pattern.compile("^[A-Za-z0-9.'\\\" _%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\$")

/**
 * Checks if the string value is an email or not
 *
 * @return true if the string is an email address
 */
class ValidateEmailUseCase @Inject constructor() {
    operator fun invoke(emailString: String) = EMAIL_ADDRESS.matcher(emailString).matches()
}
