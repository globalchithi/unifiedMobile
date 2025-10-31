import com.vaxcare.unifiedhub.coverageExclusions
import org.gradle.testing.jacoco.tasks.JacocoReport

buildscript {
    dependencies {
        classpath(libs.google.oss.licenses.plugin)
    }
}

plugins {
    jacoco // Just to get JacocoReport available
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.androidx.room) apply false
    alias(libs.plugins.buildlogic.ktlint) apply false
    alias(libs.plugins.datadog) apply false
    alias(libs.plugins.google.dagger.hilt) apply false
    alias(libs.plugins.google.gms) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ktlint) apply false
}

allprojects {
    apply(plugin = "buildlogic.ktlint")
}

val devFile = "dev-properties.gradle"
val buildServerFile = "properties.gradle"
if (project.hasProperty("go")) {
    println("Gradle running build tasks with GoVar properties.")
    apply(from = buildServerFile)
} else {
    println("Gradle running build tasks with local dev properties.")
    apply(from = devFile)
}

tasks.register<JacocoReport>("jacocoMergedReport") {
    group = "verification"
    description = "Generates an aggregate coverage report (unit + androidTest) of all modules."

    val variant = (findProperty("coverageVariant") as? String)?.ifBlank { "debug" } ?: "debug"

    reports {
        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/merged/jacocoMerged.xml"))
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/merged/html"))
    }

    // 1) Execution data: *.exec (unit) + *.ec (androidTest)
    val allExecFiles = fileTree(rootDir) {
        include(
            "**/build/jacoco/*.exec",
            "**/build/outputs/unit_test_code_coverage/**/*.exec",
            "**/build/outputs/code_coverage/**/*.ec"
        )
        exclude("**/tmp/**")
    }
    val existingExecFiles = provider { allExecFiles.files.filter { it.exists() && it.length() > 0 } }
    executionData.setFrom(existingExecFiles)

    // 2) All modules sources
    val allSrc = subprojects.flatMap { p ->
        listOf(
            file("${p.projectDir}/src/main/java"),
            file("${p.projectDir}/src/main/kotlin")
        )
    }
    sourceDirectories.setFrom(provider { allSrc.filter { it.exists() } })

    // 3) All modules classes:
    //    - Android: tmp/kotlin-classes/<variant>, intermediates/javac/<variant>/classes
    //    - JVM: classes/kotlin/main
    //    - Android Library fallback: intermediates/aar_main_jar/<variant>/classes.jar (zipTree)
    val classTrees = provider {
        subprojects.flatMap { p ->
            val out = mutableListOf<FileTree>()

            // Android Kotlin
            val kot = file("${p.buildDir}/tmp/kotlin-classes/$variant")
            if (kot.exists()) out += fileTree(kot) { exclude(coverageExclusions) }

            // Android Java
            val jav = file("${p.buildDir}/intermediates/javac/$variant/classes")
            if (jav.exists()) out += fileTree(jav) { exclude(coverageExclusions) }

            // JVM only
            val jvm = file("${p.buildDir}/classes/kotlin/main")
            if (jvm.exists()) out += fileTree(jvm) { exclude(coverageExclusions) }

            // Android Library: classes.jar
            val aarJar = file("${p.buildDir}/intermediates/aar_main_jar/$variant/classes.jar")
            if (aarJar.exists()) {
                val jarTree = zipTree(aarJar).matching { exclude(coverageExclusions) }
                out += jarTree
            }

            out
        }
    }
    classDirectories.setFrom(classTrees)

    doFirst {
        val dirsCount = classDirectories.files.sumOf { it.walk().count { f -> f.isFile && f.extension == "class" } }
        val execCount = executionData.files.size
        println("▶️  Coverage variant: $variant")
        println("▶️  Coverage files (.exec/.ec) found: $execCount")
        println("▶️  Total .class found in classDirectories: $dirsCount")
    }

    doLast {
        println(
            "✅ JaCoCo merged XML  -> " + reports.xml.outputLocation
                .get()
                .asFile.absolutePath
        )
        println(
            "✅ JaCoCo merged HTML -> " + reports.html.outputLocation
                .get()
                .asFile.absolutePath + "/index.html"
        )
    }
}
