package com.skoumal.teanity.plugin.version

import com.skoumal.teanity.plugin.VersionOptions
import com.skoumal.teanity.plugin.VersionType
import org.gradle.api.Project

interface VersionIntegrator {

    val versionName: String
    val versionCode: Long

    companion object {

        operator fun invoke(project: Project, type: VersionOptions): VersionIntegrator? =
            when (type.versionType) {
                VersionType.SEMANTIC -> VersionIntegratorSemanticImpl(project)
                VersionType.INTEGRATION -> VersionIntegratorIntegrationImpl(project)
                VersionType.CI -> VersionIntegratorCIImpl(project)
                else -> null
            }

    }

}