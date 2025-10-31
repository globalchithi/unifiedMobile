plugins {
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.datadog)
    alias(libs.plugins.google.gms)

    alias(libs.plugins.buildlogic.android.application)
    alias(libs.plugins.buildlogic.android.application.compose)
    alias(libs.plugins.buildlogic.android.application.jacoco)
    alias(libs.plugins.buildlogic.android.application.versioning)
    alias(libs.plugins.buildlogic.hilt)
    id("com.google.android.gms.oss-licenses-plugin")
}

datadog {
    site = "US3"
}

android {
    namespace = "com.vaxcare.unifiedhub"

    defaultConfig {
        applicationId = "com.vaxcare.unifiedhub"
    }
    lint {
        abortOnError = true
        fatal.add("HardcodedText")
        disable.addAll(
            listOf(
                "UnusedResources",
                "RtlSymmetry",
                "ContentDescription",
                "UnsafeExperimentalUsageError",
                "UnsafeExperimentalUsageWarning",
                "PrivateApi",
                "LabelFor",
                "Autofill",
                "IconDensities",
                "IconMissingDensityFolder",
                "UselessParent",
                "Overdraw"
            )
        )
        baseline = File("lint-baseline.xml")
    }

    packaging {
        resources {
            excludes += ("META-INF/LICENSE.md")
            excludes += ("META-INF/LICENSE-notice.md")
        }
    }
}

dependencies {
    implementation(projects.library.analytics)
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.common)
    implementation(projects.core.ui)
    implementation(projects.library.scanner)
    implementation(projects.library.vaxjob)
    implementation(projects.feature.admin)
    implementation(projects.feature.home)
    implementation(projects.feature.pinin)
    implementation(projects.feature.transactions)

    implementation(libs.moshi)

    implementation(libs.retrofit)
    implementation(projects.core.domain)
    implementation(libs.androidx.test.core)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.kotlinx.serialization.json)

    // play store
    implementation(libs.google.android.play.app.update)

    // compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // timber
    implementation(libs.timber)
    implementation(libs.datadog.compose)
    implementation(libs.datadog.rum)
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.messaging)
    implementation(libs.mixpanel.android)

    // hilt
    implementation(libs.google.dagger.hilt.android)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.androidx.hilt.compiler)
    ksp(libs.google.dagger.hilt.compiler)

    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)

    debugImplementation(libs.google.dagger.hilt.android.testing)

    implementation(files("$rootDir/libs/CortexDecoderLibrary.aar"))
}
