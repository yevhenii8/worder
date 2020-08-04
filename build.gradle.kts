subprojects {
    repositories {
        jcenter()
    }

    tasks {
        withType<Test> {
            useJUnitPlatform()
            testLogging.setEvents(listOf("passed", "skipped", "failed"))
            testLogging.showStandardStreams = true
        }

        withType<Wrapper> {
            distributionType = Wrapper.DistributionType.ALL
            gradleVersion = "6.5"
        }

//    register<UpdateFileStampsTask>(
//            "updateFileStamps",
//            projectDir.resolve("src"),
//            listOf(".kt", ".kts")
//    ).also {
//        compileKotlin.get().dependsOn(it.get())
//    }
    }
}
