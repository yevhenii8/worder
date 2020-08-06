import worder.buildsrc.tasks.UpdateFileStampsTask

plugins {
    application
    java
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

application {
    mainClassName = "worder.launcher.App"
}

tasks {
//    withType<UpdateFileStampsTask> {
//        sourcesDir = projectDir.resolve("src")
//        sourcesFormats = listOf(".java")
//        compileJava.get().dependsOn(this)
//    }
}
