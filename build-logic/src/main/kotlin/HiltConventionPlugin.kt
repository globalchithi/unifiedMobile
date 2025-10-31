import com.android.build.gradle.api.AndroidBasePlugin
import com.vaxcare.unifiedhub.getPluginId
import com.vaxcare.unifiedhub.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.devtools.ksp")

            dependencies {
                "ksp"(libs.findLibrary("google.dagger.hilt.compiler").get())
            }

            // Add support for Jvm Module, base on org.jetbrains.kotlin.jvm
            pluginManager.withPlugin(libs.findPlugin("kotlin-jvm").getPluginId()) {
                dependencies {
                    "implementation"(libs.findLibrary("google.dagger.hilt.core").get())
                }
            }

            /** Add support for Android modules, based on [AndroidBasePlugin] */
            pluginManager.withPlugin("com.android.base") {
                apply(plugin = "com.google.dagger.hilt.android")

                dependencies { "implementation"(libs.findLibrary("google.dagger.hilt.android").get()) }
            }
        }
    }
}
