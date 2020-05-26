package com.skoumal.teanity.plugin.version

import com.skoumal.teanity.plugin.git.GitIntegrator
import org.gradle.api.Project

class VersionIntegratorSemanticImpl(
    private val project: Project,
    private val multiplier: Int,
    private val git: GitIntegrator = GitIntegrator.semantic(project)
) : VersionIntegrator, GitIntegrator by git {

    override val versionName: String = latestVersion
    override val versionCode: Int
        get() {
            val parts = versionName.split('.').toMutableList()
            if (parts.size != 3 || parts[1].length > 2 || parts[2].length > 2) {
                throw RuntimeException("Invalid version tag \"${versionName}\", format \"x.y.z\" expected.")
            }
            if (parts[1].length == 1) {
                parts[1] = "0" + parts[1]
            }
            if (parts[2].length == 1) {
                parts[2] = "0" + parts[2]
            }
            return Integer.parseInt(parts[0] + parts[1] + parts[2]) * multiplier
        }

}

class VersionIntegratorIntegrationImpl(
    private val project: Project,
    private val multiplier: Int,
    private val git: GitIntegrator = GitIntegrator.integration(project)
) : VersionIntegrator, GitIntegrator by git {

    override val versionName: String = latestVersion
    override val versionCode: Int
        get() {
            val parts = versionName.split('.')

            if (parts.size != 2) {
                throw RuntimeException("$versionName is not eligible for fetching version code from.")
            }

            val mainVersion = parts[0].toLong() * 1000 //999 is max range of minVersion
            val minVersion = parts[1].toLong() % 1000

            return (mainVersion + minVersion).toInt() * multiplier
        }

}