import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.vaxcare.unifiedhub.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplicationCompose") {
            id = libs.plugins.buildlogic.android.application.compose.get().pluginId
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplication") {
            id = libs.plugins.buildlogic.android.application.asProvider().get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationJacoco") {
            id = libs.plugins.buildlogic.android.application.jacoco.get().pluginId
            implementationClass = "JacocoApplicationConventionPlugin"
        }
        register("androidApplicationVersioning") {
            id = libs.plugins.buildlogic.android.application.versioning.get().pluginId
            implementationClass = "AndroidApplicationVersioningConventionPlugin"
        }
        register("androidFeature") {
            id = libs.plugins.buildlogic.android.feature.get().pluginId
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.buildlogic.android.library.asProvider().get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = libs.plugins.buildlogic.android.library.compose.get().pluginId
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibraryJacoco") {
            id = libs.plugins.buildlogic.android.library.jacoco.get().pluginId
            implementationClass = "JacocoLibraryConventionPlugin"
        }
        register("androidRoom") {
            id = libs.plugins.buildlogic.android.room.get().pluginId
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("androidTest") {
            id = libs.plugins.buildlogic.android.test.get().pluginId
            implementationClass = "AndroidTestConventionPlugin"
        }
        register("hilt") {
            id = libs.plugins.buildlogic.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }
        register("ktlint") {
            id = libs.plugins.buildlogic.ktlint.get().pluginId
            implementationClass = "KtLintConventionPlugin"
        }
        register("jvm") {
            id = libs.plugins.buildlogic.jvm.library.get().pluginId
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}
