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
    implementation("no.tornado:tornadofx:latest.integration")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.5")

    implementation("org.xerial:sqlite-jdbc:latest.release")
    implementation("org.jetbrains.exposed:exposed-core:latest.release")
    implementation("org.jetbrains.exposed:exposed-jdbc:latest.release")
}

application {
    mainClassName = "worder.AppEntry"
}

javafx {
    version = "14"
    modules("javafx.controls", "javafx.graphics")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
