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

    implementation("com.android.tools.build:gradle:4.0.0")
    implementation(kotlin("gradle-plugin", "1.3.72"))
    implementation(kotlin("android-extensions"))
}
