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
            gradleVersion = "6.5.1"
        }

        register<worder.buildsrc.UpdateFileStampsTask>(
                name = "updateFileStamps"
        )
    }
}
