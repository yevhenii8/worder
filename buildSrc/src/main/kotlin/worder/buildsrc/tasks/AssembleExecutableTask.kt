package worder.buildsrc.tasks

import org.gradle.api.tasks.Exec
import org.gradle.jvm.tasks.Jar
import worder.commons.OS

@Suppress("LeakingThis")
open class AssembleExecutableTask : Exec() {
    init {
        val jarTask = project.tasks.getByName("jar") as Jar
        val jarFile = jarTask.outputs.files.singleFile

        dependsOn(project.tasks.getByName("clean"))
        dependsOn(jarTask)

        val iconsCatalog = project.rootDir
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
            OS.LINUX -> {
                jpackageCommand.add("--icon \"${iconsCatalog.resolve("worder-icon_256x256.png")}\"")
                jpackageCommand.add("--linux-deb-maintainer yevhenii.nadtochii@gmail.com")
                jpackageCommand.add("--linux-package-name worder-launcher")
                jpackageCommand.add("--linux-shortcut")

                commandLine("bash", "-c", jpackageCommand.joinToString(" "))
            }
            OS.WINDOWS_10 -> {
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
