package com.vaxcare.unifiedhub.library.scanner.domain

import java.time.LocalDate

data class DriverLicense(
    val firstName: String = "",
    val middleName: String = "",
    val lastName: String = "",
    val gender: Gender = defaultGender,
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val zip: String = "",
    val licenseNo: String = "",
    val dateOfBirth: LocalDate = defaultDateOfBirth
) {
    companion object {
        private val defaultDateOfBirth = LocalDate.of(1900, 1, 1)
        private val defaultGender = Gender.MALE
    }

    enum class Gender { MALE, FEMALE }
}
