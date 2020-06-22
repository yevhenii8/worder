import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application

    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id("org.openjfx.javafxplugin") version "0.0.8"
}

repositories {
    jcenter()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("no.tornado:tornadofx:2.0.0-SNAPSHOT")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.7")

    implementation("org.xerial:sqlite-jdbc:3.30.1")
    implementation("org.jetbrains.exposed:exposed-core:0.23.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.23.1")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.1.0.RC2")
    testImplementation("io.mockk:mockk:1.10.0")
}

application {
    mainClassName = "worder.core.AppEntry"

    javafx {
        version = "14"
        modules("javafx.controls", "javafx.graphics")
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<Test> {
        useJUnitPlatform()
    }
}
