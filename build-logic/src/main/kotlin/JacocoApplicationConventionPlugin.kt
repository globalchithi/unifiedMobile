import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.vaxcare.unifiedhub.configureJacoco
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

class JacocoApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "jacoco")

        val android = extensions.getByType<ApplicationExtension>()
        android.buildTypes.configureEach {
            if (name == "debug") {
                enableUnitTestCoverage = true
//                TODO: enableAndroidTestCoverage = true
            }
        }

        configureJacoco(extensions.getByType<ApplicationAndroidComponentsExtension>())
    }
}
