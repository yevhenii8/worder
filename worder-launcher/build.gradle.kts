import org.gradle.jvm.tasks.Jar
import worder.buildsrc.tasks.UpdateFileStampsTask
import worder.buildsrc.tasks.UpdateVersionTask


version = "1.0.162-SNAPSHOT"


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
        from(configurations.compileClasspath.get().files.map { if (it.isDirectory) it else zipTree(it) })
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

        when (val currentOs = System.getProperty("os.name")) {
            "Linux" -> {
                commandLine(
                        "bash", "-c",
                        "jpackage" +
                                " --input '${jarFile.parent}'" +
                                " --name 'Worder Launcher'" +
                                " --main-jar ${jarFile.name}" +
                                " --app-version '${project.version}'" +
                                " --copyright '© 2020 Yevhenii Nadtochii No Rights Reserved'" +
                                " --description 'Launcher with auto-update for Worder GUI'" +
                                " --dest 'build/executables'" +
                                " --vendor 'Yevhenii Nadtochii'" +
                                " --icon 'build/resources/main/icons/worder-icon_512x512.png'" +

                                " --linux-deb-maintainer yevhenii.nadtochii@gmail.com" +
                                " --linux-package-name worder-launcher" +
                                " --linux-shortcut"
                )
            }
            else -> throw IllegalStateException("There's no support for native build of WorderLauncher for your OS: $currentOs")
        }
    }
}
