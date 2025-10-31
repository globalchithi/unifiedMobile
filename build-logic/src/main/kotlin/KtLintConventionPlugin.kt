import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension

class KtLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jlleitschuh.gradle.ktlint")

            extensions.configure<KtlintExtension> {
                version.set("1.5.0")
                verbose.set(true)
                android.set(true)
                debug.set(true)
                outputToConsole.set(true)
                outputColorName.set("RED")
                additionalEditorconfig.set(
                    mapOf(
                        "ktlint_standard_no-wildcard-imports" to "disabled",
                        "ktlint_standard_backing-property-naming" to "disabled",
                        "ktlint_standard_trailing-comma-on-declaration-site" to "disabled",
                        "ktlint_standard_trailing-comma-on-call-site" to "disabled",
                        "ktlint_standard_context-receiver-wrapping" to "disabled",
                        "ktlint_standard_parameter-wrapping" to "disabled",
                        "ktlint_standard_property-wrapping" to "disabled",
                        "ktlint_standard_annotation" to "disabled",
                        "ktlint_standard_class-signature" to "disabled",
                        "ktlint_standard_string-template-indent" to "disabled",
                        "ktlint_standard_multiline-expression-wrapping" to "disabled",
                        "ktlint_function_signature_rule_force_multiline_when_parameter_count_greater_or_equal_than" to "3",
                        "max_line_length" to "120",
                        "ktlint_ignore_back_ticked_identifier" to "true",
                        "ktlint_function_naming_ignore_when_annotated_with" to "Composable"
                    )
                )
            }
        }
    }
}
