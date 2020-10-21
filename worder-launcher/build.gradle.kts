plugins {
    application
    java
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

application {
    mainClass.set("worder.launcher.App")
}

tasks {
//    withType<UpdateFileStampsTask> {
//        sourcesDir = projectDir.resolve("src")
//        sourcesFormats = listOf(".java")
//        compileJava.get().dependsOn(this)
//    }
}
