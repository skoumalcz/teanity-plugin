package com.skoumal.teanity.plugin.version

import com.skoumal.teanity.plugin.VersionType
import org.gradle.api.Project

interface VersionIntegrator {

    val versionName: String
    val versionCode: Int

    companion object {

        operator fun invoke(project: Project, type: VersionType): VersionIntegrator? = when (type) {
            VersionType.SEMANTIC -> VersionIntegratorSemanticImpl(project)
            VersionType.INTEGRATION -> VersionIntegratorIntegrationImpl(project)
            else -> null
        }

    }

}