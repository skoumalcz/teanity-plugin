package com.skoumal.teanity.plugin.git

import org.gradle.api.Project

interface GitIntegrator {

    val latestVersion: String

    companion object {

        fun semantic(project: Project): GitIntegrator = GitIntegratorSemanticImpl(project)
        fun integration(project: Project): GitIntegrator = GitIntegratorIntegrationImpl(project)
        fun ci(project: Project): GitIntegrator = GitIntegratorCIImpl(project)

    }

}