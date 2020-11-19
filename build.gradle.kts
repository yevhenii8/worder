plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.72" apply (false)
    id("org.openjfx.javafxplugin") version "0.0.9" apply (false)
}

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
            gradleVersion = "6.7.1"
        }
        withType<Test> {
            onlyIf { project.hasProperty("devThroughTest") }
        }
    }
}
