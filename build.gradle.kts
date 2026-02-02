plugins {
    alias(libs.plugins.kotlin.jvm.plugin)
    alias(libs.plugins.ktor.plugin)
    alias(libs.plugins.kotlin.serialization)
}

group = "io.github.tomhula"
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
    implementation(libs.kotlinx.serialization.json)
    // implementation(libs.logback.classic)
}

application {
    mainClass.set("io.github.tomhula.MainKt")
}

kotlin {
    jvmToolchain(21)
}

ktor {
    fatJar {
        archiveFileName.set("app.jar")
    }
}
