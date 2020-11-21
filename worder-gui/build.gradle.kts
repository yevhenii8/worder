import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import worder.buildsrc.tasks.DeployAppTask
import worder.buildsrc.tasks.DeployDemoTask
import worder.buildsrc.tasks.UpdateFileStampsTask
import worder.buildsrc.tasks.UpdateVersionTask
import worder.commons.IOExchanger
import worder.commons.impl.BintrayExchanger
import worder.commons.impl.LocalExchanger

version = "1.0.195"

plugins {
    application
    id("org.jetbrains.kotlin.jvm")
    id("org.openjfx.javafxplugin")
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
    implementation("org.xerial:sqlite-jdbc:3.32.3.1")
    implementation("org.jetbrains.exposed:exposed-core:0.26.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.26.1")
    implementation("no.tornado:tornadofx:2.0.0-SNAPSHOT")
    implementation(":worder-commons")

    runtimeOnly("org.slf4j:slf4j-nop:1.7.30")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.1.1")
    testImplementation("io.mockk:mockk:1.10.0")
}

application {
    mainClass.set("worder.gui.AppEntry")

    javafx {
        version = "14"
        modules("javafx.controls", "javafx.graphics", "javafx.web")
    }
}

tasks {
    var bintrayExchanger: IOExchanger? = null
    val bintrayUser: String? by project
    val bintrayKey: String? by project

    if (bintrayUser != null && bintrayKey != null)
        bintrayExchanger = BintrayExchanger(
                bintrayUser as String,
                bintrayKey as String,
                "generic",
                "worder-gui",
                "Latest"
        )

    val updateVersionTask by register<UpdateVersionTask>("updateVersion")
    val updateFileStampsTask by register<UpdateFileStampsTask>("updateFileStamps") {
        sourcesDir = projectDir.resolve("src")
        sourcesFormats = listOf(".kt")
    }
    val deployAppLocalTask by register<DeployAppTask>("deployAppLocal") {
        deployExchanger = LocalExchanger(projectDir.toPath().resolve("WorderLocalDistribution"))
    }
    val deployAppBintrayTask by register<DeployAppTask>("deployAppBintray") {
        if (bintrayExchanger != null) {
            deployExchanger = bintrayExchanger
        }
    }
    val deployDemoBintrayTask by register<DeployDemoTask>("deployDemoBintray") {
        if (bintrayExchanger != null) {
            deployExchanger = bintrayExchanger
        }
    }

    with(compileKotlin.get()) {
        dependsOn(updateFileStampsTask)
        dependsOn(updateVersionTask)
    }
    gradle.taskGraph.whenReady {
        if (
                hasTask(deployAppLocalTask) ||
                hasTask(deployAppBintrayTask) ||
                hasTask(deployDemoBintrayTask)
        ) {
            updateVersionTask.enabled = false
            updateFileStampsTask.enabled = false
        }
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}
