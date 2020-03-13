package com.skoumal.teanity.plugin.git

import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class GitIntegratorSemanticImpl(
    private val project: Project
) : GitIntegrator {

    override val latestVersion: String
        get() = runCatching {
            val output = ByteArrayOutputStream()
            project.exec {
                standardOutput = output
                commandLine("git", "describe", "--match", "*/*.*.*", "--long")
            }

            val prefix = "/"
            val postfix = '-'
            val longTag = output.toString().trim()

            longTag.substring(
                longTag.indexOf(prefix) + prefix.length,
                longTag.indexOf(postfix)
            )
        }.fold(onSuccess = { it }, onFailure = { "0.0.1" })

}

class GitIntegratorIntegrationImpl(
    private val project: Project
) : GitIntegrator {

    private val year get() = SimpleDateFormat("yyyy").format(Date())

    override val latestVersion: String
        get() = runCatching {
            val output = ByteArrayOutputStream()

            project.exec {
                standardOutput = output
                commandLine("git", "describe", "--match", "\"${year}.*-*\"", "--long")
            }

            val prefix = ""
            val postfix = "-"
            val tag = output.toString().trim()

            tag.substring(
                tag.lastIndexOf(prefix) + prefix.length,
                tag.indexOf(postfix)
            )
        }.fold(onSuccess = { it }, onFailure = { "$year.1" })

}
