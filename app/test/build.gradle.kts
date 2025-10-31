plugins {
    alias(libs.plugins.buildlogic.android.test)
    alias(libs.plugins.buildlogic.hilt)
}

android {
    namespace = "com.vaxcare.unifiedhub.app.test"
    targetProjectPath = ":app"

    flavorDimensions += "api"

    defaultConfig {
        /**
         * Make sure this has the right path to `CustomHiltTestRunner`; otherwise tests wont run.
         * Very Important Line ⭐️
         */
        testInstrumentationRunner = "com.vaxcare.unifiedhub.app.test.runner.CustomHiltTestRunner"
    }
}

dependencies {
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.compose.ui.test.junit4)
    implementation(libs.google.dagger.hilt.android.testing)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.okhttp3.mockwebserver)
    implementation(libs.timber)
    implementation(libs.androidx.work.testing)

    implementation(projects.app)
    implementation(projects.core.data)
    implementation(projects.core.common)
    implementation(projects.core.datastore)
    implementation(projects.library.vaxjob)
    implementation(projects.testing.data)
    implementation(projects.testing.network)
    implementation(projects.core.ui)

    ksp(libs.google.dagger.hilt.compiler)
}
