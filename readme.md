## teanity plugin

This plugin in an ongoing effort to merge several tools and features we use daily into simple 
reusable plugin. 

### We currently support following features:
 
* enabling kotlin / kotlin extensions / kapt
* enabling kotlin 1.8 target
* enabling java 1.8 target
* including teanity libraries
    * with support for kapt libraries planned in near future
    * disabled by default
* setting versions from git
    * disabled by default
    * allows for adjusting version code by predefined multiplier
        * this might be beneficial for projects that do not conform with the version types defined below
    * options
        * `com.skoumal.teanity.plugin.VersionType.NONE` (default)
        * `com.skoumal.teanity.plugin.VersionType.SEMANTIC` (1.5.20)
        * `com.skoumal.teanity.plugin.VersionType.INTEGRATION` (2020.30)

### Usage

Your **root** build.gradle(.kts)

```kotlin
buildScript {
    classpath("com.skoumal:teanity-plugin:+")
}
```

Subprojects' build.gradle(.kts)

```kotlin
plugins {
    id("com.android.application") // or com.android.library
    // remove id("kotlin")
    // remove id("kotlin-android-extensions")
    // remove id("kotlin-kapt")
    id("teanity")
}

teanity {
    modules {
        version = "1.x.x-alphaYY" // default "1.0.+"

        useComponent = true // default false
        useCore = true // default false
        useDI = true // default false
        usePersistence = true // default false
        useNetwork = true // default false
        useUI = true // default false
        useTest = true // default false
        useTestUI = true // default false

        useAll() // sets all above to true
    }
    version {
        versionType = com.skoumal.teanity.plugin.VersionType.SEMANTIC
        // if semantic version == 1.5.20, then version code will be 10520. you can offset this 
        // number by the multiplier so in this case it will be 10520000
        versionCodeMultiplier = 1000 // note that it must be a positive number, defaults to 1
    }
}

android {
    // ...
}

dependencies {
    // ...
}
```