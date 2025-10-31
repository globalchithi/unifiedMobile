import com.android.build.api.dsl.ApplicationExtension
import com.vaxcare.unifiedhub.configureKotlinAndroid
import com.vaxcare.unifiedhub.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import java.io.File

/**
 * For the moment, we only have one application module that uses this plugin. Once we begin
 * implementing the MobileHub in this apk, this will be where any shared logic between the two
 * should live.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.android")

            val buildServerKey by lazy {
                val os = org.gradle.internal.os.OperatingSystem
                    .current()
                val path =
                    when {
                        os.isMacOsX || os.isLinux ->
                            "${
                                System.getProperty(
                                    "user.home"
                                )
                            }/vaxcare/Tools/VaxHubPlatformKeys/keystore.jks"

                        else -> "C:\\vaxcare\\Tools\\VaxHubPlatformKeys\\keystore.jks"
                    }

                (project.findProperty("VaxHubPlatformKeystoreFilePath") as? String)?.let {
                    if (!(File(it).exists())) path else it
                } ?: path
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                signingConfigs {
                    create("release") {
                        keyAlias = project.findProperty("platformAlias") as? String
                        keyPassword = project.findProperty("platformPassword") as? String
                        storeFile = file(buildServerKey)
                        storePassword = project.findProperty("storePassword") as? String
                    }
                }

                defaultConfig {
                    targetSdk = 35
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
                buildFeatures {
                    buildConfig = true
                }
                buildTypes {
                    debug {
                        applicationIdSuffix = ".dev"

                        buildConfigField(
                            "String",
                            "VAX_VHAPI_URL",
                            "\"${findProperty("vhapiUrlDev") as String}\""
                        )
                        isMinifyEnabled = false
                        enableUnitTestCoverage = true
                        enableAndroidTestCoverage = true
                    }
                    create("qa") {
                        applicationIdSuffix = ".qa"
                        buildConfigField(
                            "String",
                            "VAX_VHAPI_URL",
                            "\"${findProperty("vhapiUrlQa") as String}\""
                        )
                        isMinifyEnabled = false
                        isDebuggable = true
                        signingConfig = signingConfigs.getByName("debug")
                    }
                    create("staging") {
                        applicationIdSuffix = ".stg"
                        buildConfigField(
                            "String",
                            "VAX_VHAPI_URL",
                            "\"${findProperty("vhapiUrlStg") as String}\""
                        )
                        isMinifyEnabled = true
                        isDebuggable = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                        signingConfig = signingConfigs.getByName("release")
                    }
                    release {
                        buildConfigField(
                            "String",
                            "VAX_VHAPI_URL",
                            "\"${findProperty("vhapiUrlProd") as String}\""
                        )
                        isMinifyEnabled = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                        signingConfig = signingConfigs.getByName("release")
                    }
                }

                testOptions.animationsDisabled = true
            }

            dependencies {
                "implementation"(libs.findLibrary("androidx.activity").get())
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
