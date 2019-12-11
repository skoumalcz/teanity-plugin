package com.skoumal.teanity.plugin

import org.gradle.api.Action

open class BaseModuleExtension {
    open var useKapt = false
    internal open val modules = TeanityOptions()
    internal open val version = VersionOptions()

    open fun version(action: Action<VersionOptions>) = action.execute(version)
    open fun modules(action: Action<TeanityOptions>) = action.execute(modules)
}


open class TeanityOptions {
    internal val usesTeanity
        get() = useComponent || useCore || useDI || usePersistence || useNetwork || useUI ||
                useTest || useTestUI

    open fun useAll() {
        useComponent = true
        useCore = true
        useDI = true
        usePersistence = true
        useNetwork = true
        useUI = true
        useTest = true
        useTestUI = true
    }

    open var useComponent = false
    open var useCore = false
    open var useDI = false
    open var usePersistence = false
    open var useNetwork = false
    open var useUI = false
    open var useTest = false
    open var useTestUI = false

    open var version = "1.0.+"
}


open class VersionOptions {
    open var versionType = VersionType.NONE
}

enum class VersionType {
    SEMANTIC, INTEGRATION, NONE
}