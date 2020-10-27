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
    maven(url = "https://dl.bintray.com/kotlin/kotlin-dev/")
}

dependencies {
    compileOnly(gradleApi())

    compileOnly("com.android.tools.build:gradle:4.2.0-alpha14")
    implementation(kotlin("gradle-plugin", "1.4.20-RC-202"))
    implementation(kotlin("parcelize"))
}
