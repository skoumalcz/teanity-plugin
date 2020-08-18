package com.skoumal.teanity.plugin.git

import java.text.SimpleDateFormat
import java.util.*

object GitTagTaskConfigIntegration : GitTagTask.Config {

    private val year get() = SimpleDateFormat("yyyy").format(Date())

    override val pattern: String
        get() = "${year}.*-*"
    override val default: String
        get() = "$year.1"
    override val prefix: String?
        get() = null
    override val postfix: String?
        get() = "-"

    override fun transformVersionCode(versionName: String): Long {
        val parts = versionName.split('.')

        if (parts.size != 2) {
            throw RuntimeException("$versionName is not eligible for fetching version code from.")
        }

        val mainVersion = parts[0].toLong() * 1000 //999 is max range of minVersion
        val minVersion = parts[1].toLong() % 1000

        return mainVersion + minVersion
    }

}