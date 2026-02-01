plugins {
    alias(libs.plugins.kotlin.jvm.plugin)
    alias(libs.plugins.ktor.plugin)
}

group = "cz.tomashula"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.freemarker)
    implementation(libs.kgit)
    implementation(libs.kotlinLogging)
    implementation(libs.slf4j.simple)
    // implementation(libs.logback.classic)
}

application {
    mainClass.set("cz.tomashula.MainKt")
}

kotlin {
    jvmToolchain(21)
}

ktor {
    fatJar {
        archiveFileName.set("app.jar")
    }
}
