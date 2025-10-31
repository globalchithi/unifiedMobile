plugins {
    alias(libs.plugins.buildlogic.jvm.library)
    alias(libs.plugins.buildlogic.hilt)
}
dependencies {
    implementation(libs.kotlinx.coroutines.android)
}
