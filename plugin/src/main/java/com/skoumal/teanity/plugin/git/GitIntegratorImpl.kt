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
                commandLine("git", "describe", "--match", "${year}.*-*", "--long")
            }

            val postfix = "-"
            val tag = output.toString().trim()

            tag.substring(0, tag.indexOf(postfix))
        }.fold(onSuccess = { it }, onFailure = { "$year.1" })

}

class GitIntegratorCIImpl(
    private val project: Project
) : GitIntegrator {

    override val latestVersion: String
        get() = "%s-%s (%s)".format(
            branch,
            commitHash,
            date
        )

    private val branch: String
        get() = kotlin
            .runCatching { command("git", "rev-parse", "--abbrev-ref", "HEAD") }
            .fold(onSuccess = { it }, onFailure = { "untracked" })

    private val commitHash: String
        get() = kotlin
            .runCatching { command("git", "rev-parse", "--short=7", "HEAD") }
            .fold(onSuccess = { it }, onFailure = { "detached" })

    private val date: String
        get() = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

    @Throws(Exception::class)
    private fun command(vararg commands: String): String {
        val output = ByteArrayOutputStream()
        project.exec {
            standardOutput = output
            commandLine(*commands)
        }
        return output.toString().trim()
    }
}