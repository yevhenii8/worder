import org.gradle.jvm.tasks.Jar
import worder.buildsrc.tasks.AssembleExecutableTask
import worder.buildsrc.tasks.UpdateFileStampsTask
import worder.buildsrc.tasks.UpdateVersionTask

version = "1.0.241"

plugins {
    application
    java
}

dependencies {
    implementation(":worder-commons")
}

application {
    mainClass.set("worder.launcher.App")
}

tasks {
    val updateVersionTask by register<UpdateVersionTask>("updateVersion")
    val assembleExecutableTask by register<AssembleExecutableTask>("assembleExecutable")
    val updateFileStampsTask by register<UpdateFileStampsTask>("updateFileStamps") {
        sourcesDir = projectDir.resolve("src")
        sourcesFormats = listOf(".java")
    }

    withType<Jar> {
        manifest {
            attributes["Main-Class"] = "worder.launcher.App"
        }
        from(
                configurations.compileClasspath.get().files.map { if (it.isDirectory) it else zipTree(it) },
                configurations.default.get().files.map { if (it.isDirectory) it else zipTree(it) }
        )
    }
    with(compileJava.get()) {
        dependsOn(updateFileStampsTask)
        dependsOn(updateVersionTask)
    }
    gradle.taskGraph.whenReady {
        if (hasTask(assembleExecutableTask)) {
            updateVersionTask.enabled = false
            updateFileStampsTask.enabled = false
        }
    }
}
