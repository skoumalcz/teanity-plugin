plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("teanity-plugin") {
            id = "com.skoumal.teanity.plugin"
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

    implementation("com.android.tools.build:gradle:3.5.3")
    implementation(kotlin("gradle-plugin", "1.3.61"))
    implementation(kotlin("android-extensions"))
}
