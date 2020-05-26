package com.skoumal.teanity.plugin.version

import com.skoumal.teanity.plugin.VersionOptions
import com.skoumal.teanity.plugin.VersionType
import org.gradle.api.Project

interface VersionIntegrator {

    val versionName: String
    val versionCode: Int

    companion object {

        operator fun invoke(project: Project, type: VersionOptions): VersionIntegrator? =
            when (type.versionType) {
                VersionType.SEMANTIC -> VersionIntegratorSemanticImpl(
                    project,
                    type.versionCodeMultiplier
                )
                VersionType.INTEGRATION -> VersionIntegratorIntegrationImpl(
                    project,
                    type.versionCodeMultiplier
                )
                else -> null
            }

    }

}