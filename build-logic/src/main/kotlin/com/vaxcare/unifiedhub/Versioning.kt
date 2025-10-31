package com.vaxcare.unifiedhub

import org.gradle.api.Project
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Versioning {

    // Property name expected from CI/CD for the build sequence number
    private const val CI_BUILD_NUMBER_PROPERTY = "buildNumber"

    // Default sequence number for local/debug builds
    private const val LOCAL_BUILD_SEQUENCE = 0

    // Maximum sequence number allowed per day (0-999)
    private const val MAX_SEQUENCE_PER_DAY = 999

    /**
     * Generates the versionName string in YYYY.MM.DD format.
     */
    fun getVersionName(project: Project): String {
        val date = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val baseName = date.format(formatter)

        // Append -SNAPSHOT for typical local/debug builds if no build number is provided
        return if (isReleaseBuild(project)) {
            baseName
        } else {
            "$baseName.SNAPSHOT"
        }
    }

    /**
     * Generates the versionCode based on the date and a sequence number.
     * Format: YYMMDDBBB (YearMonthDaySequence)
     * - YY: Last two digits of the year
     * - MM: Month (01-12)
     * - DD: Day (01-31)
     * - BBB: Build number (000-999), provided by CI for releases, defaults otherwise.
     * This allows up to 1000 builds per day and fits within Integer.MAX_VALUE.
     */
    fun getVersionCode(project: Project): Int {
        val date = LocalDate.now()
        val yy = date.year % 100 // Last two digits of year
        val mm = date.monthValue
        val dd = date.dayOfMonth

        val buildNumber = getBuildSequenceNumber(project)

        // Calculate versionCode: YY * 10_000_000 + MM * 100_000 + DD * 1000 + Sequence
        // Example: 2024-07-26, Build 5 -> 240726005
        val code = yy * 10_000_000 + mm * 100_000 + dd * 1000 + buildNumber

        // Basic validation (should not exceed Integer.MAX_VALUE with this scheme)
        require(code > 0) { "Calculated versionCode must be positive." }
        // require(code <= Integer.MAX_VALUE) { "Calculated versionCode $code exceeds Integer.MAX_VALUE" } // Implicitly checked by Int return type

        project.logger.lifecycle("Calculated versionCode: $code (Date: $yy-$mm-$dd, Sequence: $buildNumber)")

        return code
    }

    /**
     * Determines if this is a release build.
     * Logic: Checks if the buildNumber property is passed (typically by CI).
     */
    private fun isReleaseBuild(project: Project): Boolean {
        return project.hasProperty(CI_BUILD_NUMBER_PROPERTY) ||
                System.getenv("CI") == "true" ||  // Common CI environment variable for GitHub
                System.getenv("TF_BUILD") == "true" // Azure DevOps specific
    }

    /**
     * Gets the build sequence number.
     * Reads from Gradle property 'buildNumber' (passed via -PbuildNumber=...).
     * Defaults to LOCAL_BUILD_SEQUENCE if the property is not found or invalid.
     */
    private fun getBuildSequenceNumber(project: Project): Int {
        val buildNumberStr = project.findProperty(CI_BUILD_NUMBER_PROPERTY)?.toString()
        val sequence = buildNumberStr?.toIntOrNull() ?: LOCAL_BUILD_SEQUENCE

        require(sequence in 0..MAX_SEQUENCE_PER_DAY) {
            "Build sequence number ($sequence) must be between 0 and $MAX_SEQUENCE_PER_DAY."
        }

        // For release builds, ensure a valid number was actually provided
        if (isReleaseBuild(project) && buildNumberStr == null) {
            project.logger.warn(
                "Warning: Running a release-like build (e.g., CI=true) but '$CI_BUILD_NUMBER_PROPERTY' property not set. " +
                        "Using default sequence $LOCAL_BUILD_SEQUENCE for versionCode. Ensure your CI passes -P$CI_BUILD_NUMBER_PROPERTY=<value> for unique versionCodes."
            )
        } else if (buildNumberStr != null && buildNumberStr.toIntOrNull() == null) {
            project.logger.warn(
                "Warning: Invalid value '$buildNumberStr' passed for '$CI_BUILD_NUMBER_PROPERTY'. " +
                        "Using default sequence $LOCAL_BUILD_SEQUENCE for versionCode."
            )
        }

        return sequence
    }
}