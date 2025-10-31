package com.vaxcare.unifiedhub

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.api.variant.SourceDirectories
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

val coverageExclusions = listOf(
    // Android infra
    "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*",

    // Test classes (if they get picked up)
    "**/*Test*.*", "**/*AndroidTest*.*",

    // Hilt / Dagger
    "**/*_Hilt*.*", "**/Hilt_*.*", "**/*Dagger*.*", "**/*MembersInjector*.*",
    "**/*_Factory.*", "**/*_Provide*Factory.*", "**/*_ComponentTreeDeps*.*",

    // Kotlin/Compose
    "**/*ComposableSingletons*.*", "**/*Preview*.*", "**/*Kt$*.*",
    "**/DevTools*.*",
    "**/*NavHost*.*",
    "**/feature/**/ui/**/*Kt.class", // Any feature composable generated class
    "**/core/designsystem/**/*.*", // Anything from design system
    "**/core/ui/**/*.*", // Anything from core ui

    // Room generated
    "**/*_Impl*.*",

    // Navigation (Safe Args)
    "**/*Directions*.*", "**/*Args*.*",

    // Serialization
    "**/*JsonAdapter.*",

    // Network DTOs
    "**/core/network/model/**/*DTO.*",

    // Room plain data objects
    "**/core/database/model/**/*Entity.*",

    // Multi-release JAR classes
    "**/META-INF/versions/**/*",

    // Coroutines internal debugging class
    "**/kotlin/coroutines/jvm/internal/DebugProbesKt.class"
)

private fun SourceDirectories.Flat?.toFilePaths(project: Project): Provider<List<String>> =
    this?.all?.map { dirs -> dirs.map { it.asFile.path } } ?: project.provider { emptyList() }

internal fun Project.configureJacoco(androidComponents: AndroidComponentsExtension<*, *, *>) {
    configure<JacocoPluginExtension> {
        toolVersion = libs.findVersion("jacoco").get().toString()
    }

    // Jacoco global adjustments
    tasks.withType<Test>().configureEach {
        extensions.configure(JacocoTaskExtension::class) {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }

    androidComponents.onVariants { variant ->
        val obj = objects
        val buildDirFile = layout.buildDirectory.get().asFile

        val allJars: ListProperty<RegularFile> = obj.listProperty(RegularFile::class.java)
        val allDirs: ListProperty<Directory> = obj.listProperty(Directory::class.java)

        val taskName =
            "create${variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}CombinedCoverageReport"

        val reportTask = tasks.register(taskName, JacocoReport::class) {
            // Sources (Java/Kotlin)
            sourceDirectories.setFrom(
                files(
                    variant.sources.java.toFilePaths(this@configureJacoco),
                    variant.sources.kotlin.toFilePaths(this@configureJacoco),
                )
            )

            // Classes (ScopedArtifacts + fallbacks)
            val scopedClassDirs = allDirs.map { dirs ->
                dirs.map { dir -> obj.fileTree().setDir(dir).exclude(coverageExclusions) }
            }

            // JARs to FileTrees to exclude classes
            val jarTrees = allJars.map { jars ->
                jars.map { jar -> zipTree(jar).matching { exclude(coverageExclusions) } }
            }

            classDirectories.setFrom(jarTrees, scopedClassDirs)

            // Execution data: unit (*.exec) + androidTest (*.ec)
            val unitExec =
                fileTree("$buildDirFile/outputs/unit_test_code_coverage/${variant.name}UnitTest") {
                    include("**/*.exec")
                }
            val jacocoExec = fileTree("$buildDirFile/jacoco") { include("**/*.exec") }
            val androidEc =
                fileTree("$buildDirFile/outputs/code_coverage/${variant.name}AndroidTest") {
                    include("**/*.ec")
                }

            executionData.setFrom(files(unitExec, jacocoExec, androidEc))

            reports {
                xml.required.set(true)
                html.required.set(true)
            }
        }

        // Connects variant CLASSES to the tasks via ScopedArtifacts
        variant.artifacts.forScope(ScopedArtifacts.Scope.ALL)
            .use(reportTask)
            .toGet(
                ScopedArtifact.CLASSES,
                { _ -> allJars },
                { _ -> allDirs },
            )
    }
}