plugins {
    alias(libs.plugins.buildlogic.android.feature)
    alias(libs.plugins.buildlogic.android.library.compose)
    alias(libs.plugins.buildlogic.hilt)
}

android {
    namespace = "com.vaxcare.unifiedhub.feature.transactions"
}

dependencies {
    api(projects.core.common)
    api(projects.core.data)
    api(projects.core.domain)
    api(projects.core.designsystem)
    api(projects.core.model)
    api(projects.library.analytics)
    api(projects.library.scanner)
    api(projects.library.vaxjob)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.lottie)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
