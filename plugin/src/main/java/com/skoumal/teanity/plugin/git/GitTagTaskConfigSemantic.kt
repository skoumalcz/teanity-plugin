package com.skoumal.teanity.plugin.git

object GitTagTaskConfigSemantic : GitTagTask.Config {

    override val pattern: String
        get() = "*/*.*.*"
    override val default: String
        get() = "0.0.1"
    override val prefix: String?
        get() = "/"
    override val postfix: String?
        get() = "-"

    override fun transformVersionCode(versionName: String): Long {
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
        return parts.joinToString(separator = "").toLong()
    }

}