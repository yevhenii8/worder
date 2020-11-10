import worder.buildsrc.tasks.UpdateFileStampsTask
import org.gradle.jvm.tasks.Jar

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
}
