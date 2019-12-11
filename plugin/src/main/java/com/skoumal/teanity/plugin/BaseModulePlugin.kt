package com.skoumal.teanity.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class BaseModulePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply {
            apply("kotlin-android")
            apply("kotlin-android-extensions")
        }

        target.applyOptions()
        target.applyAndroid()
    }

    private fun Project.applyAndroid() {
        (extensions.getByName("android") as? BaseExtension)?.apply {
            defaultConfig {
                versionCode = 1
                versionName = "1.0.0"
            }

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
                }
            }
        }

        val extension = extensions.getByType<BaseModuleExtension>()
        if (extension.version.useAutoVersion) {
            // corresponds to branches @ https://github.com/skoumalcz/gradle-git-android-version
            val branch = when (extension.version.versionType) {
                VersionType.SEMANTIC -> "semantic"
                VersionType.INTEGRATION -> "integration"
            }
            apply(from = "https://raw.githubusercontent.com/skoumalcz/gradle-git-android-version/$branch/android-version.gradle")
        }
    }

    private fun Project.applyOptions() {
        extensions.create<BaseModuleExtension>("teanity")

        afterEvaluate {
            val extension = extensions.getByType<BaseModuleExtension>()
            if (extension.useKapt) {
                plugins.apply("kotlin-kapt")
            }
            if (extension.modules.usesTeanity) {
                dependencies { applyTeanity(extension.modules) }
            }
        }
    }

    private fun DependencyHandlerScope.applyTeanity(definition: TeanityOptions) {
        if (definition.useComponent) {
            add("api", teanity("component"))
        }
        if (definition.useCore) {
            add("api", teanity("core"))
        }
        if (definition.useDI) {
            add("api", teanity("di"))
        }
        if (definition.usePersistence) {
            add("api", teanity("persistence"))
        }
        if (definition.useUI) {
            add("api", teanity("ui"))
        }
        if (definition.useNetwork) {
            add("api", teanity("network"))
        }
        if (definition.useTest) {
            add("testImplementation", teanity("test"))
        }
        if (definition.useTestUI) {
            add("androidTestImplementation", teanity("test-ui"))
        }
    }

    private companion object {

        private fun teanity(module: String, version: String = "1.0.+") =
            "com.skoumal.teanity:$module:$version"

    }


}