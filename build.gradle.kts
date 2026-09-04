plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktor) apply false
    alias(libs.plugins.spotless)
}

val selectedFiles = findProperty("formatFilesFile")?.let { file(it).readLines() }
val kotlinFiles = selectedFiles?.filter { it.endsWith(".kt") }
val kotlinGradleFiles = selectedFiles?.filter { it.endsWith(".gradle.kts") }

spotless {
    kotlin {
        if (kotlinFiles == null) {
            target("**/*.kt")
        } else {
            target(kotlinFiles)
        }
        targetExclude("**/build/**", "**/generated/**", "**/schemas/**")
        ktlint(libs.versions.ktlint.get())
            .editorConfigOverride(
                mapOf(
                    "ktlint_standard_function-naming" to "disabled",
                    "ktlint_standard_filename" to "disabled",
                    "ktlint_standard_no-wildcard-imports" to "disabled",
                ),
            )
    }
    kotlinGradle {
        if (kotlinGradleFiles == null) {
            target("**/*.gradle.kts")
        } else {
            target(kotlinGradleFiles)
        }
        ktlint(libs.versions.ktlint.get())
            .editorConfigOverride(
                mapOf(
                    "ktlint_standard_function-naming" to "disabled",
                    "ktlint_standard_filename" to "disabled",
                    "ktlint_standard_no-wildcard-imports" to "disabled",
                ),
            )
    }
}

tasks.register("formatStaged") {
    group = "formatting"
    description = "Formats the staged Kotlin and Gradle Kotlin files"
    dependsOn("spotlessApply")
}

tasks.register("checkFormatting") {
    group = "verification"
    description = "Checks Kotlin and Gradle Kotlin formatting"
    dependsOn("spotlessCheck")
}
