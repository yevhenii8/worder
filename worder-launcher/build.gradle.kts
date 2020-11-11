import org.gradle.jvm.tasks.Jar
import worder.buildsrc.tasks.UpdateFileStampsTask

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


    register("package") {
        doFirst {
            val jarFile = jar.get().outputs.files.singleFile
            val executableType = when (val currentOs = System.getProperty("os.name")) {
                "Linux" -> "deb"
                else -> throw IllegalStateException("There's no support of Worder-Launcher for your OS: $currentOs")
            }

            ProcessBuilder()
                    .command(
                            "jpackage",
                            "--input", jarFile.absolutePath,
                            "--name", "WorderLauncher",
                            "--main-jar", jarFile.name,
                            "--type", executableType
                    )
                    .inheritIO()
                    .start()
        }
    }
}
