import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
}

repositories {
    jcenter()
}

dependencies {
    implementation(gradleApi())
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.1.1")
    testImplementation("io.mockk:mockk:1.10.0")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}
