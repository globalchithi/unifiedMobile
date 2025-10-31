plugins {
    alias(libs.plugins.buildlogic.android.library)
    alias(libs.plugins.buildlogic.android.library.jacoco)
    alias(libs.plugins.buildlogic.hilt)
}

android {
    namespace = "com.vaxcare.unifiedhub.core.data"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.database)
    api(projects.core.common)
    api(projects.core.datastore)
    api(projects.core.model)
    api(projects.core.network)

    implementation(libs.androidx.datastore)
    implementation(libs.google.android.play.app.update)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)
    implementation(libs.okhttp3)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    ksp(libs.moshi.codegen)

    // FIXME: I don't love this, but AppRepository needs it in order to wipe the database
    implementation(libs.androidx.room.ktx)

    testImplementation(libs.kotlinx.coroutines.test)
}
