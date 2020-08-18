plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

apply(plugin = "maven")
apply(plugin = "maven-publish")

gradlePlugin {
    plugins {
        register("teanity-plugin") {
            id = "teanity"
            implementationClass = "com.skoumal.teanity.plugin.BaseModulePlugin"
        }
    }
}

repositories {
    google()
    mavenCentral()
    jcenter()
}

dependencies {
    compileOnly(gradleApi())

    compileOnly("com.android.tools.build:gradle:4.1.0-rc01")
    implementation(kotlin("gradle-plugin", "1.4.0"))
    implementation(kotlin("android-extensions"))
}
