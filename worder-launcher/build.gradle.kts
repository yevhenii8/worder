import org.gradle.jvm.tasks.Jar
import worder.buildsrc.tasks.UpdateFileStampsTask
import worder.buildsrc.tasks.UpdateVersionTask
import worder.commons.OS
import worder.commons.OS.LINUX
import worder.commons.OS.WINDOWS_10


version = "1.0.211"


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
    val updateFileStampsTask by register<UpdateFileStampsTask>("updateFileStamps")
    val updateVersionTask by register<UpdateVersionTask>("updateVersion")
    val makeExecutableTask by register<Exec>("makeExecutable")


    with(compileJava.get()) {
        dependsOn(updateFileStampsTask)
        dependsOn(updateVersionTask)
    }
    withType<UpdateFileStampsTask> {
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

    gradle.taskGraph.whenReady {
        if (hasTask(makeExecutableTask)) {
            updateVersionTask.enabled = false
            updateFileStampsTask.enabled = false
        }
    }
    makeExecutableTask.apply {
        dependsOn(clean)
        dependsOn(jar)

        val jarFile = jar.get().outputs.files.singleFile
        val iconsCatalog = rootDir
                .resolve("worder-commons")
                .resolve("src")
                .resolve("main")
                .resolve("resources")
                .resolve("icons")
        val jpackageCommand = mutableListOf(
                "jpackage",
                "--input", "\"${jarFile.parent}\"",
                "--name", "\"Worder Launcher\"",
                "--main-jar", "\"${jarFile.name}\"",
                "--app-version", "\"${project.version}\"",
                "--copyright", "\"© 2020 Yevhenii Nadtochii No Rights Reserved\"",
                "--description", "\"Launcher with auto-update for Worder GUI\"",
                "--dest", "\"build/executables\"",
                "--vendor", "\"Yevhenii Nadtochii\""
        )

        when (OS.getCurrentOS()!!) {
            LINUX -> {
                jpackageCommand.add("--icon \"${iconsCatalog.resolve("worder-icon_256x256.png")}\"")
                jpackageCommand.add("--linux-deb-maintainer yevhenii.nadtochii@gmail.com")
                jpackageCommand.add("--linux-package-name worder-launcher")
                jpackageCommand.add("--linux-shortcut")

                commandLine("bash", "-c", jpackageCommand.joinToString(" "))
            }
            WINDOWS_10 -> {
                jpackageCommand.add("--icon")
                jpackageCommand.add("\"${iconsCatalog.resolve("worder-icon_256x256.ico")}\"")
                jpackageCommand.add("--win-menu")
                jpackageCommand.add("--win-shortcut")

                commandLine(jpackageCommand)
            }
        }

        doFirst {
            if (commandLine.isEmpty())
                throw IllegalStateException(
                        "There's no support for building native executable of Worder Launcher for your OS: " +
                                OS.getCurrentOS()
                )
        }
    }
}
