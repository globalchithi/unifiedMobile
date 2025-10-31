import com.android.build.api.dsl.ApplicationExtension
import com.vaxcare.unifiedhub.Versioning
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationVersioningConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> {
                    defaultConfig {
                        versionCode = Versioning.getVersionCode(project)
                        versionName = Versioning.getVersionName(project)

                        logger.lifecycle("Applied versionCode: $versionCode")
                        logger.lifecycle("Applied versionName: $versionName")
                    }
                }
            }
        }
    }
}