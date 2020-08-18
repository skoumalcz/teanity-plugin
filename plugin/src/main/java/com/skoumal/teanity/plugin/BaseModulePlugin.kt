package com.skoumal.teanity.plugin

import com.android.build.api.variant.VariantOutputConfiguration.OutputType
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.skoumal.teanity.plugin.git.GitTagTask
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class BaseModulePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply {
            apply("kotlin-android")
            apply("kotlin-android-extensions")
            apply("kotlin-kapt")
        }

        target.extensions.create<BaseModuleExtension>("teanity")

        target.applyOptions()
        target.applyAndroid()
        target.applyKapt()
    }

    private fun Project.applyKapt() {
        afterEvaluate {
            extensions.getByType(KaptExtension::class).apply {
                correctErrorTypes = true
                useBuildCache = true
                mapDiagnosticLocations = true
                arguments {
                    arg("room.schemaLocation", "$projectDir/schemas")
                    arg("moshi.generated", "javax.annotation.Generated")
                }
                javacOptions {
                    option("-Xmaxerrs", 1000)
                }
            }
        }
    }

    private fun Project.applyAndroid() {
        (extensions.getByName("android") as? BaseExtension)?.apply {
            // Target at least 1.8, since it's supported by mainstream toolchain
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }
            project.tasks.withType(KotlinCompile::class.java).configureEach {
                kotlinOptions {
                    jvmTarget = "1.8"
                    freeCompilerArgs = listOf(
                        "-Xallow-result-return-type",
                        "-Xuse-experimental=kotlin.Experimental"
                    )
                }
            }

            (this as? BaseAppModuleExtension)?.also {
                it.setVersionIntegrator(this@applyAndroid)
            }
        }
    }

    private fun Project.applyOptions() {
        afterEvaluate {
            val extension = extensions.getByType<BaseModuleExtension>()
            if (extension.modules.usesTeanity) {
                dependencies { applyTeanity(extension.modules) }
            }
        }
    }

    private fun DependencyHandlerScope.applyTeanity(definition: TeanityOptions) {
        val version = definition.version

        if (definition.usesAll) {
            add("api", "com.skoumal:teanity:$version")
            return
        }

        if (definition.useComponent) {
            add("api", teanity("component", version))
        }
        if (definition.useCore) {
            add("api", teanity("core", version))
            add("kapt", teanity("core-compiler", version))
        }
        if (definition.useDI) {
            add("api", teanity("di", version))
        }
        if (definition.usePersistence) {
            add("api", teanity("persistence", version))
            add("kapt", teanity("persistence-compiler", version))
        }
        if (definition.useUI) {
            add("api", teanity("ui", version))
        }
        if (definition.useNetwork) {
            add("api", teanity("network", version))
            add("kapt", teanity("network-compiler", version))
        }
        if (definition.useTest) {
            add("testImplementation", teanity("test", version))
        }
        if (definition.useTestUI) {
            add("androidTestImplementation", teanity("test-ui", version))
        }
    }

    @Suppress("UnstableApiUsage")
    private fun BaseAppModuleExtension.setVersionIntegrator(project: Project) {
        onVariantProperties {
            val mainOutput = this.outputs.single { it.outputType == OutputType.SINGLE }
            val versionCodeTask = project.tasks.register(
                "selectGitTagFor${name.capitalize()}",
                GitTagTask::class.java
            ) {
                outputFile.set(project.layout.buildDirectory.file("git-version.txt"))
                versionOptions.set(project.extensions.getByType<BaseModuleExtension>().version)
                identity.set(this@onVariantProperties)
            }

            val defaultVersionName = mainOutput.versionName.getOrElse("")
            val defaultVersionCode = mainOutput.versionCode.getOrElse(0)

            mainOutput.versionName.set(versionCodeTask.map { task ->
                task.outputFile.runCatching { get().asFile.readLines()[0] }
                    .fold(onSuccess = { it }, onFailure = { defaultVersionName })
                    .also { println("Version Name = $it") }
            })
            mainOutput.versionCode.set(versionCodeTask.map { task ->
                task.outputFile.runCatching { get().asFile.readLines()[1].toInt() }
                    .fold(onSuccess = { it }, onFailure = { defaultVersionCode })
                    .also { println("Version Code = $it") }
            })
        }
    }

    private companion object {

        private fun teanity(module: String, version: String) =
            "com.skoumal.teanity:$module:$version"

    }


}