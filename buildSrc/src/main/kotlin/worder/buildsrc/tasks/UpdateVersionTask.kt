package worder.buildsrc.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import worder.buildsrc.VersionDescriptor

open class UpdateVersionTask : DefaultTask() {
    init {
        group = "Documentation"
        description = "Maintains the dynamic version of an application."
    }


    @TaskAction
    fun execute() {
        val worderVersion = VersionDescriptor.fromString(project.version.toString()).apply {
            buildNumber++
        }

        with(project.projectDir) {
            val buildScript = resolve("build.gradle.kts")
            val buildScriptContent = buildScript.readText()

            buildScript.writeText(
                    buildScriptContent.replace(
                            regex = "^version = .*$".toRegex(RegexOption.MULTILINE),
                            replacement = "version = \"$worderVersion\""
                    )
            )

            resolve("src")
                    .resolve("main")
                    .resolve("resources")
                    .resolve("version")
                    .writeText(worderVersion.toString())
        }
    }
}
