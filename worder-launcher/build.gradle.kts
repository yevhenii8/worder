import worder.buildsrc.tasks.UpdateFileStampsTask

plugins {
    application
    java
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
}
