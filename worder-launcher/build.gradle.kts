import org.gradle.jvm.tasks.Jar
import worder.buildsrc.tasks.UpdateFileStampsTask


version = "1.0"


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


    with(compileJava.get()) {
        dependsOn(updateFileStampsTask)
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


    register<Exec>("package") {
        dependsOn(jar)

        val jarFile = jar.get().outputs.files.singleFile

        when (val currentOs = System.getProperty("os.name")) {
            "Linux" -> {
                commandLine(
                        "bash", "-c",
                        "jpackage" +
                                " --input '${jarFile.parent}'" +
                                " --name WorderLauncher" +
                                " --main-jar ${jarFile.name}" +
                                " --type deb" +
                                " --app-version '${project.version}'" +
                                " --copyright '© 2019-2020 Yevhenii Nadtochii No Rights Reserved'" +
                                " --description 'Launcher with auto-update for Worder GUI'" +
                                " --dest 'build/executables'" +
                                " --icon 'build/resources/main/icons/worder-icon_512x512.png'" +
                                " --linux-deb-maintainer yevhenii.nadtochii@gmail.com" +
                                " --linux-shortcut"
                )
            }
            else -> throw IllegalStateException("There's no support for native build of WorderLauncher for your OS: $currentOs")
        }
    }
}
