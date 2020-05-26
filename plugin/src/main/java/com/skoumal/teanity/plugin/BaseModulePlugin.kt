package com.skoumal.teanity.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import com.skoumal.teanity.plugin.version.VersionIntegrator
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
            val proguardFile = "proguard-rules.pro"
            when (this) {
                is LibraryExtension -> defaultConfig {
                    consumerProguardFiles(proguardFile)
                }
                is AppExtension -> buildTypes {
                    getByName("release") {
                        isMinifyEnabled = true
                        isShrinkResources = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            proguardFile
                        )
                    }
                }
            }

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
        }

        afterEvaluate {
            if (!plugins.hasPlugin("com.android.application")) {
                return@afterEvaluate
            }
            val extension = extensions.getByType<BaseModuleExtension>()
            val integrator = VersionIntegrator(this, extension.version) ?: return@afterEvaluate

            setVersionIntegrator(integrator)
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
        }
        if (definition.useDI) {
            add("api", teanity("di", version))
        }
        if (definition.usePersistence) {
            // todo provide compiler
            add("api", teanity("persistence", version))
        }
        if (definition.useUI) {
            add("api", teanity("ui", version))
        }
        if (definition.useNetwork) {
            add("api", teanity("network", version))
        }
        if (definition.useTest) {
            add("testImplementation", teanity("test", version))
        }
        if (definition.useTestUI) {
            add("androidTestImplementation", teanity("test-ui", version))
        }
    }

    private fun Project.setVersionIntegrator(version: VersionIntegrator) {
        val android = extensions.getByName("android") as AppExtension
        val versionName = version.versionName
        val versionCode = version.versionCode

        android.applicationVariants.all {
            outputs.all {
                this as ApkVariantOutputImpl
                versionNameOverride = versionName
                versionCodeOverride = versionCode

                println("> Task :${project.name}:${name}:version [$versionNameOverride ($versionCodeOverride)]")
            }
        }
    }

    private companion object {

        private fun teanity(module: String, version: String) =
            "com.skoumal.teanity:$module:$version"

    }


}