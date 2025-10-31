import com.vaxcare.unifiedhub.configureKotlinAndroid
import com.vaxcare.unifiedhub.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidTestConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            apply(plugin = "com.android.test")
            apply(plugin = "org.jetbrains.kotlin.android")
            apply(plugin = "com.google.devtools.ksp")

            dependencies.add("ksp", libs.findLibrary("google.dagger.hilt.compiler").get())

            pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
                dependencies.add("implementation", libs.findLibrary("google.dagger.hilt.core").get())
            }

            pluginManager.withPlugin("com.android.base") {
                plugins.apply("com.google.dagger.hilt.android")
            }

            extensions.configure<com.android.build.api.dsl.TestExtension> {
                configureKotlinAndroid(this)

                buildFeatures {
                    buildConfig = true
                }
            }

            dependencies {
                "implementation"(libs.findLibrary("androidx.test.espresso.core").get())
                "implementation"(libs.findLibrary("androidx.test.junit").get())
                "implementation"(libs.findLibrary("junit").get())
            }
        }
    }
}