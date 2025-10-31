import com.android.build.gradle.LibraryExtension
import com.vaxcare.unifiedhub.Versioning
import com.vaxcare.unifiedhub.configureKotlinAndroid
import com.vaxcare.unifiedhub.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.extra

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 35
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                testOptions.animationsDisabled = true

                buildFeatures {
                    buildConfig = true
                }

                buildTypes {
                    all {
                        buildConfigField(
                            "int",
                            "VERSION_CODE",
                            Versioning.getVersionCode(project).toString()
                        )
                        buildConfigField(
                            "String",
                            "VERSION_NAME",
                            "\"${Versioning.getVersionName(project)}\""
                        )
                    }

                    debug {
                        buildConfigField(
                            "String",
                            "VAX_VHAPI_URL",
                            "\"${findProperty("vhapiUrlDev") as String}\""
                        )
                        buildConfigField(
                            "String",
                            "BUILD_VARIANT",
                            "\"DEV_ENV_NAME\""
                        )
                        isMinifyEnabled = false
                        enableUnitTestCoverage = true
                        enableAndroidTestCoverage = true
                    }
                    create("qa") {
                        buildConfigField(
                            "String",
                            "VAX_VHAPI_URL",
                            "\"${findProperty("vhapiUrlQa") as String}\""
                        )
                        buildConfigField(
                            "String",
                            "BUILD_VARIANT",
                            "\"qa\""
                        )
                    }
                    create("staging") {
                        buildConfigField(
                            "String",
                            "VAX_VHAPI_URL",
                            "\"${findProperty("vhapiUrlStg") as String}\""
                        )
                        buildConfigField(
                            "String",
                            "BUILD_VARIANT",
                            "\"staging\""
                        )
                    }
                    release {
                        buildConfigField(
                            "String",
                            "VAX_VHAPI_URL",
                            "\"${findProperty("vhapiUrlProd") as String}\""
                        )
                        buildConfigField(
                            "String",
                            "BUILD_VARIANT",
                            "\"release\""
                        )
                    }
                }
            }

            dependencies {
                "implementation"(libs.findLibrary("timber").get())
                "testImplementation"(libs.findLibrary("junit").get())
                "testImplementation"(libs.findLibrary("mockk").get())
                "testImplementation"(libs.findLibrary("androidx.test.rules").get())
                "testImplementation"(libs.findLibrary("androidx.test.core").get())
                "androidTestImplementation"(libs.findLibrary("androidx.test.junit").get())
            }
        }
    }
}
