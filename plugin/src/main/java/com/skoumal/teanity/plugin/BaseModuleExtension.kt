package com.skoumal.teanity.plugin

import com.android.build.gradle.api.ApkVariantOutput
import org.gradle.api.Action

open class BaseModuleExtension {
    internal open val modules = TeanityOptions()
    internal open val version = VersionOptions()

    open fun version(action: Action<VersionOptions>) = action.execute(version)
    open fun modules(action: Action<TeanityOptions>) = action.execute(modules)
}


internal object TeanityModules {
    const val COMPONENT = "component"
    const val CORE = "core"
    const val DI = "di"
    const val PERSISTENCE = "persistence"
    const val NETWORK = "network"
    const val UI = "ui"
    const val TEST = "test"
    const val TEST_UI = "test-ui"
}

open class TeanityOptions {
    private var components = mutableMapOf(
        TeanityModules.COMPONENT to false,
        TeanityModules.CORE to false,
        TeanityModules.DI to false,
        TeanityModules.PERSISTENCE to false,
        TeanityModules.NETWORK to false,
        TeanityModules.UI to false,
        TeanityModules.TEST to false,
        TeanityModules.TEST_UI to false
    )

    internal val usesTeanity
        get() = components.any { it.value }

    internal val usesAll
        get() = components.all { it.value }

    open fun useAll() {
        components.forEach {
            components[it.key] = true
        }
    }

    open var useComponent
        get() = components[TeanityModules.COMPONENT] ?: false
        set(value) = components.set(TeanityModules.COMPONENT, value)
    open var useCore
        get() = components[TeanityModules.CORE] ?: false
        set(value) = components.set(TeanityModules.CORE, value)
    open var useDI
        get() = components[TeanityModules.DI] ?: false
        set(value) = components.set(TeanityModules.DI, value)
    open var usePersistence
        get() = components[TeanityModules.PERSISTENCE] ?: false
        set(value) = components.set(TeanityModules.PERSISTENCE, value)
    open var useNetwork
        get() = components[TeanityModules.NETWORK] ?: false
        set(value) = components.set(TeanityModules.NETWORK, value)
    open var useUI
        get() = components[TeanityModules.UI] ?: false
        set(value) = components.set(TeanityModules.UI, value)
    open var useTest
        get() = components[TeanityModules.TEST] ?: false
        set(value) = components.set(TeanityModules.TEST, value)
    open var useTestUI
        get() = components[TeanityModules.TEST_UI] ?: false
        set(value) = components.set(TeanityModules.TEST_UI, value)

    open var version = "1.0.+"
}


open class VersionOptions {
    open var versionType = VersionType.NONE
    internal var versionCodeOverride: (VersionCodeOverrideAction) = { 1 }

    fun versionCodeOverride(action: VersionCodeOverrideAction) {
        versionCodeOverride = action
    }
}

enum class VersionType {
    SEMANTIC, INTEGRATION, NONE
}

typealias VersionCodeOverrideAction = ApkVariantOutput.(versionCode: Long) -> Long