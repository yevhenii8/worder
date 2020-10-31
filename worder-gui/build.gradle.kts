import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import worder.buildsrc.tasks.UpdateFileStampsTask
import worder.buildsrc.tasks.UpdateVersionTask
import worder.buildsrc.tasks.DeployAppTask
import worder.buildsrc.FileSystemDeployer
import worder.buildsrc.BintrayDeployer
import java.nio.file.Path

version = "1.0.111-SNAPSHOT"

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

    implementation("org.xerial:sqlite-jdbc:3.32.3.1")
    implementation("org.jetbrains.exposed:exposed-core:0.26.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.26.1")
    implementation("no.tornado:tornadofx:2.0.0-SNAPSHOT")
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
    val updateFileStampsTask by register<UpdateFileStampsTask>("updateFileStamps")
    val updateVersionTask by register<UpdateVersionTask>("updateVersion")
    val deployLocalTask by register<DeployAppTask>("deployLocal")
    val deployBintrayTask by register<DeployAppTask>("deployBintray")


    with(compileKotlin.get()) {
        dependsOn(updateFileStampsTask)
        dependsOn(updateVersionTask)
    }
    gradle.taskGraph.whenReady {
        if (hasTask(deployLocalTask) || hasTask(deployBintrayTask)) {
            updateVersionTask.enabled = false
            updateFileStampsTask.enabled = false
        }
    }
    deployBintrayTask.apply {
        deployer = BintrayDeployer(
                bintrayUser = project.properties["bintrayUser"] as String,
                bintrayKey = project.properties["bintrayKey"] as String,
                repository = "generic",
                `package` = "worder-gui",
                version = "Latest"
        )
    }
    deployLocalTask.apply {
        deployer = FileSystemDeployer(Path.of("/home/yevhenii/WorderDeployTest"))
    }


    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
    withType<UpdateFileStampsTask> {
        sourcesDir = projectDir.resolve("src")
        sourcesFormats = listOf(".kt")
    }
    withType<DeployAppTask> {

    }


    // FOR DEV PURPOSES ONLY
    register("testArgs") {
        dependsOn(project.tasks.getByName("configJavafxRun"))
        dependsOn(project.tasks.getByName(JavaPlugin.JAR_TASK_NAME))

        doLast {
            val execTask = project.tasks.findByName(ApplicationPlugin.TASK_RUN_NAME) as JavaExec
            val jarTask = project.tasks.findByName(JavaPlugin.JAR_TASK_NAME) as org.gradle.jvm.tasks.Jar

            println(execTask.allJvmArgs)
            println()
            println(execTask.classpath.files)
            println()
            println(execTask.jvmArgs)
        }
    }
}
