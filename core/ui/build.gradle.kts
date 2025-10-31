plugins {
    alias(libs.plugins.buildlogic.android.library)
    alias(libs.plugins.buildlogic.android.library.compose)
    alias(libs.plugins.buildlogic.android.library.jacoco)
}

android {
    namespace = "com.vaxcare.unifiedhub.core.ui"

    testFixtures { enable = true }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    api(projects.core.model)

    implementation(libs.timber)
    implementation(libs.lottie)

    testFixturesImplementation(libs.androidx.lifecycle.viewmodel.compose)
    testFixturesImplementation(libs.junit)
    testFixturesImplementation(libs.kotlinx.coroutines.android)
    testFixturesImplementation(libs.kotlinx.coroutines.test)
    testFixturesImplementation(libs.mockk)
    testFixturesImplementation(libs.turbine)
    testFixturesImplementation(projects.core.common)
}
