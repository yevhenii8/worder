import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application

    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id("org.openjfx.javafxplugin") version "0.0.9"
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.7")

    implementation("no.tornado:tornadofx:2.0.0-SNAPSHOT")

    runtimeOnly("org.slf4j:slf4j-nop:1.7.30")
    implementation("org.xerial:sqlite-jdbc:3.32.3.1")
    implementation("org.jetbrains.exposed:exposed-core:0.26.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.26.1")


    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.1.1")
    testImplementation("io.mockk:mockk:1.10.0")
}

application {
    mainClassName = "worder.gui.AppEntry"

    javafx {
        version = "14"
        modules("javafx.controls", "javafx.graphics", "javafx.web")
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}
