package com.skoumal.teanity.plugin.version

import org.gradle.api.Project

interface VersionIntegrator {

    val versionName: String
    val versionCode: Int

    companion object {

        operator fun invoke(project: Project, type: String): VersionIntegrator = when (type) {
            "semantic" -> VersionIntegratorSemanticImpl(project)
            "integration" -> VersionIntegratorIntegrationImpl(project)
            else -> throw IllegalStateException("Type $type is not defined")
        }

    }

}