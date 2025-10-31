package com.vaxcare.unifiedhub.core.common.ext

/**
 * Remove the special characters and display only alphanumeric characters.
 * - No dashes, no hashes, no crashes or slashes, no spaces, whatever.
 *
 * @return the string only alphanumeric characters
 */
fun String.toAlphanumeric(): String = this.replace("[^a-zA-Z0-9]*".toRegex(), "")

fun String.isFlu() = this.uppercase() == "INFLUENZA"
